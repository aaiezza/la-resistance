package org.resistance.site.web.utils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author Alex Aiezza
 */
@Service ( "chatLogger" )
@ManagedResource
public class ChatLogger extends DeferredResponder<String, Long>
{
    private static final String           CHAT_FORMAT = "%s %s: %s";

    private static final int              CHAT_CAP    = 100;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
                                                              "([MM-dd-yyyy] HH:mm:ss)" );

    private Map<Long, String>             log;

    private long                          lastUpdate;

    public ChatLogger()
    {
        super( LogFactory.getLog( ChatLogger.class ) );
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

        sendResults();
    }

    @Override
    protected void doBeforeSendingSingleResult(
            DeferredResult<List<String>> deferredResult,
            Pair<ShabaUser, Long> userAndRestrictor )
    {
        LOGGER.debug( String.format( "\n\t\tThe wait is OVER for %s", userAndRestrictor.getKey()
                .getUsername() ) );
    }

    @Override
    protected List<String> getResult( Long resultRestrictor )
    {
        return messagesSince( resultRestrictor );
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
            if ( entry.getKey() > when )
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

    void systemUpdate( String message, Object... args )
    {
        say( "::", String.format( message, args ) );
    }
}
