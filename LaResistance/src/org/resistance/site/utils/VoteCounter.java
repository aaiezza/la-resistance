package org.resistance.site.utils;

import java.util.HashMap;

import org.resistance.site.Player;

/**
 * @author Alex Aiezza
 */
public class VoteCounter
{
    private final HashMap<Player, Boolean> votes;

    public VoteCounter()
    {
        votes = new HashMap<Player, Boolean>();
    }

    public synchronized boolean vote( Player player, boolean _vote )
    {
        if ( !votes.containsKey( player ) )
        {
            return false;
        }
        votes.put( player, _vote );
        return true;
    }

    public synchronized VoteResults getResults()
    {
        VoteResults results = new VoteResults();

        for ( boolean vote : votes.values() )
        {
            if ( vote )
            {
                results.approve++;
            } else
            {
                results.deny++;
            }
        }

        return results;
    }

    public class VoteResults
    {
        private int approve, deny;

        public int approves()
        {
            return approve;
        }

        public int denies()
        {
            return deny;
        }
    }
}
