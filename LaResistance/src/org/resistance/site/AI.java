package org.resistance.site;

import static org.resistance.site.mech.Role.SPY;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.resistance.site.mech.GameState;
import org.resistance.site.mech.Mission;

public class AI extends Player
{
    private static final String NAME_FORMAT   = "bot%d";

    private Game                activeGame;

    private boolean             choseTeam;

    private GameState           lastGameState = GameState.AWAITING_PLAYERS;

    private final Log           LOGGER        = LogFactory.getLog( AI.class );

    private AI( String name, String gameID )
    {
        super( name, gameID );
    }

    static AI createAI( final int aiNumber, final String gameID )
    {
        return new AI( String.format( NAME_FORMAT, aiNumber ), gameID );
    }

    void updateGame( Game game ) throws InterruptedException
    {
        if ( lastGameState.equals( game.getState() ) )
        {
            LOGGER.info( "NO NEED TO UPDATE: " + getName() );
            return;
        }

        activeGame = game;

        lastGameState = activeGame.getState();

        LOGGER.info( "I could use an UPDATE: " + getName() + " - " + lastGameState );

        Thread.currentThread().sleep( 2000 );

        switch ( activeGame.getState() )
        {
        case AWAITING_PLAYERS:
            break;
        case PLAYERS_LEARNING_ROLES:
            activeGame.setPlayerRoleLearned( getName() );
            break;
        case LEADER_CHOOSING_TEAM:
            if ( activeGame.getCurrentLeader().equals( getName() ) && !choseTeam )
            {
                decideOnATeam();
                if ( !activeGame.submitTeam( getName() ) )
                {
                    LOGGER.info( "Trouble SUBMITTING TEAM: " + getName() );
                }
            }
            break;
        case RESISTANCE_VOTES_ON_TEAM:
            if ( !activeGame.submitTeamVote( getName(), decideToLikeTeam() ) )
            {
                LOGGER.info( "Trouble SUBMITTING TEAM VOTE: " + getName() );
            }
            break;
        case TEAM_VOTES_ON_MISSION:
            if ( activeGame.getTeam().contains( getName() ) )
            {
                if ( !activeGame.submitMissionVote( getName(), decideToDoDuty() ) )
                {
                    LOGGER.info( "Trouble SUBMITTING MISSION VOTE: " + getName() );
                }
            }
            break;
        case GAME_OVER:
            break;
        default:
            break;
        }
    }

    private void decideOnATeam()
    {
        choseTeam = true;

        final List<Player> myTeam = activeGame.getDefaultScopeBoard().getCurrentMission().getTeam();

        int requirement = activeGame.getDefaultScopeBoard().getCurrentMission().TeamSize;

        // TODO

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
        if ( choseTeam )
        {
            choseTeam = false;
            return true;
        }

        List<Player> team = activeGame.getDefaultScopeBoard().getCurrentMission().getTeam();

        for ( Player p : team )
        {
            if ( !likePlayer( p, null ) )
            {
                return activeGame.getTeamVoteTracker() == 4;
            }
        }

        return true;
    }

    private boolean decideToDoDuty()
    {
        // TODO

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
        if ( pick.equals( this ) )
        {
            return true;
        }

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
                    choose = false;
                    break;
                }
            }

            return choose || ( picks != null && picks.size() < ( requirement - team.size() ) );
        }
    }

}
