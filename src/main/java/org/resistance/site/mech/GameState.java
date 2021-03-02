package org.resistance.site.mech;

/**
 * Resting states
 *
 * @author Alex Aiezza
 */
public enum GameState {
  AWAITING_PLAYERS,
  PLAYERS_LEARNING_ROLES,
  LEADER_CHOOSING_TEAM,
  RESISTANCE_VOTES_ON_TEAM,
  TEAM_VOTES_ON_MISSION,
  GAME_OVER;

  public String getName() {
    return name();
  }
}
