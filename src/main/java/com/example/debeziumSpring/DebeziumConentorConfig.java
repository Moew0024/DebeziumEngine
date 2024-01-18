package com.example.debeziumSpring;

import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;


@Configuration
public class DebeziumConentorConfig {
  private final Logger logger = LoggerFactory.getLogger(DebeziumConentorConfig.class);
  @Bean
  public io.debezium.config.Configuration customConnector() throws IOException {
    return io.debezium.config.Configuration.create()
      .with("name", "customer-postgres-connector")
      .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
      .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
      .with("offset.storage.file.filename", "/tmp/offset1.dat")
      .with("offset.flush.interval.ms", "10")
      .with("database.hostname", "localhost")
      .with("database.port", "5432")
      .with("database.user", "user1")
      .with("database.password", "24102003")
      .with("database.dbname", "kdbtest")
      .with("topic.prefix", "prefix24")
      .with("tasks.max", 1)
      .with("plugin.name", "pgoutput")
            .with("max.batch.size", 2)
            .with("max.queue.size", 4)
      .build();
  }
}
