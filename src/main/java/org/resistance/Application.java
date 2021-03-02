package org.resistance;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.resistance.configuration.SecurityConfiguration;
import org.resistance.configuration.WebConfig;
import org.resistance.site.property.MultiPropertyLoaderListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@SpringBootApplication
@lombok.RequiredArgsConstructor(onConstructor_ = @Autowired)
public class Application extends SpringBootServletInitializer {
  private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

  @Override
  public void onStartup(final ServletContext container) throws ServletException {
    super.onStartup(container);

    System.out.println("Hello");

    final AnnotationConfigWebApplicationContext rootContext = defaultServlet();

    rootContext.register(WebConfig.class);

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

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
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

  @PostConstruct
  public void init() {
    requestMappingHandlerAdapter.setSynchronizeOnSession(true);
  }
}
