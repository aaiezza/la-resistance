package org.resist.ance.mech;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;

public class Missions
{
    private final static int                      NUMBER_OF_MISSIONS  = 5;

    private final static int                      UNATTEMPTED_MISSION = 0;

    private final static int                      SUCCESSFUL_MISSION  = 1;

    private final static int                      FAILED_MISSION      = -1;

    private final LinkedHashMap<Mission, Integer> missions;

    private final Iterator<Mission>               iterator;

    public Missions( Stack<Mission> missions )
    {
        this.missions = new LinkedHashMap<Mission, Integer>( NUMBER_OF_MISSIONS );

        missions.forEach( ( m ) -> this.missions.put( m, UNATTEMPTED_MISSION ) );

        iterator = this.missions.keySet().iterator();
    }

    public final Mission nextMission()
    {
        if ( iterator.hasNext() )
        {
            return iterator.next();
        }

        return null;
    }

    public void failMission( Mission m )
    {
        missions.put( m, FAILED_MISSION );
    }

    public void succeedMission( Mission m )
    {
        missions.put( m, SUCCESSFUL_MISSION );
    }
}
