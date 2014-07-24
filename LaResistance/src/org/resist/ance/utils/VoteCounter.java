package org.resist.ance.utils;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

/**
 * @author Alex Aiezza
 */
@Service
public class VoteCounter
{
    private final ArrayList<Boolean> votes;

    public VoteCounter()
    {
        votes = new ArrayList<Boolean>();
    }

    public synchronized void vote( boolean _vote )
    {
        votes.add( _vote );
    }

    public synchronized void resetVoteCounter()
    {
        votes.clear();
    }

    public synchronized VoteResults getResults()
    {
        VoteResults results = new VoteResults();

        for ( boolean vote : votes )
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
        protected int approve, deny;

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
