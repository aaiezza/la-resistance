package org.resistance.configuration;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.resistance.site.web.ChatController;
import org.resistance.site.web.LoginController;
import org.resistance.site.web.ResistanceController;
import org.resistance.site.web.UserController;
import org.resistance.site.web.utils.UserTracker;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfiguration {
  @Bean("Login_Logger")
  public Log loginLogger() {
    return LogFactory.getLog(LoginController.class);
  }

  @Bean("UserTracker_Logger")
  public Log userTrackerLogger() {
    return LogFactory.getLog(UserTracker.class);
  }

  @Bean("GameLobby_Logger")
  public Log gameLobbyLogger() {
    return LogFactory.getLog(UserTracker.class);
  }

  @Bean("Resistance_Logger")
  public Log resistanceLogger() {
    return LogFactory.getLog(ResistanceController.class);
  }

  @Bean("User_Logger")
  public Log userLogger() {
    return LogFactory.getLog(UserController.class);
  }

  @Bean("Chat_Logger")
  public Log chatLogger() {
    return LogFactory.getLog(ChatController.class);
  }
  
//  @Bean
//  public SimpMessagingTemplate simplMessagingTemplate() {
//    return null;
//  }

  @Bean
  public DataSource dataSource()
  {
      DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
      dataSourceBuilder.driverClassName("org.sqlite.JDBC");
      dataSourceBuilder.url("jdbc:sqlite:src/main/resources/sqlite_resist/resist_members.db");
      dataSourceBuilder.username("root");
      dataSourceBuilder.password("");
      return dataSourceBuilder.build();
  }

//  @Bean
//  public JdbcTemplate jdbcTemplate() throws IllegalAccessException, InvocationTargetException, InstantiationException {
//    // extract this 4 parameters using your own logic
//    final String driverClassName = "org.sqlite.JDBC";
//    final String jdbcUrl = "jdbc:sqlite:resources/sqlite_resist/resist_members.db";
//    final String username = "root";
//    final String password = "";
//    final DataSource dataSource = DataSourceBuilder.create().driverClassName(driverClassName).url(jdbcUrl)
//        .username(username).password(password).build();
//    // and make the jdbcTemplate
//    return new JdbcTemplate(dataSource);
//  }
}
