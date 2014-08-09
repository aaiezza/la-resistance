package org.resistance.site.web.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
public class ShabaHttpSessionListener extends HttpSessionEventPublisher implements
        ApplicationContextAware, ApplicationListener<SessionDestroyedEvent>
{
    private final ShabaJdbcUserDetailsManager USER_MAN;

    private final UserTracker                 USER_TRACKER;

    @Autowired
    private ShabaHttpSessionListener(
        UserTracker userTracker,
        ShabaJdbcUserDetailsManager userManager )
    {
        USER_TRACKER = userTracker;
        USER_MAN = userManager;
    }

    @Override
    public void setApplicationContext( ApplicationContext applicationContext )
            throws BeansException
    {
        if ( applicationContext instanceof WebApplicationContext )
        {
            ( (WebApplicationContext) applicationContext ).getServletContext().addListener( this );
        } else
        {
            // Either throw an exception or fail gracefully, up to you
            throw new RuntimeException( "Must be inside a web application context" );
        }
    }

    @Override
    public void onApplicationEvent( SessionDestroyedEvent event )
    {
        for ( SecurityContext securityContext : event.getSecurityContexts() )
        {
            UserDetails ud = (UserDetails) securityContext.getAuthentication().getPrincipal();

            if ( USER_TRACKER.contains( ud ) )
            {
                ShabaUser user = USER_MAN.loadShabaUserByUsername( ud.getUsername() );

                if ( user != null )
                    USER_TRACKER.removeUser( user );
            }
        }
    }
}
