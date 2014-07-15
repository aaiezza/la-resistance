package org.resist.ance;

import java.util.ArrayList;

import org.resist.ance.mech.Mission;
import org.resist.ance.mech.Missions;

/**
 * @author Alex Aiezza
 */
public class Board
{
    private final Missions          missions;

    private Mission                 currentMission;

    private final int               teamVoteTracker;

    private final int               num_players;

    private final int               num_spies;


    private final ArrayList<Player> players;

    public Board( final int num_players, final int num_spies, final Missions missions )
    {
        this.missions = missions;

        teamVoteTracker = 0;

        this.num_spies = num_spies;

        this.num_players = num_players;

        players = new ArrayList<Player>();
    }

    public int getNumPlayers()
    {
        return num_players;
    }

    public ArrayList<Player> getPlayers()
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
    public boolean nextMission()
    {
        currentMission = missions.nextMission();

        return currentMission != null;
    }

    public Mission getCurrentMission()
    {
        return currentMission;
    }

    public int getNumSpies()
    {
        return num_spies;
    }
}
