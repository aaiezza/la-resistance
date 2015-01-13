package org.resistance.site.web.chat;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.logging.LogFactory;
import org.resistance.site.web.utils.MessageRelayer;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

/**
 * @author Alex Aiezza
 */
@Service ( "chatLogger" )
@ManagedResource
public class ChatLogger extends MessageRelayer<List<String>>
{
    private static final String           USER_ENTERED_CHAT = "[%s] has entered Resistance Lobby";

    public static final String            RELAY_DESTINATION = "/topic/chat";

    public static final String            SUBSCRIPTION_URL  = "/chat";

    private static final String           CHAT_FORMAT       = "%s %s: %s";

    private static final int              CHAT_CAP          = 100;

    private static final SimpleDateFormat DATE_FORMAT       = new SimpleDateFormat(
                                                                    "([MM-dd-yyyy] HH:mm:ss)" );

    private Map<Long, String>             log;

    private long                          lastUpdate;

    public ChatLogger()
    {
        super( LogFactory.getLog( ChatLogger.class ), RELAY_DESTINATION );
        log = Collections.synchronizedMap( new LinkedHashMap<Long, String>() );

    }

    public void say( String username, String message )
    {
        lastUpdate = System.currentTimeMillis();

        log.put( lastUpdate, String.format( CHAT_FORMAT,
            DATE_FORMAT.format( new Date( lastUpdate ) ), username, message ) );

        if ( log.size() > CHAT_CAP )
        {
            for ( long time : log.keySet() )
            {
                log.remove( time );
                break;
            }
        }

        broadcastPayload();
    }

    @Override
    protected List<String> getPayload()
    {
        long currentTime = System.currentTimeMillis() - 100;

        return messagesSince( currentTime );
    }

    @Override
    public synchronized List<String> onSubscription( ShabaUser user )
    {
        systemUpdate( USER_ENTERED_CHAT, user );
        return onSubscription( user, NO_UPDATE );
    }

    public synchronized String lastMessage()
    {
        return log.get( lastUpdate );
    }

    public synchronized long lastUpdate()
    {
        return lastUpdate;
    }

    @ManagedOperation ( description = "clear the log" )
    public void clearLog()
    {
        log.clear();
        lastUpdate = 0;
    }

    public synchronized Stack<String> messagesSince( Long when )
    {
        Stack<String> messages = new Stack<String>();

        for ( Entry<Long, String> entry : log.entrySet() )
        {
            if ( entry.getKey() >= when )
            {
                messages.push( entry.getValue() );
            }
        }

        return messages;
    }

    @ManagedOperation ( description = "get the whole log" )
    public synchronized Stack<String> getAllMessages()
    {
        return messagesSince( 0L );
    }

    public void systemUpdate( String message, Object... args )
    {
        systemUpdate( String.format( message, args ) );
    }

    @ManagedOperation ( description = "Automatically formats message input" )
    @ManagedOperationParameters ( { @ManagedOperationParameter (
        name = "message",
        description = "The message to update the chat log with.\n(Given String will be formatted)" ) } )
    public void systemUpdate( String message )
    {
        say( "::", message );
    }

}
