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
      .with("offset.storage.file.filename", "/tmp/offsetsPostgres.dat")
      .with("offset.flush.interval.ms", "10")
      .with("database.hostname", "localhost")
      .with("database.port", "5432")
      .with("database.user", "user1")
      .with("database.password", "24102003")
      .with("database.dbname", "kdbtest")
      .with("topic.prefix", "postgresPrefixEnginedebe")
      .with("include.schema.changes", "true")
      .with("database.server.id", "10181")
      .with("tasks.max", 1)
      .with("plugin.name", "pgoutput")
      .with("slot.name", "myslottttt")
      .with("database.server.name", "customer-oracle-db-server")
      .with("debezium.source.schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
      .with("debezium.source.schema.history.internal.file.filename", "/tmp/dbezeHistory.dat")
      .with("schema.history.internal.kafka.topic", "Schema-change.postgresdbengine")
      .with("schema.history.internal.kafka.bootstrap.servers", "localhost:9092")
      .build();
  }
}
