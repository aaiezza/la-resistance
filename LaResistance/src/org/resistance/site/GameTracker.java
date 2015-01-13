package org.resistance.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.resistance.site.mech.AINamer;
import org.resistance.site.web.utils.MessageRelayer;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameTracker extends MessageRelayer<List<Game>>
{
    public static final String      RELAY_DESTINATION = "/topic/activeGames";

    public static final String      SUBSCRIPTION_URL  = "/activeGames";

    private final Map<String, Game> games;

    private final BoardFactory      BOARD_FACTORY;

    private final AINamer           aiNamer;

    @Autowired
    public GameTracker( BoardFactory boardFactory, AINamer ai_namer )
    {
        super( LogFactory.getLog( GameTracker.class ), RELAY_DESTINATION );
        BOARD_FACTORY = boardFactory;
        games = Collections.synchronizedMap( new HashMap<String, Game>() );
        aiNamer = ai_namer;
    }

    public boolean registerGame( final String user )
    {
        Game game = new Game( user, BOARD_FACTORY, TEMPLATE, aiNamer );

        for ( final Game g : games.values() )
        {
            if ( g.containsDuplicatePlayers( game ) )
            {
                return false;
            }
        }

        games.put( game.getGameID(), game );
        LOGGER.info( String.format( "Game: %s REGISTERED", game ) );

        broadcastPayload();

        return true;
    }

    public boolean unRegisterGame( String gameID )
    {
        Game game = games.remove( gameID );
        if ( game != null )
        {
            LOGGER.info( String.format( "Game: %s UNREGISTERED", game ) );
        }

        broadcastPayload();

        return game != null;
    }

    public Game getGameFromHostUsername( String playerName )
    {
        for ( Game g : games.values() )
        {
            if ( g.getHost().getName().equals( playerName ) )
            {
                return g;
            }
        }

        return null;
    }

    public Game getGameUsernameIsPlayerIn( String playerName )
    {
        for ( Game g : games.values() )
        {
            if ( g.getPlayers().contains( playerName ) )
            {
                return g;
            }
        }

        return null;
    }

    public Game getGame( String gameID )
    {
        return games.get( gameID );
    }


    // TODO better ERROR handling
    public boolean addPlayerToGame( Player player )
    {
        Game game = getGame( player.getGameID() );

        if ( game == null )
        {
            // Game does not exist exception
            return false;
        }

        for ( final Game g : games.values() )
        {
            if ( !g.equals( game ) && g.getPlayers().contains( player ) )
            {
                // Player already in ANOTHER game
                return false;
            }
        }

        return game.addPlayer( player );
    }

    public synchronized List<Game> getActiveGames()
    {
        return new ArrayList<Game>( games.values() );
    }

    @Override
    protected synchronized List<Game> getPayload()
    {
        return getActiveGames();
    }

    @Override
    public synchronized List<Game> onSubscription( ShabaUser user )
    {
        return onSubscription( user, NO_UPDATE );
    }

    @Override
    public String toString()
    {
        return games.toString();
    }
}
