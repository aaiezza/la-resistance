package org.resist.ance.web.utils;

import javax.servlet.http.HttpSessionEvent;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
public class ShabaHttpSessionListener extends HttpSessionEventPublisher implements
        ApplicationContextAware
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

    /**
     * @see org.springframework.security.web.session.HttpSessionEventPublisher#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionDestroyed( HttpSessionEvent event )
    {
        super.sessionDestroyed( event );
        Authentication auth = ( (SecurityContext) event.getSession().getAttribute(
            "SPRING_SECURITY_CONTEXT" ) ).getAuthentication();
        USER_TRACKER.removeUser( USER_MAN.loadShabaUserByUsername( ( (UserDetails) auth
                .getPrincipal() ).getUsername() ) );
    }


}
