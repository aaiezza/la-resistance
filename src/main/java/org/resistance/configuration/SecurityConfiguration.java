package org.resistance.configuration;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired private DataSource dataSource;

  @Override
  public void configure(final WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/resources/**");
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    final JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
    userDetailsManager.setAuthoritiesByUsernameQuery(
        "SELECT username, role FROM user_role WHERE username = ?  ");
    userDetailsManager.setUsersByUsernameQuery(
        "SELECT username, password, enabled FROM users WHERE username = ?");

    auth.apply(
        new JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder>(userDetailsManager));
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/css/**", "/js/**", "/images/**", "/login*", "/signup*", "/vote*")
        .permitAll()
        .antMatchers("/results*", "/userManagement*")
        .hasRole("ADMIN")
        .antMatchers("/profile*", "/**")
        .hasRole("USER")
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .usernameParameter("username")
        .passwordParameter("password")
        .defaultSuccessUrl("/profile", true)
        .failureUrl("/login?authfailed")
        .permitAll()
        .and()
        .logout()
        .logoutUrl("/login?logout")
        .permitAll()
        .deleteCookies("JSESSIONID");
  }

  @Bean
  public AuthenticationManager authMan() throws Exception {
    return this.authenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Configuration
  public static class WC extends AbstractSecurityWebApplicationInitializer {}
}
