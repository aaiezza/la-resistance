package org.resist.ance.web.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

import javafx.util.Pair;

/**
 * @author Alex Aiezza
 */
public class UserTracker extends Observable
{
    private final ArrayList<ShabaUser> onlineUsers;

    private UserTracker()
    {
        onlineUsers = new ArrayList<ShabaUser>();
    }

    public synchronized void addUser( final ShabaUser user )
    {
        if ( !onlineUsers.contains( user ) )
        {
            onlineUsers.add( user );
            setChanged();
            notifyObservers( new Pair<Boolean, ShabaUser>( true, user ) );
        }
    }

    public synchronized void removeUser( final ShabaUser user )
    {
        onlineUsers.remove( user );
        setChanged();
        notifyObservers( new Pair<Boolean, ShabaUser>( false, user ) );
    }

    public synchronized Collection<ShabaUser> getLoggedInUsers()
    {
        return onlineUsers;
    }

}
