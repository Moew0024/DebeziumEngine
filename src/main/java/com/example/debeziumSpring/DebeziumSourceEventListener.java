package com.example.debeziumSpring;

import io.debezium.config.Configuration;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.connect.source.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import io.debezium.embedded.Connect;
import org.apache.kafka.connect.data.Struct;
@Service
public class DebeziumSourceEventListener {
  private final Logger logger = LoggerFactory.getLogger(DebeziumSourceEventListener.class);
  private final Executor executor = Executors.newFixedThreadPool(5);
  private final DebeziumEngine<RecordChangeEvent<SourceRecord>> engine;

  private class MyChangeConsumer implements DebeziumEngine.ChangeConsumer<RecordChangeEvent<SourceRecord>> {
    @Override
    public void handleBatch(List<RecordChangeEvent<SourceRecord>> records, DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> committer) throws InterruptedException {
       executor.execute(new HandleEvents(records));
    }
  }
  public class HandleEvents implements Runnable {
    public List<RecordChangeEvent<SourceRecord>> records;
    public HandleEvents(List<RecordChangeEvent<SourceRecord>> records) {
      this.records = records;
    }
    @Override
    public void run() {
      System.out.println(Thread.currentThread().getName() + " starting process");
      records.forEach(record -> {
        handleChangeEvent(record);
      });
      System.out.println(Thread.currentThread().getName() + " finished process");
    }
  }
  @Autowired
  private JdbcService jdbcService;

  public DebeziumSourceEventListener(Configuration customerConnector) {
    this.engine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
      .using(customerConnector.asProperties())
      .notifying(new MyChangeConsumer())
      .build();
  }
  private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
    SourceRecord record = sourceRecordRecordChangeEvent.record();

    // get messageType INSERT UPDATE DELETE
    String sourceOffset = record.sourceOffset().toString();
    String sourceOffsetTemp = sourceOffset.substring(1, sourceOffset.length()-1);
    List<String> results = Arrays.stream(sourceOffsetTemp.split(",")).collect(Collectors.toList());
    List<String> results2 = Arrays.stream(results.get(2).split("=")).collect(Collectors.toList());
    String messageType = results2.get(1);
    logger.info("This is message type: " + messageType);

    Struct sourceRecordValue = (Struct)record.value();
    Struct sourceRecordKey = (Struct)record.key();
    logger.info("Record:..." + record.toString());
    logger.info("String source offset: ..." + record.sourceOffset().toString());
    if (messageType.equalsIgnoreCase("INSERT")) {
      // get payload
      String id = sourceRecordValue.getStruct("after").get("id").toString();
      String name = sourceRecordValue.getStruct("after").get("name").toString();
      String pass = sourceRecordValue.getStruct("after").get("pass").toString();
      logger.info(id + " " + name + " " + pass);
      jdbcService.insert(id, name, pass);
    } else if (messageType.equalsIgnoreCase("UPDATE")) {
      // get payload
      String id = sourceRecordValue.getStruct("after").get("id").toString();
      String name = sourceRecordValue.getStruct("after").get("name").toString();
      String pass = sourceRecordValue.getStruct("after").get("pass").toString();
      logger.info(id + " " + name + " " + pass);
      jdbcService.update(id, name, pass);
    } else if (messageType.equalsIgnoreCase("DELETE")) {
      //get id to delete
      String id = sourceRecordKey.get("id").toString();
      jdbcService.delete(id);
    }

//    logger.info("Key: " + sourceRecordKey.get("id").toString());
//    logger.info("Value before: " + sourceRecordValue.getStruct("before"));
//    logger.info("Value after:  " + sourceRecordValue.getStruct("after"));
  }

  @PostConstruct
  private void start() {
    this.executor.execute(engine);
  }
  @PreDestroy
  private void stop() throws IOException {
    if (this.engine != null) {
      this.engine.close();
    }
  }
}
