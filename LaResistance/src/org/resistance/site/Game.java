package org.resistance.site;

import static org.resistance.site.Game.GameIDGenerator.GenerateID;
import static org.resistance.site.mech.GameState.AWAITING_PLAYERS;
import static org.resistance.site.mech.GameState.LEADER_CHOOSING_TEAM;
import static org.resistance.site.mech.GameState.RESISTANCE_VOTES_ON_TEAM;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.resistance.site.mech.GameState;
import org.resistance.site.utils.RandomPicker;
import org.resistance.site.web.utils.MessageRelayer;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * @author Alex Aiezza
 */
public class Game extends MessageRelayer<Game>
{
    private static final String RELAY_DESTINATION_FORMAT = "/queue/game/%s";

    public static final String  RELAY_DESTINATION        = "/queue/game/{gameID}";

    public static final String  SUBSCRIPTION_URL         = "/game/{gameID}";

    private static final String TO_STRING_FORMAT         = "(ID:%s, spies:%d,players:%d)";

    private static final int    DEFAULT_PLAYERS          = 5;

    private Board               board;

    private final Player        host;

    private GameState           state;

    private final String        GAME_ID;

    private final BoardFactory  BoardFactory;

    public Game(
        final String hostName,
        final BoardFactory boardFactory,
        SimpMessagingTemplate template )
    {
        super( LogFactory.getLog( Game.class ), template );

        GAME_ID = GenerateID( hostName );

        host = new Player( hostName, GAME_ID );
        state = AWAITING_PLAYERS;

        BoardFactory = boardFactory;
        makeBoard( DEFAULT_PLAYERS );
        addPlayer( host );
    }

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* STANDARD GAME FUNCTIONALITY * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */

    private boolean progressState()
    {
        switch ( state )
        {
        case AWAITING_PLAYERS:
            assignRoles();
            appointLeader( true );
            state = LEADER_CHOOSING_TEAM;
            broadcastPayload();
            return true;
        default:
            return false;
        }
    }

    @Override
    protected Game getPayload()
    {
        return this;
    }

    @Override
    public String getRelayDestination()
    {
        return String.format( RELAY_DESTINATION_FORMAT, GAME_ID );
    }

    @Override
    public void onSubscription( ShabaUser user )
    {
        onSubscription( user, UPDATE_USER );
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

    public Player getHost()
    {
        return host;
    }

    public GameState getState()
    {
        return state;
    }

    public String getGameID()
    {
        return GAME_ID;
    }

    public int getMaxPlayers()
    {
        return board.getNumPlayers();
    }

    public Player getPlayerFromUsername( String username )
    {
        int p = getPlayers().indexOf( new Player( username, GAME_ID ) );
        return getPlayers().get( p );
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
    public List<Player> getPlayers()
    {
        return Collections.synchronizedList( new ArrayList<Player>( board.getPlayers() )
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
        } );
    }

    @Override
    public String toString()
    {
        return String
                .format( TO_STRING_FORMAT, GAME_ID, board.getNumSpies(), board.getNumPlayers() );
    }

    static class GameIDGenerator
    {
        private final static String    ID_FORMAT = "%s_%s";

        private final static int       idLength  = 8;

        private final static String [] HEX       = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
                                                         "9", "A", "B", "C", "D", "E", "F" };

        protected static String GenerateID( String host )
        {
            StringBuilder id = new StringBuilder();

            for ( int i = 0; i < idLength; i++ )
            {
                id.append( RandomPicker.pick( HEX ) );
            }

            return String.format( ID_FORMAT, host, id );
        }
    }

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* AWAITING PLAYERS */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */


    /**
     * Called every time the number of players changes
     * 
     * @param numberOfPlayers
     */
    public boolean makeBoard( int numberOfPlayers )
    {
        if ( !state.equals( AWAITING_PLAYERS ) )
        {
            return false;
        }

        boolean saveThePlayers = board != null;
        List<Player> players = null;

        if ( saveThePlayers )
        {
            players = board.getPlayers();
        }

        try
        {
            board = BoardFactory.MakeBoard( numberOfPlayers );
        } catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }

        if ( saveThePlayers )
        {
            board.getPlayers().addAll( players );
        }

        return board != null;
    }

    // TODO static int numerical state handling
    boolean addPlayer( Player player )
    {
        if ( !state.equals( AWAITING_PLAYERS ) || board.getPlayers().contains( player ) ||
                board.getNumPlayers() < board.getPlayers().size() )
        {
            return false;
        }
        board.getPlayers().add( player );
        broadcastPayload();
        return true;
    }

    // TODO static int numerical state handling
    public boolean dismissPlayer( Player player )
    {
        if ( !state.equals( AWAITING_PLAYERS ) || player.equals( host ) )
        {
            return false;
        }
        boolean dismissed = board.getPlayers().remove( player );
        if ( dismissed )
            broadcastPayload();
        return dismissed;
    }

    // TODO static int numerical state handling
    public boolean startGame( Player player )
    {
        if ( !state.equals( AWAITING_PLAYERS ) || !player.equals( host ) ||
                board.getNumPlayers() != board.getPlayers().size() )
        {
            return false;
        }
        return progressState();
    }

    // TODO
    private void assignRoles()
    {
        if ( !state.equals( AWAITING_PLAYERS ) )
        {
            return;
        }
    }

    private void appointLeader( boolean randomAppointing )
    {
        if ( randomAppointing )
        {
            board.setCurrentLeader( RandomPicker.pick( board.getPlayers() ) );
        } else
        {
            board.rotateLeadership();
        }
    }

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* LEADER_CHOOSING_TEAM */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* RESISTANCE_VOTES_ON_TEAM */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    public boolean submitVote( Player player, boolean _vote )
    {
        if ( !state.equals( RESISTANCE_VOTES_ON_TEAM ) )
        {
            return false;
        }
        return board.submitVote( player, _vote );
    }

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* TEAM_VOTES_ON_MISSION */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* GAME_OVER */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
}
