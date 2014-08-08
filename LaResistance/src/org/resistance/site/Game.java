package org.resistance.site;

import static org.resistance.site.Game.GameIDGenerator.GenerateID;
import static org.resistance.site.mech.GameState.AWAITING_PLAYERS;
import static org.resistance.site.mech.GameState.GAME_OVER;
import static org.resistance.site.mech.GameState.LEADER_CHOOSING_TEAM;
import static org.resistance.site.mech.GameState.PLAYERS_LEARNING_ROLES;
import static org.resistance.site.mech.GameState.RESISTANCE_VOTES_ON_TEAM;
import static org.resistance.site.mech.GameState.TEAM_VOTES_ON_MISSION;
import static org.resistance.site.mech.Role.LOYAL;
import static org.resistance.site.mech.Role.SPY;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.LogFactory;
import org.resistance.site.mech.GameState;
import org.resistance.site.mech.Mission;
import org.resistance.site.mech.Role;
import org.resistance.site.utils.RandomPicker;
import org.resistance.site.web.utils.MessageRelayer;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.sun.istack.internal.NotNull;

/**
 * @author Alex Aiezza
 */
public class Game extends MessageRelayer<Game>
{
    private static final String PLAYER_UPDATE_LOG        = "Number of Players Updated: %s";

    private static final String MONITOR_URL_FORMAT       = "game?gameID=%s";

    private static final String RELAY_DESTINATION_FORMAT = "/queue/game/%s";

    public static final String  RELAY_DESTINATION        = "/queue/game/{gameID}";

    public static final String  SUBSCRIPTION_URL         = "/game/{gameID}";

    private static final String TO_STRING_FORMAT         = "(ID:%s, spies:%d,players:%d)";

    private static final int    DEFAULT_PLAYERS          = 5;

    private static final String MES_WINNER               = "<span id='winner' class='%1$s'>The %1$sS are Victorious!</span>";

    private static final String MES_MISSION_SUCCESS      = "<span id='successful'>The mission was a Success! (%d : %d)</span>";

    private static final String MES_MISSION_FAILURE      = "<span id='failure'>The mission was a Failure! (%d : %d)</span>";

    private static final String MES_TEAM_APPROVE         = "<span>Your Team is out on their Mission!<br/>(%d : %d)</span>";

    private static final String MES_TEAM_DENY            = "<span id='failure'>The Resistance did NOT agree with that Team!<br/>(%d : %d)</span>";

    private Board               board;

    private final Player        host;

    private GameState           state;

    private final String        GAME_ID;

    private final BoardFactory  BoardFactory;

    private Role                winningRole;

    String                      message                  = new String();

    @NotNull
    private String              broadcastingRoles        = new String();

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
            state = PLAYERS_LEARNING_ROLES;
            break;
        case PLAYERS_LEARNING_ROLES:
            appointLeader( true );
            if ( !board.nextMission() )
            {
                // Bad logic error if we end up here =[
                LOGGER.error( "Yea... This would be a very weird error, but an error none the less" );
            }
            board.prepareForTeamVote();
            state = LEADER_CHOOSING_TEAM;
            break;
        case LEADER_CHOOSING_TEAM:
            if ( !board.getCurrentMission().teamIsFull() )
                break;
            board.getCurrentMission().finalizeTeam();
            state = RESISTANCE_VOTES_ON_TEAM;
            break;
        case RESISTANCE_VOTES_ON_TEAM:
            if ( board.getTeamVoter().getResults().isPasses() )
            {
                state = TEAM_VOTES_ON_MISSION;
                message = String.format( MES_TEAM_APPROVE, board.getTeamVoter().getResults()
                        .approves(), board.getTeamVoter().getResults().denies() );
            } else
            {
                if ( !board.prepareForTeamVote() )
                {
                    // SPIES WIN
                    winningRole = SPY;
                    message += "<br/>" + String.format( MES_WINNER, winningRole );
                    state = GAME_OVER;
                    break;
                }
                message = String.format( MES_TEAM_DENY, board.getLastTeamVoteResults().approves(),
                    board.getLastTeamVoteResults().denies() );
                appointLeader( false );
                state = LEADER_CHOOSING_TEAM;
            }
            break;
        case TEAM_VOTES_ON_MISSION:
            if ( board.getCurrentMission().isSuccessful() )
            {
                message = String.format( MES_MISSION_SUCCESS, board.getCurrentMission()
                        .getMissionVotes().getResults().approves(), board.getCurrentMission()
                        .getMissionVotes().getResults().denies() );
            } else
            {
                message = String.format( MES_MISSION_FAILURE, board.getCurrentMission()
                        .getMissionVotes().getResults().approves(), board.getCurrentMission()
                        .getMissionVotes().getResults().denies() );
            }
            if ( !board.nextMission() )
            {
                winningRole = board.getWinner();
                message = "<br/>" + String.format( MES_WINNER, winningRole );
                state = GAME_OVER;
            } else
            {
                board.prepareForTeamVote();
                appointLeader( false );
                state = LEADER_CHOOSING_TEAM;
            }
            break;
        case GAME_OVER:
            break;
        default:
            return false;
        }

        broadcastGame();
        return true;
    }

    private void broadcastGameToEachPlayer()
    {
        board.getPlayers().forEach( ( player ) -> {
            broadcastingRoles = player.getName();
            broadcastPayload( player.getName() );
        } );
        broadcastingRoles = "";
    }

    /**
     * The reason we want this method is because the impersonal
     * {@link #broadcastGame} does not contain roles. The
     * {@link #broadcastGameToEachPlayer()} however, is a personal broadcast
     * that contains only that user's role so as to not reveal it to any other
     * client or listener
     */
    private void broadcastGame()
    {
        broadcastPayload();
        broadcastGameToEachPlayer();
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
        if ( !state.equals( AWAITING_PLAYERS ) )
        {
            broadcastingRoles = user.getUsername();
        }
        onSubscription( user, UPDATE_USER );
        broadcastingRoles = "";
    }

    public String getUpdateMessage()
    {
        return message.isEmpty() ? message : String.format( "<tr><td colspan='2'>%s</td></tr>",
            message );
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

    public String getMonitorURL()
    {
        return String.format( MONITOR_URL_FORMAT, GAME_ID );
    }

    public Set<Mission> getMissions()
    {
        return board.getMissions();
    }

    public List<String> getTeam()
    {
        if ( board.getCurrentMission() == null )
        {
            return Collections.<String> emptyList();
        }
        final ArrayList<String> team = new ArrayList<String>();
        board.getCurrentMission().getTeam().forEach( ( player ) -> team.add( player.getName() ) );
        return team;
    }

    public int getTeamVoteTracker()
    {
        if ( state.equals( AWAITING_PLAYERS ) || state.equals( PLAYERS_LEARNING_ROLES ) )
        {
            return 0;
        } else if ( state.equals( GAME_OVER ) )
        {
            if ( board.getCurrentMission() != null )
            {
                return board.getCurrentMission().getAllTeamVotes().size() - 1;
            } else if ( board.getLastMission() != null )
            {
                return board.getLastMission().getAllTeamVotes().size() - 1;
            } else
            {
                return -1;
            }
        }
        return board.getTeamVoteTracker() - 1;
    }

    public String getCurrentLeader()
    {
        if ( state.equals( AWAITING_PLAYERS ) || state.equals( PLAYERS_LEARNING_ROLES ) ||
                state.equals( GAME_OVER ) )
        {
            return "";
        }
        return board.getCurrentLeader().getName();
    }

    public int getTeamSizeRequirement()
    {
        if ( state.equals( AWAITING_PLAYERS ) || state.equals( PLAYERS_LEARNING_ROLES ) ||
                state.equals( GAME_OVER ) )
        {
            return 0;
        }
        return board.getCurrentMission().TeamSize;
    }

    public Player getPlayerFromUsername( String username )
    {
        int p = board.getPlayers().indexOf( new Player( username, GAME_ID ) );
        return board.getPlayers().get( p );
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
        List<Player> players = Collections.synchronizedList( new ArrayList<Player>()
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

        // SPIES KNOW WHO'S WHO!
        if ( ( !broadcastingRoles.isEmpty() && getPlayerFromUsername( broadcastingRoles ).getRole() == SPY ) ||
                state.equals( GAME_OVER ) )
        {
            return board.getPlayers();
        }

        // HIDE THE ROLES!!!
        board.getPlayers().forEach( ( player ) -> {
            try
            {
                Player p = player.clone();

                if ( player.getName().equals( broadcastingRoles ) )
                {
                    p.setRole( player.role );
                }
                players.add( p );
            } catch ( Exception e )
            {
                e.printStackTrace();
            }
        } );

        return players;
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
    public synchronized boolean makeBoard( int numberOfPlayers )
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
            Board b = BoardFactory.MakeBoard( numberOfPlayers );
            if ( b == null )
                return false;
            board = b;
        } catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }

        if ( saveThePlayers )
        {
            board.getPlayers().addAll(
                players.subList( 0, players.size() >= board.getNumPlayers() ? board.getNumPlayers()
                                                                           : players.size() ) );
            broadcastPayload();
            LOGGER.info( String.format( PLAYER_UPDATE_LOG, this ) );
        }

        return board != null;
    }

    // TODO static int numerical state handling
    boolean addPlayer( Player player )
    {
        if ( !state.equals( AWAITING_PLAYERS ) || board.getPlayers().contains( player ) ||
                board.getNumPlayers() <= board.getPlayers().size() )
        {
            // Wrong state exception
            // player already in THIS game
            // At max capacity
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

    private void assignRoles()
    {
        if ( !state.equals( AWAITING_PLAYERS ) )
        {
            return;
        }
        List<Player> players = board.getPlayers();

        final Stack<Role> roles = new Stack<Role>();

        for ( int i = 0; i < players.size(); i++ )
        {
            if ( i < board.getNumSpies() )
            {
                roles.push( SPY );
            } else
            {
                roles.push( LOYAL );
            }
        }

        Collections.shuffle( roles );

        players.forEach( ( player ) -> player.setRole( roles.pop() ) );
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
    /* PLAYERS_LEARNING_ROLES */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */

    public void setPlayerRoleLearned( String username )
    {
        if ( !state.equals( PLAYERS_LEARNING_ROLES ) )
        {
            return;
        }

        getPlayerFromUsername( username ).setRoleLearned();

        broadcastGame();

        final AtomicBoolean rolesLearned = new AtomicBoolean( true );

        board.getPlayers().forEach( ( player ) -> {
            if ( !player.isRoleLearned() )
            {
                rolesLearned.set( false );
            }
        } );

        if ( rolesLearned.get() )
        {
            progressState();
        }
    }

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* LEADER_CHOOSING_TEAM */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    public void addTeammate( String leader, String username )
    {
        if ( !state.equals( LEADER_CHOOSING_TEAM ) || !getPlayers().contains( username ) ||
                !getCurrentLeader().equals( leader ) )
        {
            return;
        }

        board.getCurrentMission().addPlayerToTeam( getPlayerFromUsername( username ) );

        broadcastGame();
    }

    public void dismissTeammate( String leader, String username )
    {
        if ( !state.equals( LEADER_CHOOSING_TEAM ) || !getPlayers().contains( username ) ||
                !getCurrentLeader().equals( leader ) )
        {
            return;
        }

        board.getCurrentMission().dismissPlayerFromTeam( getPlayerFromUsername( username ) );

        broadcastGame();
    }

    // TODO static int numerical state handling
    public boolean submitTeam( String currentLeader )
    {
        if ( !state.equals( LEADER_CHOOSING_TEAM ) || !getCurrentLeader().equals( currentLeader ) )
        {
            return false;
        }

        progressState();
        return true;
    }

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* RESISTANCE_VOTES_ON_TEAM */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    public boolean submitTeamVote( String username, boolean _vote )
    {
        if ( !state.equals( RESISTANCE_VOTES_ON_TEAM ) || !getPlayers().contains( username ) )
        {
            return false;
        }

        board.getCurrentMission().submitTeamVote( getPlayerFromUsername( username ), _vote );

        if ( !board.getTeamVoter().acceptingVotes() )
        {
            progressState();
        }

        return true;
    }

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* TEAM_VOTES_ON_MISSION */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    public boolean submitMissionVote( String username, boolean _vote )
    {
        if ( !state.equals( TEAM_VOTES_ON_MISSION ) || !getPlayers().contains( username ) )
        {
            return false;
        }

        boolean voteSubmitted = board.submitMissionVote( getPlayerFromUsername( username ), _vote );

        if ( voteSubmitted && !board.getCurrentMission().getMissionVotes().acceptingVotes() )
        {
            progressState();
        }

        return voteSubmitted;
    }

    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ONLY WHILE ** ** ** ** ** ** ** ** ** ** ** ** */
    /* GAME_OVER */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    /* ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** * */
    public Board getBoard()
    {
        if ( !state.equals( GAME_OVER ) )
        {
            return null;
        }
        return board;
    }

    public Role getWinners()
    {
        if ( !state.equals( GAME_OVER ) )
        {
            return null;
        }
        return winningRole;
    }
}
