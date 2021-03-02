package org.resistance.site.mech;

import static org.resistance.site.mech.Role.LOYAL;
import static org.resistance.site.mech.Role.SPY;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Stack;
import org.resistance.site.Mission;

/** @author Alex Aiezza */
public class Missions {
  private static final int NUMBER_OF_MISSIONS = 5;

  public static final int UNATTEMPTED_MISSION = 0;

  public static final int SUCCESSFUL_MISSION = 1;

  public static final int FAILED_MISSION = -1;

  private final LinkedHashMap<Mission, Integer> missions;

  private final Iterator<Mission> iterator;

  private Role WINNER;

  public Missions(Stack<Mission> missions) {
    this.missions = new LinkedHashMap<Mission, Integer>(NUMBER_OF_MISSIONS);

    missions.forEach((m) -> this.missions.put(m, UNATTEMPTED_MISSION));

    iterator = this.missions.keySet().iterator();
  }

  public final Mission nextMission() {
    final int goal = (int) Math.ceil(NUMBER_OF_MISSIONS / 2d);
    int successes = 0, fails = 0;

    for (int status : missions.values()) {
      if (status == SUCCESSFUL_MISSION) {
        successes++;
      } else if (status == FAILED_MISSION) {
        fails++;
      }
      if (status == UNATTEMPTED_MISSION) {
        break;
      }

      if (successes >= goal) {
        WINNER = LOYAL;
        return null;
      } else if (fails >= goal) {
        WINNER = SPY;
        return null;
      }
    }

    if (iterator.hasNext()) {
      return iterator.next();
    }

    return null;
  }

  public void failMission(Mission m) {
    missions.put(m, FAILED_MISSION);
  }

  public void succeedMission(Mission m) {
    missions.put(m, SUCCESSFUL_MISSION);
  }

  public int getMissionStatus(Mission m) {
    return missions.get(m);
  }

  public Set<Mission> getMissions() {
    return missions.keySet();
  }

  public Role getWinner() {
    return WINNER;
  }
}
