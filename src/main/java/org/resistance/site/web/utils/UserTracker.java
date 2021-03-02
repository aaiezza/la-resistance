package org.resistance.site.web.utils;

import static java.lang.String.format;

import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.resistance.site.Game;
import org.resistance.site.GameTracker;
import org.resistance.site.web.chat.ChatLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/** @author Alex Aiezza */
@Service
@ManagedResource
public class UserTracker extends MessageRelayer<List<ShabaUser>> {
  public static final String RELAY_DESTINATION = "/topic/activeUsers";

  public static final String SUBSCRIPTION_URL = "/activeUsers";

  private static final String LOGGED_IN = "%s successfully Logged in!";

  private static final String LOGGED_OUT = "%s has Logged out!";

  private static final String CHAT_UPDATE_ONLINE = "%s is ONLINE!";

  private static final String CHAT_UPDATE_OFFLINE = "%s has LEFT!";

  private final List<ShabaUser> onlineUsers;

  private final ChatLogger CHAT_LOGGER;

  private final GameTracker GAME_TRACKER;

  @Autowired
  private UserTracker(
      @Qualifier("UserTracker_Logger") Log loginLogger,
      ChatLogger chatLogger,
      GameTracker gameTracker) {
    super(loginLogger, RELAY_DESTINATION);
    onlineUsers = Collections.synchronizedList(new ArrayList<ShabaUser>());
    CHAT_LOGGER = chatLogger;
    GAME_TRACKER = gameTracker;
  }

  synchronized void addUser(final ShabaUser user) {
    if (!onlineUsers.contains(user)) {
      onlineUsers.add(user);
      LOGGER.info(format(LOGGED_IN, user));

      CHAT_LOGGER.systemUpdate(CHAT_UPDATE_ONLINE, user.getUsername());

      broadcastPayload();
    }
  }

  synchronized void removeUser(final ShabaUser user) {
    onlineUsers.remove(user);
    LOGGER.info(format(LOGGED_OUT, user));

    CHAT_LOGGER.systemUpdate(CHAT_UPDATE_OFFLINE, user.getUsername());
    Game g = GAME_TRACKER.getGameFromHostUsername(user.getUsername());
    if (g != null)
      GAME_TRACKER.unRegisterGame(
          GAME_TRACKER.getGameFromHostUsername(user.getUsername()).getGameID());

    g = GAME_TRACKER.getGameUsernameIsPlayerIn(user.getUsername());
    if (g != null) g.dismissPlayer(g.getPlayerFromUsername(user.getUsername()));

    broadcastPayload();
  }

  @Override
  protected synchronized List<ShabaUser> getPayload() {
    return getLoggedInUsers();
  }

  @Override
  public synchronized List<ShabaUser> onSubscription(ShabaUser user) {
    return onSubscription(user, UPDATE_ALL_SUBSCRIBERS);
  }

  @ManagedOperation(description = "View Active Users")
  public List<String> getStringLoggedInUsers() {
    return new ArrayList<String>(
        Collections2.transform(getLoggedInUsers(), (user) -> user.getUsername()));
  }

  public synchronized List<ShabaUser> getLoggedInUsers() {
    onlineUsers.forEach((user) -> user.eraseCredentials());
    return onlineUsers;
  }

  public synchronized boolean contains(UserDetails user) {
    if (user == null) {
      return false;
    }
    return onlineUsers.contains(user);
  }

  @Override
  public String toString() {
    return onlineUsers.toString();
  }
}
