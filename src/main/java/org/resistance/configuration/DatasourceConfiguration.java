package org.resistance.configuration;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasourceConfiguration {
  @Bean
  public DataSource dataSource() {
    DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.driverClassName("org.sqlite.JDBC");
    dataSourceBuilder.url("jdbc:sqlite:src/main/resources/sqlite_resist/resist_members.db");
    dataSourceBuilder.username("root");
    dataSourceBuilder.password("");
    return dataSourceBuilder.build();
  }
}
