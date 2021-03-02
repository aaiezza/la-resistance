package org.resistance.site.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.resistance.site.Player;

/** @author Alex Aiezza */
public class VoteCounter {
  private final Map<Player, Boolean> votes;

  private final int nPlayers;

  public VoteCounter(int nPlayers) {
    this.nPlayers = nPlayers;
    votes = Collections.synchronizedMap(new HashMap<Player, Boolean>());
  }

  public synchronized boolean vote(Player player, boolean _vote) {
    if (votes.containsKey(player) || !acceptingVotes()) {
      return false;
    }
    votes.put(player, _vote);
    return true;
  }

  public boolean acceptingVotes() {
    return votes.size() < nPlayers;
  }

  @Override
  public String toString() {
    return String.format("approves: %d | denies: %d", getResults().approve, getResults().deny);
  }

  public synchronized VoteResults getResults() {
    VoteResults results = new VoteResults();

    if (acceptingVotes()) {
      return results;
    }

    for (boolean vote : votes.values()) {
      if (vote) {
        results.approve++;
      } else {
        results.deny++;
      }
    }

    return results;
  }

  public class VoteResults {
    private int approve, deny;

    public int approves() {
      return approve;
    }

    public int denies() {
      return deny;
    }

    public Boolean isPasses() {
      if (deny == 0 && approve == 0) {
        return null;
      }
      return deny < approve;
    }

    public Boolean isFailed(int minFailsToFail) {
      return deny < minFailsToFail;
    }
  }
}
