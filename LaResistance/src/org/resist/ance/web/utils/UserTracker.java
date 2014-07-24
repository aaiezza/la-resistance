package org.resist.ance.web.utils;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

import javafx.util.Pair;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * @author Alex Aiezza
 */
@Service
public class UserTracker extends Observable
{
    private static final String        LOGGED_IN  = "%s successfully Logged in!";

    private static final String        LOGGED_OUT = "%s has Logged out!";

    private final ArrayList<ShabaUser> onlineUsers;

    private final Log                  LOGGER;

    @Autowired
    private UserTracker( @Qualifier ( "UserTracker_Logger" ) Log loginLogger )
    {
        onlineUsers = new ArrayList<ShabaUser>();
        LOGGER = loginLogger;
    }

    synchronized void addUser( final ShabaUser user )
    {
        if ( !onlineUsers.contains( user ) )
        {
            onlineUsers.add( user );
            LOGGER.info( format( LOGGED_IN, user ) );
            setChanged();
            notifyObservers( new Pair<Boolean, ShabaUser>( true, user ) );
        }
    }

    synchronized void removeUser( final ShabaUser user )
    {
        onlineUsers.remove( user );
        LOGGER.info( format( LOGGED_OUT, user ) );
        setChanged();
        notifyObservers( new Pair<Boolean, ShabaUser>( false, user ) );
    }

    public synchronized Collection<ShabaUser> getLoggedInUsers()
    {
        return onlineUsers;
    }

    public synchronized boolean contains( UserDetails user )
    {
        if ( user == null )
        {
            return false;
        }
        return onlineUsers.contains( user );
    }

}
