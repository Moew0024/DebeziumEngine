package com.example.debeziumSpring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.example.debeziumSpring")
@PropertySource("classpath:database.properties")
public class DataSourceConfig {
  @Autowired
  private Environment environment;

  private final String URL = "url";
  private final String USERNAME = "username";
  private final String PASSWORD = "password";
  private final String DRIVER = "driver";

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/kdbtest");
    dataSource.setUsername("user1");
    dataSource.setPassword("24102003");
    return dataSource;
  }
}
