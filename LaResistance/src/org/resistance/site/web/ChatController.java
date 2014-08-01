package org.resistance.site.web;

import static org.resistance.site.web.utils.ShabaJdbcUserDetailsManager.ADMIN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.resistance.site.web.utils.ChatLogger;
import org.resistance.site.web.utils.ShabaJdbcUserDetailsManager;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author Alex Aiezza
 */
@Controller
public class ChatController
{
    private final ShabaJdbcUserDetailsManager USER_MAN;

    private final ChatLogger                  CHAT_LOG;

    private final Log                         LOGGER;

    @Autowired
    private ChatController(
        @Qualifier ( "Chat_Logger" ) Log logger,
        ChatLogger chatLog,
        ShabaJdbcUserDetailsManager userManager )
    {
        LOGGER = logger;
        CHAT_LOG = chatLog;
        USER_MAN = userManager;
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

        ShabaUser user = USER_MAN.getShabaUser();

        // IF you're an ADMIN you get some fun tools here!
        if ( user.getAuthorities().contains( ADMIN ) )
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
                CHAT_LOG.say( user.getUsername(), sayIt );
                LOGGER.info( CHAT_LOG.lastMessage() );
            }
        } else
        {
            CHAT_LOG.say( user.getUsername(), sayIt );

            LOGGER.info( CHAT_LOG.lastMessage() );
        }

        response.setStatus( HttpStatus.OK.value() );

        return map;
    }

    @RequestMapping ( method = GET, value = "updateChat" )
    @ResponseBody
    public DeferredResult<List<String>> getChatLog() throws IOException, InterruptedException
    {
        long lastUpdate = System.currentTimeMillis();

        ShabaUser user = USER_MAN.getShabaUser();

        return CHAT_LOG.registerRequest( user, lastUpdate );
    }
}
