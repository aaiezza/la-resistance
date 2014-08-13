package org.resistance.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.resistance.site.mech.Mission;
import org.resistance.site.mech.Missions;
import org.resistance.site.mech.Role;
import org.resistance.site.utils.VoteCounter;
import org.resistance.site.utils.VoteCounter.VoteResults;

/**
 * @author Alex Aiezza
 */
public class Board
{
    private final int          MAX_CONSECUTIVE_FAILED_TEAM_VOTES = 5;

    private final Missions     missions;

    private Mission            currentMission;

    private Mission            lastMission;

    private final int          num_players;

    private final int          num_spies;

    private final List<Player> players;

    private int                num_bots;

    private final List<AI>     bots;

    private Player             currentLeader;

    private Role               WINNER;

    public Board( final int num_players, final int num_spies, final Missions missions )
    {
        this.missions = missions;

        this.num_spies = num_spies;

        this.num_players = num_players;

        players = Collections.synchronizedList( new ArrayList<Player>() );

        bots = Collections.synchronizedList( new ArrayList<AI>() );
    }

    public int getNumPlayers()
    {
        return num_players;
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    List<AI> getBots()
    {
        return bots;
    }

    Role getWinner()
    {
        return WINNER;
    }

    int getTeamVoteTracker()
    {
        if ( currentMission == null )
        {
            return 0;
        }
        return currentMission.getAllTeamVotes().size();
    }

    /**
     * Proceed to the next mission
     * 
     * @return <code>true</code> if there is another mission to go on.
     */
    boolean nextMission()
    {
        if ( currentMission != null )
            lastMission = currentMission;

        currentMission = missions.nextMission();

        if ( currentMission == null )
        {
            WINNER = missions.getWinner();
        }

        return currentMission != null;
    }

    public Mission getCurrentMission()
    {
        return currentMission;
    }

    public Mission getLastMission()
    {
        return lastMission;
    }

    VoteCounter getTeamVoter()
    {
        return currentMission.getTeamVotes();
    }

    VoteResults getLastTeamVoteResults()
    {
        return currentMission.getLastTeamVotes().getResults();
    }

    public Player getCurrentLeader()
    {
        return currentLeader;
    }

    void setCurrentLeader( Player player )
    {
        if ( !players.contains( player ) )
        {
            return;
        }
        currentLeader = player;
    }

    boolean prepareForTeamVote()
    {
        currentMission.emptyTeam();
        currentMission.getAllTeamVotes().add( new VoteCounter( num_players ) );
        return currentMission.getAllTeamVotes().size() <= MAX_CONSECUTIVE_FAILED_TEAM_VOTES;
    }

    void rotateLeadership()
    {
        int nextLeader = players.indexOf( currentLeader ) + 1;
        currentLeader = nextLeader >= players.size() ? players.get( 0 ) : players.get( nextLeader );
    }

    boolean submitMissionVote( Player player, boolean _vote )
    {
        boolean missionVote = currentMission.submitMissionVote( player, _vote );
        if ( !currentMission.getMissionVotes().acceptingVotes() )
        {
            if ( currentMission.isSuccessful() )
            {
                missions.succeedMission( currentMission );
            } else
            {
                missions.failMission( currentMission );
            }
        }
        return missionVote;
    }

    public int getNumSpies()
    {
        return num_spies;
    }

    public int getNumBots()
    {
        return num_bots;
    }

    public boolean setNumBots( int nBots )
    {
        if ( ( nBots + ( players.size() - nBots ) ) > num_players )
        {
            return false;
        }
        num_bots = nBots;
        return true;
    }

    public boolean isFull()
    {
        return players.size() >= num_players;
    }

    Set<Mission> getMissions()
    {
        return missions.getMissions();
    }
}
