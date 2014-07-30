package org.resistance.site.mech;

import java.util.ArrayList;
import java.util.Collections;

import org.resistance.site.Player;

/**
 * @author Alex Aiezza
 */
public final class Mission implements Comparable<Mission>
{
    public final int          MissionNumber;

    public final int          TeamSize;

    public final int          MinimumFails;

    private ArrayList<Player> team;

    public Mission( int missionNumber, int teamSize, int minimumFails )
    {
        MissionNumber = missionNumber;
        TeamSize = teamSize;
        team = new ArrayList<Player>( teamSize );
        MinimumFails = minimumFails;
    }

    public void addPlayerToTeam( Player player ) throws UnsupportedOperationException
    {
        if ( team.size() >= TeamSize )
        {
            throw new UnsupportedOperationException( "Team is Full!" );
        }

        team.add( player );
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

        out.append( "\n" );

        team.forEach( ( p ) -> out.append( String.format( " %s\n", p.getName() ) ) );

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
