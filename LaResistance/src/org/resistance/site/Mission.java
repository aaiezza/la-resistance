package org.resistance.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.resistance.site.utils.VoteCounter;

/**
 * @author Alex Aiezza
 */
public final class Mission implements Comparable<Mission>, Cloneable
{
    public final int                MissionNumber;

    public final int                TeamSize;

    public final int                MinimumFails;

    private ArrayList<Player>       team;

    private final List<VoteCounter> teamVotes;

    private final VoteCounter       missionVotes;

    private boolean                 teamIsFinal;

    public Mission( int missionNumber, int teamSize, int minimumFails )
    {
        MissionNumber = missionNumber;
        TeamSize = teamSize;
        team = new ArrayList<Player>( teamSize );
        MinimumFails = minimumFails;
        missionVotes = new VoteCounter( teamSize );
        teamVotes = Collections.synchronizedList( new ArrayList<VoteCounter>() );
        teamIsFinal = false;
    }

    public void addPlayerToTeam( Player player ) throws UnsupportedOperationException
    {
        if ( teamIsFull() || teamIsFinal || team.contains( player ) )
            throw new UnsupportedOperationException( "Cannot add this player to the Team!" );

        team.add( player );
    }

    public void dismissPlayerFromTeam( Player player ) throws UnsupportedOperationException
    {
        if ( !team.contains( player ) || teamIsFinal )
            throw new UnsupportedOperationException( "Cannot remove this player from the Team!" );

        team.remove( player );
    }

    public boolean submitMissionVote( Player player, boolean _vote )
    {
        if ( !team.contains( player ) || !teamIsFull() )
            return false;

        // If player already voted, this will not work
        return missionVotes.vote( player, _vote );
    }

    public VoteCounter getMissionVotes()
    {
        return missionVotes;
    }

    public Boolean isSuccessful()
    {
        if ( missionVotes.acceptingVotes() )
            return null;

        return missionVotes.getResults().isFailed( MinimumFails );
    }

    public void submitTeamVote( Player player, boolean vote )
    {
        if ( !teamIsFull() )
            return;

        // If player already voted, this will not work
        getTeamVotes().vote( player, vote );
    }

    public List<VoteCounter> getAllTeamVotes()
    {
        return teamVotes;
    }

    public VoteCounter getTeamVotes()
    {
        if ( teamVotes.size() <= 0 )
            return null;

        return teamVotes.get( teamVotes.size() - 1 );
    }

    public VoteCounter getLastTeamVotes()
    {
        if ( teamVotes.size() <= 1 )
            return null;

        return teamVotes.get( teamVotes.size() - 2 );
    }

    public void emptyTeam()
    {
        teamIsFinal = false;
        team.clear();
    }

    public Boolean isTeamVoteSuccessful()
    {
        VoteCounter teamVotes = getTeamVotes();
        if ( teamVotes == null )
            return null;

        return teamVotes.getResults().denies() < MinimumFails;
    }

    public boolean teamIsFull()
    {
        return team.size() >= TeamSize;
    }

    public void finalizeTeam()
    {
        teamIsFinal = true;
    }

    public List<Player> getTeam()
    {
        return team;
    }

    public String getHTMLTeam()
    {
        final StringBuilder out = new StringBuilder();

        team.forEach( p -> out.append( "<tr><td>" ).append( p.getName() ).append( "</td></tr>" ) );

        return out.toString();
    }

    @Override
    public Mission clone()
    {
        final Mission m = new Mission( MissionNumber, TeamSize, MinimumFails );

        team.forEach( ( player ) -> m.team.add( player.clone() ) );

        return m;
    }

    @Override
    public int compareTo( Mission o )
    {
        return MissionNumber - o.MissionNumber;
    };

    @Override
    public String toString()
    {
        final StringBuilder out = new StringBuilder( String.format( "Mission %d", MissionNumber ) );

        if ( MinimumFails > 1 )
        {
            out.append( " *" );
        }

        out.append( " [" );

        for ( int p = 0; p < team.size(); p++ )
        {
            out.append( team.get( p ) );
            if ( p + 1 < team.size() )
            {
                out.append( ", " );
            }
        }

        out.append( "]" );

        return out.toString();
    }

    public static void main( String [] args )
    {
        // TEST 1
        Mission m1 = new Mission( 1, 2, 1 );
        Mission m2 = new Mission( 2, 3, 1 );
        Mission m3 = new Mission( 3, 3, 1 );

        ArrayList<Mission> mis = new ArrayList<Mission>( 3 );
        mis.add( m1 );
        mis.add( m2 );
        mis.add( m3 );

        System.out.println( mis );
        Collections.sort( mis );
        System.out.println( mis );

        System.out.println( mis.get( 0 ) );

        // TEST 2

    }
}
