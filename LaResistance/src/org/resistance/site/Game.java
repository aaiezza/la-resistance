package org.resistance.site;

import static org.resistance.site.mech.GameState.AWAITING_PLAYERS;

import java.util.ArrayList;

import org.resistance.site.mech.GameState;

/**
 * @author Alex Aiezza
 */
public class Game
{
    private final static String TO_STRING_FORMAT = "Host: %s, spies: %d,players: %d";

    private Board         board;

    private final Player        host;

    private final GameState     state;

    public Game( final Player player )
    {
        host = player;
        state = AWAITING_PLAYERS;
    }

    public boolean containsDuplicatePlayers( Game g )
    {
        for ( final Player p : board.getPlayers() )
        {
            if ( g.board.getPlayers().contains( p ) )
            {
                return true;
            }
        }
        return false;
    }

    public GameState getState()
    {
        return state;
    }

    @Override
    public int hashCode()
    {
        return board.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof Game )
        {
            return board.equals( ( (Game) o ).board );
        }
        return false;
    }

    @SuppressWarnings ( "serial" )
    public ArrayList<Player> getPlayers()
    {
        return new ArrayList<Player>( board.getPlayers() )
        {
            /**
             * @see java.util.ArrayList#contains(java.lang.Object)
             */
            @Override
            public boolean contains( Object o )
            {
                if ( o instanceof String )
                {
                    for ( Player p : this )
                    {
                        if ( p.getName().equals( o ) )
                        {
                            return true;
                        }
                    }
                }
                return super.contains( o );
            }
        };
    }

    @Override
    public String toString()
    {
        return String.format( TO_STRING_FORMAT, host, board.getNumSpies(), board.getNumPlayers() );
    }

}
