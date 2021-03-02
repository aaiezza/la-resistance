package org.resistance.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("org.resist")
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/resources/**")
        .addResourceLocations("/resources/", "/resources/templates/", "/resources/static/");
  }

  @Override
  public void configureViewResolvers(final ViewResolverRegistry registry) {
    registry.jsp("/resources/templates/jsp/", ".jsp");
    registry.jsp("/resources/jsp/", ".jsp");
    registry.jsp("/jsp/", ".jsp");
    registry.jsp();
  }
}
