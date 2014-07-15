package org.resist.ance.web.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author Alex Aiezza
 */
@Component ( "userTracker" )
public class UserTracker
{
    private final ArrayList<Authentication> onlineUsers;

    public UserTracker()
    {
        onlineUsers = new ArrayList<Authentication>();
    }

    public synchronized void addUser( final Authentication user )
    {
        if ( !onlineUsers.contains( user ) )
        {
            onlineUsers.add( user );
        }
    }

    public synchronized void removeUser( final Authentication user )
    {
        onlineUsers.remove( user );
    }

    public synchronized Collection<Authentication> getLoggedInUsers()
    {
        return onlineUsers;
    }
}
