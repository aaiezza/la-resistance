package org.resist.ance;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@EnableWebMvc
@EnableWebSecurity
@WebAppConfiguration
@SpringBootApplication
@ComponentScan
@PropertySource("classpath:laResistance.properties")
public class Application {
  @Autowired private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public DataSource getDataSource() {
    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/shaba_members");
    dataSource.setUsername("root");
    dataSource.setPassword("");
    return dataSource;
  }

  @PostConstruct
  public void init() {
    requestMappingHandlerAdapter.setSynchronizeOnSession(true);
  }
}
