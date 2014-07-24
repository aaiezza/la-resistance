package org.resist.ance.web;

import static org.resist.ance.web.utils.ShabaJdbcUserDetailsManager.ADMIN;
import static org.resist.ance.web.utils.ShabaJdbcUserDetailsManager.USER;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.resist.ance.web.utils.ShabaJdbcUserDetailsManager;
import org.resist.ance.web.utils.ShabaUser;
import org.resist.ance.web.utils.UserTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alex Aiezza
 */
@Controller
public class LoginController
{
    private final UserTracker                 USER_TRACKER;

    private final ShabaJdbcUserDetailsManager USER_MAN;

    private final Log                         LOGGER;

    @Autowired
    public LoginController(
        @Qualifier ( "Login_Logger" ) Log logger,
        UserTracker userTracker,
        ShabaJdbcUserDetailsManager userManager )
    {
        LOGGER = logger;
        USER_TRACKER = userTracker;
        USER_MAN = userManager;
    }

    @RequestMapping ( "/*" )
    public ModelAndView backToYourRoots()
    {
        return new ModelAndView( "redirect:login" );
    }

    @RequestMapping ( "login" )
    public ModelAndView getLoginForm(
            @RequestParam ( required = false ) String authfailed,
            String logout )
    {
        if ( USER_TRACKER.contains( USER_MAN.getShabaUser() ) )
        {
            return new ModelAndView( "redirect:profile" );
        }

        HashMap<String, Object> map = new HashMap<String, Object>();

        String message = "";
        if ( authfailed != null )
        {
            message = "Invalid username or password, try again!";
            map.put( "success", false );
        } else if ( logout != null )
        {
            message = "Logged Out successfully!";
            map.put( "success", true );
        }

        if ( message.isEmpty() )
        {
            LOGGER.debug( "Waiting For Login" );
        }

        map.put( "message", message );

        return new ModelAndView( "login", map );
    }

    @RequestMapping ( "profile" )
    public ModelAndView getProfilePage()
    {
        ShabaUser user = USER_MAN.getShabaUser();

        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put( "username", user.getUsername() );

        map.put( "admin", user.getAuthorities().contains( ADMIN ) );
        map.put( "user", user.getAuthorities().contains( USER ) );

        return new ModelAndView( "profile", map );
    }
}
