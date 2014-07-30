package org.resistance.site.web.utils;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author Alex Aiezza
 */
@Service
@ManagedResource
public class UserTracker extends DeferredResponder<ShabaUser, Boolean>
{
    private static final String   LOGGED_IN  = "%s successfully Logged in!";

    private static final String   LOGGED_OUT = "%s has Logged out!";

    private final List<ShabaUser> onlineUsers;

    @Autowired
    private UserTracker( @Qualifier ( "UserTracker_Logger" ) Log loginLogger )
    {
        super( loginLogger );
        onlineUsers = Collections.synchronizedList( new ArrayList<ShabaUser>() );
    }

    synchronized void addUser( final ShabaUser user )
    {
        if ( !onlineUsers.contains( user ) )
        {
            onlineUsers.add( user );
            LOGGER.info( format( LOGGED_IN, user ) );
            setChanged();
            notifyObservers( new Pair<Boolean, ShabaUser>( true, user ) );
            sendResults();
        }
    }

    synchronized void removeUser( final ShabaUser user )
    {
        onlineUsers.remove( user );
        LOGGER.info( format( LOGGED_OUT, user ) );
        setChanged();
        notifyObservers( new Pair<Boolean, ShabaUser>( false, user ) );
        sendResults();
    }

    @Override
    protected List<ShabaUser> getResult( Boolean resultRestrictor )
    {
        return resultRestrictor ? getLoggedInUsers() : Collections.<ShabaUser> emptyList();
    }

    @Override
    protected void doBeforeSendingSingleResult(
            DeferredResult<List<ShabaUser>> deferredResult,
            Pair<ShabaUser, Boolean> userAndRestrictor )
    {
        userAndRestrictor.setValue( true );
    }

    @ManagedOperation ( description = "View Active Users" )
    public synchronized List<ShabaUser> getLoggedInUsers()
    {
        onlineUsers.forEach( ( user ) -> user.eraseCredentials() );
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

    @Override
    public String toString()
    {
        return onlineUsers.toString();
    }
}
