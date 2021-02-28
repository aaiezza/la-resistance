package org.resist.ance.configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.resist.ance.property.MultiPropertyLoaderListener;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class MyWebAppInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(final ServletContext container) throws ServletException {
    final AnnotationConfigWebApplicationContext rootContext = defaultServlet();

    container.addListener(new ContextLoaderListener(rootContext));
    container.addListener(MultiPropertyLoaderListener.class);

    rootContext.register(SecurityConfiguration.class);
    container
        .addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain"))
        .addMappingForUrlPatterns(null, false, "/*");

    final ServletRegistration.Dynamic rootDispatcher =
        container.addServlet("default", new DispatcherServlet(rootContext));

    rootDispatcher.addMapping("/js/*", "/images/*", "/css/*", "/resources/*");
    rootDispatcher.setLoadOnStartup(1);

    final ServletRegistration.Dynamic resistanceDispatcher =
        container.addServlet("Resistance-dispatcher", new DispatcherServlet(resistanceServlet()));

    resistanceDispatcher.addMapping("*.pdf", "*.txt", "/");
    resistanceDispatcher.setLoadOnStartup(1);
  }

  private AnnotationConfigWebApplicationContext defaultServlet() {
    final AnnotationConfigWebApplicationContext context =
        new AnnotationConfigWebApplicationContext();

    context.setDisplayName("LaResistance");
    // context.setConfigLocation("classpath:/WEB-INF/spring/root-context.xml");

    return context;
  }

  private AnnotationConfigWebApplicationContext resistanceServlet() {
    final AnnotationConfigWebApplicationContext context =
        new AnnotationConfigWebApplicationContext();

    // context.setConfigLocation("classpath:/WEB-INF/spring/ResistanceServlet/servlet-context.xml");

    return context;
  }
}
