package org.resist.ance.web;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.resist.ance.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GameLobbyController
{
    private final Log                       LOGGER;

    private final ArrayList<String>         LOBBY_CHAT;

    private final ArrayList<Game>           GAMES;

    private final ArrayList<Authentication> PLAYERS_ONLINE;

    @Autowired
    public GameLobbyController(
        @Qualifier ( "GameLobby_Logger" ) Log logger,
        @Qualifier ( "OnlinePlayerList" ) ArrayList<Authentication> playersOnline )
    {
        LOGGER = logger;

        LOBBY_CHAT = new ArrayList<String>();

        GAMES = new ArrayList<Game>();

        PLAYERS_ONLINE = playersOnline;
    }

    @RequestMapping ( method = POST, value = "playersOnline" )
    @ResponseBody
    public HashMap<String, Object> getPlayersOnline( HttpSession session )
    {
        HashMap<String, Object> json = new HashMap<String, Object>();


        return json;
    }


}
