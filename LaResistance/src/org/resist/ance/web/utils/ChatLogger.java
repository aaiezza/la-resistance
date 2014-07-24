package org.resist.ance.web.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import javafx.util.Pair;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

/**
 * @author Alex Aiezza
 */
@Service ( "chatLogger" )
@ManagedResource
public class ChatLogger implements Observer
{
    private static final String           CHAT_FORMAT = "%s %s: %s";

    private static final int              CHAT_CAP    = 100;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
                                                              "([MM-dd-yyyy] HH:mm:ss)" );
    private LinkedHashMap<Long, String>   log;

    private long                          lastUpdate;

    public ChatLogger()
    {
        log = new LinkedHashMap<Long, String>();
    }

    public synchronized void say( String username, String message )
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

        notifyAll();
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

    public Stack<String> messagesSince( long when )
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

    public synchronized Stack<String> getAllMessages()
    {
        return messagesSince( 0 );
    }

    @Override
    public void update( Observable o, Object arg )
    {
        if ( o instanceof UserTracker && arg instanceof Pair<?, ?> )
        {
            if ( (boolean) ( (Pair<?, ?>) arg ).getKey() )
            {
                say(
                    "::",
                    String.format( "%s is ONLINE!",
                        ( (ShabaUser) ( (Pair<?, ?>) arg ).getValue() ).getUsername() ) );
            } else
            {
                say(
                    "::",
                    String.format( "%s has LEFT!",
                        ( (ShabaUser) ( (Pair<?, ?>) arg ).getValue() ).getUsername() ) );
            }
        }

    }
}
