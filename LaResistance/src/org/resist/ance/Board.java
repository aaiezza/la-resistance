package org.resist.ance;

import java.util.ArrayList;

import org.resist.ance.mech.Mission;
import org.resist.ance.mech.Missions;

public class Board
{
    private final Missions          missions;

    private Mission                 currentMission;

    private final int               voteTeamTracker;

    private final ArrayList<Player> players;

    public Board( final Missions missions )
    {
        this.missions = missions;

        voteTeamTracker = 0;
        
        players = new ArrayList<Player>();
    }

    public ArrayList<Player> getPlayers()
    {
        return players;
    }

    public int getVoteTeamTracker()
    {
        return voteTeamTracker;
    }
}
