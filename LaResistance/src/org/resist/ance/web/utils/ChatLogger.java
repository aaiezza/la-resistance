package org.resist.ance.web.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ChatLogger
{
    private static final String           CHAT_FORMAT = "%s %s: %s";

    private static final int              CHAT_CAP    = 100;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
                                                              "([MM-dd-yyyy] HH:mm:ss)" );

    private LinkedHashMap<Long, String>   log;

    private Object                        lock;

    private long                          lastUpdate;

    @Autowired
    public ChatLogger( @Qualifier ( "threadLock" ) Object lock )
    {
        log = new LinkedHashMap<Long, String>();
        this.lock = lock;
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

        synchronized ( lock )
        {
            lock.notifyAll();
        }
    }

    public synchronized String lastMessage()
    {
        return log.get( lastUpdate );
    }

    public synchronized long lastUpdate()
    {
        return lastUpdate;
    }

    public void clearLog()
    {
        log.clear();
        lastUpdate = 0;
        synchronized ( lock )
        {
            lock.notifyAll();
        }
    }

    public synchronized Stack<String> messagesSince( long when )
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
}
