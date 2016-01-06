package org.resistance.site;

import static org.resistance.site.mech.Role.SPY;
import static org.resistance.site.mech.Role.LOYAL;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.resistance.site.mech.GameState;

public class AI extends Player
{
    private Game      activeGame;

    private GameState lastGameState = GameState.AWAITING_PLAYERS;

    private final Log LOGGER        = LogFactory.getLog( AI.class );

    private AI( String name, String gameID )
    {
        super( name, gameID );
    }

    static AI createAI( final String aiName, final String gameID )
    {
        return new AI( aiName, gameID );
    }

    @SuppressWarnings ( "static-access" )
    void updateGame( Game game ) throws InterruptedException
    {
        if ( lastGameState.equals( game.getState() ) )
        {
            LOGGER.debug( "NO NEED TO UPDATE: " + getName() );
            return;
        }

        activeGame = game;

        lastGameState = activeGame.getState();

        LOGGER.debug( "I could use an UPDATE: " + getName() + " - " + lastGameState );

        // Replicate "thinking"
        Thread.currentThread().sleep( ( (long) Math.ceil( Math.random() * 5000 ) ) );

        switch ( activeGame.getState() )
        {
        case AWAITING_PLAYERS:
            break;
        case PLAYERS_LEARNING_ROLES:
            activeGame.setPlayerRoleLearned( getName() );
            break;
        case LEADER_CHOOSING_TEAM:
            if ( activeGame.getDefaultScopeBoard().getCurrentLeader() == this )
            {
                decideOnATeam();
                // final ThreadLocal<Player> proofOfLeadership = new
                // ThreadLocal<Player>();
                // proofOfLeadership.set( this );
                if ( !activeGame.submitTeam( getName() ) )
                    LOGGER.error( "Trouble SUBMITTING TEAM: " + getName() );
            }
            break;
        case RESISTANCE_VOTES_ON_TEAM:
            if ( !activeGame.submitTeamVote( getName(), decideToLikeTeam() ) )
                LOGGER.error( "Trouble SUBMITTING TEAM VOTE: " + getName() );
            break;
        case TEAM_VOTES_ON_MISSION:
            if ( activeGame.getTeam().contains( getName() ) )
                if ( !activeGame.submitMissionVote( getName(), decideToDoDuty() ) )
                    LOGGER.error( "Trouble SUBMITTING MISSION VOTE: " + getName() );
            break;
        case GAME_OVER:
            break;
        default:
            break;
        }
    }

    private void decideOnATeam()
    {
        final List<Player> myTeam = activeGame.getDefaultScopeBoard().getCurrentMission().getTeam();

        int requirement = activeGame.getDefaultScopeBoard().getCurrentMission().TeamSize;

        Stack<Player> picks = new Stack<Player>();
        picks.addAll( activeGame.getDefaultScopeBoard().getPlayers() );
        Collections.shuffle( picks );

        // Random-ish choice!
        while ( myTeam.size() < requirement )
        {
            Player pick = picks.pop();

            if ( likePlayer( pick, picks ) )
            {
                activeGame.addTeammate( getName(), pick.getName() );
            }
        }
    }

    private boolean decideToLikeTeam()
    {
        if ( activeGame.getDefaultScopeBoard().getCurrentLeader() == this )
            return true;

        if ( activeGame.getTeamVoteTracker() == 4 )
            return getRole() == LOYAL;

        List<Player> team = activeGame.getDefaultScopeBoard().getCurrentMission().getTeam();

        int dislikes = getRole() == SPY ? team.size() -
                activeGame.getDefaultScopeBoard().getCurrentMission().MinimumFails : activeGame
                .getDefaultScopeBoard().getCurrentMission().MinimumFails;

        for ( Player p : team )
        {
            if ( !likePlayer( p, null ) )
                dislikes--;


            if ( dislikes == 0 )
                return false;
        }

        return true;
    }

    private boolean decideToDoDuty()
    {
        if ( getRole() == SPY )
        {
            if ( activeGame.getFailedMissions() == 2 )
            {
                return false;
            }

            if ( activeGame.getTeam().size() == 2 )
            {
                return Math.random() > 0.5;
            } else
            {
                return false;
            }
        } else
        {
            return true;
        }
    }

    private int getSpyCount( List<Player> players )
    {
        int spies = 0;

        for ( Player p : players )
        {
            if ( p.getRole() == SPY )
            {
                spies++;
            }
        }
        return spies;
    }

    private boolean likePlayer( Player pick, final Stack<Player> picks )
    {
        // Arrogance Standard
        if ( pick.equals( this ) )
            return true;

        int minFails = activeGame.getDefaultScopeBoard().getCurrentMission().MinimumFails;
        int requirement = activeGame.getDefaultScopeBoard().getCurrentMission().TeamSize;

        final List<Player> team = activeGame.getDefaultScopeBoard().getCurrentMission().getTeam();


        if ( getRole() == SPY )
        {
            if ( getSpyCount( team ) < minFails )
            {
                return pick.getRole() == SPY ||
                        ( picks != null && picks.size() < ( requirement - team.size() ) );
            } else
            {
                return pick.getRole() != SPY ||
                        ( picks != null && picks.size() < ( requirement - team.size() ) );
            }
        } else
        {
            // SEE IF THIS PLAYER WAS ON A LOSING MISSION
            boolean choose = true;

            for ( Mission m : activeGame.getMissions() )
            {
                if ( m.equals( activeGame.getDefaultScopeBoard().getCurrentMission() ) )
                {
                    break;
                }
                if ( !m.isSuccessful() && m.getTeam().contains( pick ) )
                {
                    choose = Math.random() >= ( (double) m.getMissionVotes().getResults().denies() ) /
                            ( (double) m.TeamSize );
                    break;
                }
            }

            return choose || ( picks != null && picks.size() < ( requirement - team.size() ) ) ||
                    ( activeGame.getTeamVoteTracker() == 4 && Math.random() > 0.50 );
        }
    }

}
