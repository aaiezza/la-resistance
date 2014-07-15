package org.resist.ance.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.resist.ance.web.utils.ChatLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Alex Aiezza
 */
@Controller
public class ChatController
{
    private ChatLogger CHAT_LOG;

    private Log        LOGGER;

    @Autowired
    private ChatController( @Qualifier ( "Chat_Logger" ) Log logger, ChatLogger chatLog )
    {
        LOGGER = logger;
        CHAT_LOG = chatLog;
    }

    @RequestMapping ( method = GET, value = "serverTime" )
    public void getServerTime( HttpServletResponse response ) throws IOException
    {
        PrintWriter out = response.getWriter();
        long time = System.currentTimeMillis();
        out.print( time );
        LOGGER.debug( "TIME = " + time );
    }

    @RequestMapping (
        method = POST,
        value = "sayIt",
        headers = { "say" },
        produces = "application/json" )
    @ResponseBody
    public HashMap<String, Object> updateChat(
            @RequestHeader ( "say" ) String sayIt,
            HttpServletResponse response ) throws IOException
    {
        HashMap<String, Object> map = new HashMap<String, Object>();

        UserDetails userDetails = UserController.getUserDetails();

        // IF you're an ADMIN you get some fun tools here!
        if ( userDetails.getAuthorities().toString().contains( "ROLE_ADMIN" ) )
        {
            switch ( sayIt )
            {
            case "/clear":
                CHAT_LOG.clearLog();
                map.put( "messages", new String [] { "Chat Log Cleared!" } );
                break;
            case "/all":
                map.put( "messages", CHAT_LOG.getAllMessages() );
                break;
            default:
                CHAT_LOG.say( userDetails.getUsername(), sayIt );
                LOGGER.info( CHAT_LOG.lastMessage() );
            }
        } else
        {
            CHAT_LOG.say( userDetails.getUsername(), sayIt );
            LOGGER.info( CHAT_LOG.lastMessage() );
        }

        response.setStatus( HttpStatus.OK.value() );

        return map;
    }

    @RequestMapping ( method = GET, value = "updateChat", params = { "lastUpdate" } )
    public void getChatLog(
            @RequestParam ( "lastUpdate" ) long lastUpdate,
            HttpServletResponse response ) throws IOException, InterruptedException
    {
        UserDetails userDetails = UserController.getUserDetails();

        synchronized ( CHAT_LOG )
        {
            LOGGER.debug( String.format( "\n\t\t%s is waiting for the chat to update!!!",
                userDetails.getUsername() ) );
            CHAT_LOG.wait();
        }

        LOGGER.debug( String.format( "\n\t\tThe wait is OVER for %s", userDetails.getUsername() ) );

        PrintWriter out = response.getWriter();

        for ( String line : CHAT_LOG.messagesSince( lastUpdate ) )
        {
            out.println( line );
        }
    }
}
