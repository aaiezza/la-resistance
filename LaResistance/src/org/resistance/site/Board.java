package org.resistance.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.resistance.site.mech.Mission;
import org.resistance.site.mech.Missions;
import org.resistance.site.utils.VoteCounter;

/**
 * @author Alex Aiezza
 */
public class Board
{
    private final Missions                  missions;

    private Mission                         currentMission;

    private final int                       teamVoteTracker;

    private final Map<Mission, VoteCounter> voteLog;

    private final int                       num_players;

    private final int                       num_spies;

    private final List<Player>              players;

    private Player                          currentLeader;

    public Board( final int num_players, final int num_spies, final Missions missions )
    {
        this.missions = missions;

        teamVoteTracker = 0;

        this.num_spies = num_spies;

        this.num_players = num_players;

        players = Collections.synchronizedList( new ArrayList<Player>() );

        voteLog = Collections.synchronizedMap( new LinkedHashMap<Mission, VoteCounter>( 5 ) );
    }

    public int getNumPlayers()
    {
        return num_players;
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public int getTeamVoteTracker()
    {
        return teamVoteTracker;
    }

    /**
     * Proceed to the next mission
     * 
     * @return <code>true</code> if there is another mission to go on.
     */
    boolean nextMission()
    {
        currentMission = missions.nextMission();

        return currentMission != null;
    }

    public Mission getCurrentMission()
    {
        return currentMission;
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

    void rotateLeadership()
    {
        int nextLeader = players.indexOf( currentLeader ) + 1;
        currentLeader = nextLeader >= players.size() ? players.get( 0 ) : players.get( nextLeader );
    }

    // TODO
    boolean submitVote( Player player, boolean _vote )
    {
        if ( !voteLog.containsKey( currentMission ) )
        {
            voteLog.put( currentMission, new VoteCounter() );
        }
        
        return voteLog.get( currentMission ).vote( player, _vote );
    }

    public int getNumSpies()
    {
        return num_spies;
    }
}
