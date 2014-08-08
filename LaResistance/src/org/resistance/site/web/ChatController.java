package org.resistance.site.web;

import static org.resistance.site.web.utils.ShabaJdbcUserDetailsManager.ADMIN;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.resistance.site.web.chat.ChatLogger;
import org.resistance.site.web.chat.NaughtyWordTransformer;
import org.resistance.site.web.utils.ShabaJdbcUserDetailsManager;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * @author Alex Aiezza
 */
@Controller
public class ChatController
{
    private final ShabaJdbcUserDetailsManager USER_MAN;

    private final ChatLogger                  CHAT_LOG;

    private final Log                         LOGGER;

    private final SimpMessagingTemplate       TEMPLATE;

    private final NaughtyWordTransformer      NAUGHTY_CONTROL;

    @Autowired
    private ChatController(
        @Qualifier ( "Chat_Logger" ) Log logger,
        ChatLogger chatLog,
        ShabaJdbcUserDetailsManager userManager,
        SimpMessagingTemplate template )
    {
        LOGGER = logger;
        CHAT_LOG = chatLog;
        USER_MAN = userManager;
        TEMPLATE = template;
        NAUGHTY_CONTROL = new NaughtyWordTransformer();
        CHAT_LOG.setTemplate( template );
    }

    @MessageMapping ( "say" )
    public void updateChat( @Payload String sayIt, Principal principle ) throws MessagingException,
            IOException, InterruptedException
    {
        HashMap<String, Object> map = new HashMap<String, Object>();

        ShabaUser user = USER_MAN.loadShabaUserByUsername( principle.getName() );

        // IF you're an ADMIN you get some fun tools here!
        if ( user.getAuthorities().contains( ADMIN ) )
        {
            switch ( sayIt )
            {
            case "/clear":
                CHAT_LOG.clearLog();
                map.put( "messages", new String [] { "Chat Log Cleared!" } );
                TEMPLATE.convertAndSendToUser( user.getUsername(), "/queue/chatSpecial", map );
                break;
            case "/all":
                map.put( "messages", CHAT_LOG.getAllMessages() );
                TEMPLATE.convertAndSendToUser( user.getUsername(), "/queue/chatSpecial", map );
                break;
            default:
                CHAT_LOG.say( user.getUsername(), NAUGHTY_CONTROL.makeNice( sayIt, true ) );
                LOGGER.info( CHAT_LOG.lastMessage() );
            }
        } else
        {
            CHAT_LOG.say( user.getUsername(), NAUGHTY_CONTROL.makeNice( sayIt, true ) );
            LOGGER.info( CHAT_LOG.lastMessage() );
        }
    }

    @SubscribeMapping ( ChatLogger.SUBSCRIPTION_URL )
    public void subscribeToChat( Principal principal )
    {
        CHAT_LOG.onSubscription( USER_MAN.loadShabaUserByUsername( principal.getName() ) );
    }
}
