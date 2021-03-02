package org.resistance.site.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.json.JSONObject;
import org.resistance.site.Game;
import org.resistance.site.GameTracker;
import org.resistance.site.web.utils.ShabaJdbcUserDetailsManager;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/** @author Alex Aiezza */
@Controller
public class ResistanceController {
  private final Log LOGGER;

  private final ShabaJdbcUserDetailsManager USER_MAN;

  private final GameTracker GAME_TRACKER;

  @Autowired
  public ResistanceController(
      @Qualifier("Resistance_Logger") Log logger,
      ShabaJdbcUserDetailsManager userManager,
      GameTracker gameTracker) {
    LOGGER = logger;
    USER_MAN = userManager;
    GAME_TRACKER = gameTracker;
  }

  @RequestMapping("game")
  public ModelAndView getGameView(@RequestParam(required = true) String gameID) {
    final ModelAndView gamePage = new ModelAndView("game");

    ShabaUser user = USER_MAN.getShabaUser();

    // GAME MONITOR SCENARIO
    if (user == null) {
      gamePage.addObject("user", "");
    } else {
      user.eraseCredentials();
      gamePage.addObject("user", new JSONObject(user));
    }

    gamePage.addObject("game", gameID);

    return gamePage;
  }

  /**
   * Client informs server that it has learned it's role
   *
   * @param principal
   */
  @MessageMapping("learnedRole")
  public void playerLearnedRole(Principal principal) {
    Game g = GAME_TRACKER.getGameUsernameIsPlayerIn(principal.getName());
    if (g == null) {
      return;
    }
    g.setPlayerRoleLearned(principal.getName());
    // LOGGER.debug( );
  }

  @MessageMapping("addTeammate")
  public void addTeammate(Principal principal, @Payload String username) {
    Game g = GAME_TRACKER.getGameUsernameIsPlayerIn(username);
    g.addTeammate(principal.getName(), username);
  }

  @MessageMapping("dismissTeammate")
  public void dismissTeammate(Principal principal, @Payload String username) {
    Game g = GAME_TRACKER.getGameUsernameIsPlayerIn(username);
    g.dismissTeammate(principal.getName(), username);
  }

  @RequestMapping(method = GET, value = "missionTeamSubmitted")
  @ResponseBody
  public List<String> missionTeamSubmitted() {
    ShabaUser user = USER_MAN.getShabaUser();

    Game g = GAME_TRACKER.getGameUsernameIsPlayerIn(user.getUsername());

    if (!g.submitTeam(user.getUsername())) {
      ArrayList<String> out = new ArrayList<String>();
      out.add("You cannot submit this team!");
      return out;
    }

    return Collections.<String>emptyList();
  }

  @MessageMapping("teamVote")
  public void teamVote(Principal principal, @Payload boolean _vote) {
    String username = principal.getName();

    Game game = GAME_TRACKER.getGameUsernameIsPlayerIn(username);

    game.submitTeamVote(username, _vote);
  }

  @MessageMapping("missionVote")
  public void missionVote(Principal principal, @Payload boolean _vote) {
    String username = principal.getName();

    Game game = GAME_TRACKER.getGameUsernameIsPlayerIn(username);

    game.submitMissionVote(username, _vote);
  }
}
