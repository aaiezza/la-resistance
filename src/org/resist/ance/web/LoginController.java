package org.resist.ance.web;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    static final String             JUST_JOINING_US = "just_joining_us";

    private final ArrayList<Authentication> PLAYERS_ONLINE;

    private final Log                       LOGGER;

    @Autowired
    public LoginController(
        @Qualifier ( "Login_Logger" ) Log logger,
        @Qualifier ( "OnlinePlayerList" ) ArrayList<Authentication> playersOnline )
    {
        LOGGER = logger;
        PLAYERS_ONLINE = playersOnline;
    }

    @RequestMapping ( "/*" )
    public ModelAndView backToYourRoots()
    {
        return new ModelAndView( "redirect:login" );
    }

    @RequestMapping ( "login" )
    public ModelAndView getLoginForm(
            @RequestParam ( required = false ) String authfailed,
            String logout,
            HttpServletRequest request,
            HttpSession session )
    {
        if ( session.getAttribute( JUST_JOINING_US ) != null )
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

        String referer = request.getHeader( "referer" );

        if ( referer != null && referer.endsWith( "/results" ) )
        {
            LOGGER.debug( "Trying to access results without logging in..." );
        } else
        {
            LOGGER.info( message.isEmpty() ? "Waiting For Login" : message );
        }

        map.put( "message", message );

        return new ModelAndView( "login", map );
    }

    @RequestMapping ( "profile" )
    public ModelAndView getProfilePage( HttpSession session )
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        HashMap<String, Object> map = new HashMap<String, Object>();

        Boolean justJoiningUs = (Boolean) session.getAttribute( JUST_JOINING_US );

        if ( justJoiningUs == null )
        {
            session.setAttribute( JUST_JOINING_US, true );
            LOGGER.info( String.format( "%s successfully Logged in!", auth.getName() ) );

            if ( !PLAYERS_ONLINE.contains( auth ) )
            {
                PLAYERS_ONLINE.add( auth );
            }
        }

        map.put( "username", auth.getName() );

        ArrayList<String> authorities = getUserAuthorities( auth );

        map.put( "admin", authorities.contains( "ROLE_ADMIN" ) );
        map.put( "user", authorities.contains( "ROLE_USER" ) );

        return new ModelAndView( "profile", map );
    }

    private final ArrayList<String> getUserAuthorities( Authentication auth )
    {
        ArrayList<String> auths = new ArrayList<String>();

        auth.getAuthorities().forEach( ( ga ) -> auths.add( ga.getAuthority() ) );

        return auths;
    }
}
