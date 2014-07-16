package org.resist.ance.web;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.resist.ance.web.utils.UserTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Alex Aiezza
 */
@Controller
public class GameLobbyController
{
    private final Log         LOGGER;

    private final UserTracker USER_TRACKER;

    @Autowired
    public GameLobbyController(
        @Qualifier ( "GameLobby_Logger" ) Log logger,
        @Qualifier ( "userTracker" ) UserTracker userTracker )
    {
        LOGGER = logger;

        USER_TRACKER = userTracker;
    }

    @RequestMapping ( method = POST, value = "playersOnline" )
    @ResponseBody
    public HashMap<String, Object> getPlayersOnline( HttpSession session )
    {
        HashMap<String, Object> json = new HashMap<String, Object>();

        json.put( "users", USER_TRACKER );

        return json;
    }
}
