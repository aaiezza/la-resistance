package org.resist.ance.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.resist.ance.web.utils.ChatLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatController
{
    private final Object lock;

    private ChatLogger   CHAT_LOG;

    private Log          LOGGER;

    @Autowired
    private ChatController(
        @Qualifier ( "Chat_Logger" ) Log logger,
        ChatLogger chatLog,
        @Qualifier ( "threadLock" ) Object lock )
    {
        LOGGER = logger;
        CHAT_LOG = chatLog;
        this.lock = lock;
    }

    @RequestMapping ( method = POST, value = "sayIt", headers = { "say" } )
    @ResponseBody
    public ResponseEntity<String> updateChat( @RequestHeader ( "say" ) String sayIt )
    {
        String username = UserController.getUserDetails().getUsername();

        CHAT_LOG.say( username, sayIt );

        LOGGER.info( CHAT_LOG.lastMessage() );

        return new ResponseEntity<String>( HttpStatus.OK );
    }

    @RequestMapping ( method = GET, value = "updateChat", params = { "lastUpdate" } )
    public void getChatLog(
            @RequestParam ( "lastUpdate" ) long lastUpdate,
            HttpServletResponse response ) throws IOException, InterruptedException
    {
        synchronized ( lock )
        {
            lock.wait();
        }

        PrintWriter out = response.getWriter();

        for ( String line : CHAT_LOG.messagesSince( lastUpdate ) )
        {
            out.println( line );
        }
    }
}
