package org.resist.ance.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController
{
    private final Log LOGGER;

    @Autowired
    public LoginController( @Qualifier ( "Login_Logger" ) Log logger )
    {
        LOGGER = logger;
    }

    @RequestMapping ( "login" )
    public ModelAndView getLoginForm(
            @RequestParam ( required = false ) String authfailed,
            String logout,
            HttpServletRequest request )
    {
        String message = "";
        if ( authfailed != null )
        {
            message = "Invalid username of password, try again!";
        } else if ( logout != null )
        {
            message = "Logged Out successfully!";
        }

        String referer = request.getHeader( "referer" );

        if ( referer != null && referer.endsWith( "/results" ) )
        {
            LOGGER.debug( "Trying to access results without logging in..." );
        } else
        {
            LOGGER.info( message.isEmpty() ? "Waiting For Login" : message );
        }

        return new ModelAndView( "login", "message", message );
    }

    @RequestMapping ( "profile" )
    public ModelAndView getProfilePage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        LOGGER.info( String.format( "%s successfully Logged in!", auth.getName() ) );
        return new ModelAndView( "profile", "username", auth.getName() );
    }

    @RequestMapping ( "signup" )
    public ModelAndView getSignupPage()
    {
        LOGGER.info( "FRESH MEAT" );
        return new ModelAndView( "signup" );
    }

}
