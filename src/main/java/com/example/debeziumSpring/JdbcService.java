package com.example.debeziumSpring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class JdbcService {
  @Autowired
  private DataSource dataSource;
  private JdbcTemplate jdbcTemplate;
  public JdbcService(DataSource dataSource) {
    this.dataSource = dataSource;
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public void insert(String id, String name, String pass) {
    Integer idInt = Integer.parseInt(id);
    this.jdbcTemplate.update(
      "INSERT INTO accounts(id, name, pass) VALUES(?, ?, ?)",
      idInt, name, pass
    );
  }
  public void update(String id, String name, String pass) {
    Integer idInt = Integer.parseInt(id);
    this.jdbcTemplate.update(
      "UPDATE accounts SET name = ?, pass = ? WHERE id = ?",
      name, pass, idInt
    );
  }
  public void delete(String id) {
    Integer idInt = Integer.parseInt(id);
    this.jdbcTemplate.update(
      "DELETE FROM accounts WHERE id = ?",
      idInt
    );
  }
}
