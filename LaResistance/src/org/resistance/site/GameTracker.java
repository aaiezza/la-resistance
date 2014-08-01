package org.resistance.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.resistance.site.web.utils.DeferredResponder;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class GameTracker extends DeferredResponder<Game, Boolean>
{
    private final Map<String, Game> games;

    private final BoardFactory      BOARD_FACTORY;

    @Autowired
    public GameTracker( BoardFactory boardFactory )
    {
        super( LogFactory.getLog( GameTracker.class ) );
        BOARD_FACTORY = boardFactory;
        games = Collections.synchronizedMap( new HashMap<String, Game>() );
    }

    public boolean registerGame( final String user )
    {
        Game game = new Game( user, BOARD_FACTORY );

        for ( final Game g : games.values() )
        {
            if ( g.containsDuplicatePlayers( game ) )
            {
                return false;
            }
        }

        games.put( game.getGameID(), game );
        LOGGER.info( String.format( "Game: %s REGISTERED", game ) );

        sendResults();

        return true;
    }

    public boolean unRegisterGame( String gameID )
    {
        Game game = games.remove( gameID );
        if ( game != null )
        {
            LOGGER.info( String.format( "Game: %s UNREGISTERED", game ) );
        }

        sendResults();

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
            return false;
        }

        for ( final Game g : games.values() )
        {
            if ( !g.equals( game ) && g.getPlayers().contains( player ) )
            {
                return false;
            }
        }

        return game.addPlayer( player );
    }

    public List<Game> getActiveGames()
    {
        return new ArrayList<Game>( games.values() );
    }

    @Override
    protected List<Game> getResult( Boolean resultRestrictor )
    {
        return resultRestrictor ? getActiveGames() : Collections.<Game> emptyList();
    }

    @Override
    protected synchronized void doBeforeSendingSingleResult(
            DeferredResult<List<Game>> deferredResult,
            Pair<ShabaUser, Boolean> userAndRestrictor )
    {
        userAndRestrictor.setValue( true );
    }
}
