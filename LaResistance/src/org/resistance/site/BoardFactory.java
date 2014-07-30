package org.resistance.site;

import java.util.Scanner;
import java.util.Stack;

import org.resistance.site.mech.Mission;
import org.resistance.site.mech.Missions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Alex Aiezza
 */
@Service
public class BoardFactory
{
    private final Scanner BoardDB;

    @Autowired
    private BoardFactory( @Value ( "${Board_DB}" ) String boardDB )
    {
        BoardDB = new Scanner( boardDB );
    }

    public synchronized final Board MakeBoard( int numberOfPlayers )
    {
        Board board = null;

        findBoard: while ( BoardDB.hasNext() )
        {
            String [] values = BoardDB.nextLine().split( "\t" );
            int num_players = Integer.parseInt( values[0] );

            if ( num_players != numberOfPlayers )
            {
                continue findBoard;
            }

            int num_spies = Integer.parseInt( values[1] );

            String [] str_missions = values[2].split( ";" );

            Stack<Mission> missions_stack = new Stack<Mission>();

            int mission_num = 1;
            for ( String s_m : str_missions )
            {
                String [] str_mission = s_m.split( ":" );
                missions_stack.add( new Mission( mission_num++, Integer.parseInt( str_mission[0] ),
                        Integer.parseInt( str_mission[1] ) ) );
            }

            board = new Board( num_players, num_spies, new Missions( missions_stack ) );

            break findBoard;

        }

        return board;
    }
}
