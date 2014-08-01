package org.resistance.site.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.resistance.site.Game;
import org.resistance.site.GameTracker;
import org.resistance.site.Player;
import org.resistance.site.web.utils.PrintBeans;
import org.resistance.site.web.utils.ShabaJdbcUserDetailsManager;
import org.resistance.site.web.utils.ShabaUser;
import org.resistance.site.web.utils.UserTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alex Aiezza
 */
@Controller
public class GameLobbyController
{
    private final Log                         LOGGER;

    private final UserTracker                 USER_TRACKER;

    private final ShabaJdbcUserDetailsManager USER_MAN;

    private final GameTracker                 GAME_TRACKER;

    @Autowired
    public GameLobbyController(
        @Qualifier ( "GameLobby_Logger" ) Log logger,
        UserTracker userTracker,
        ShabaJdbcUserDetailsManager userManager,
        PrintBeans beanPrinter,
        GameTracker gameTracker )
    {
        LOGGER = logger;

        USER_TRACKER = userTracker;

        USER_MAN = userManager;

        GAME_TRACKER = gameTracker;

        LOGGER.debug( beanPrinter.printBeans() );
    }

    @RequestMapping ( method = GET, value = "/gameLobby" )
    public ModelAndView getGameLobbyPage()
    {
        ShabaUser user = USER_MAN.getShabaUser();

        LOGGER.debug( String.format( "%s is looking for recruitment!", user.getUsername() ) );

        return new ModelAndView( "gameLobby", "username", user.getUsername() );
    }

    @RequestMapping ( method = GET, value = "usersOnline/{forceGet}" )
    @ResponseBody
    public DeferredResult<List<ShabaUser>> updateUsersOnline( @PathVariable boolean forceGet )
            throws InterruptedException
    {
        ShabaUser user = USER_MAN.getShabaUser();

        return USER_TRACKER.registerRequest( user, forceGet );
    }

    // TODO BETTER ERROR HANDLING!!!
    @RequestMapping ( method = POST, value = "createGame" )
    @ResponseBody
    public List<String> createGame()
    {
        ShabaUser user = USER_MAN.getShabaUser();

        if ( !GAME_TRACKER.registerGame( user.getUsername() ) )
        {
            ArrayList<String> out = new ArrayList<String>();
            out.add( "You are already a member of a resistance!" );
            return out;
        }

        return Collections.<String> emptyList();
    }

    @RequestMapping ( method = GET, value = "updateGame/{gameID}" )
    @ResponseBody
    public DeferredResult<List<Game>> getGameUpdate(
            @PathVariable String gameID,
            @RequestParam boolean forceGet )
    {
        ShabaUser user = USER_MAN.getShabaUser();

        Game g = GAME_TRACKER.getGame( gameID );

        if ( g != null )
        {
            return g.registerRequest( user, forceGet );
        }
        
        return null;
    }

    @RequestMapping ( method = POST, value = "cancelGame/{gameID}" )
    @ResponseBody
    public List<String> cancelGame( @PathVariable String gameID )
    {
        ShabaUser user = USER_MAN.getShabaUser();

        if ( !GAME_TRACKER.getGame( gameID ).getHost().getName().equals( user.getUsername() ) ||
                !GAME_TRACKER.unRegisterGame( gameID ) )
        {
            ArrayList<String> out = new ArrayList<String>();
            out.add( "This Game cannot be Canceled" );
            return out;
        }

        return Collections.<String> emptyList();
    }

    @RequestMapping ( method = GET, value = "activeGames/{forceGet}" )
    @ResponseBody
    public DeferredResult<List<Game>> getActiveGames( @PathVariable boolean forceGet )
    {
        return GAME_TRACKER.registerRequest( USER_MAN.getShabaUser(), forceGet );
    }

    // TODO BETTER ERROR HANDLING!!!
    @RequestMapping ( method = POST, value = "joinGame/{gameID}" )
    @ResponseBody
    public List<String> joinGame( @PathVariable String gameID )
    {
        ShabaUser user = USER_MAN.getShabaUser();

        if ( !GAME_TRACKER.addPlayerToGame( new Player( user.getUsername(), gameID ) ) )
        {
            ArrayList<String> out = new ArrayList<String>();
            out.add( "You cannot join this game!" );
            return out;
        }

        return Collections.<String> emptyList();
    }

    // TODO BETTER ERROR HANDLING!!!
    @RequestMapping ( method = POST, value = "unJoinGame/{gameID}" )
    @ResponseBody
    public List<String> unJoinGame( @PathVariable String gameID )
    {
        ShabaUser user = USER_MAN.getShabaUser();

        if ( !GAME_TRACKER.getGame( gameID )
                .dismissPlayer( new Player( user.getUsername(), gameID ) ) )
        {
            ArrayList<String> out = new ArrayList<String>();
            out.add( "You cannot leave this game!" );
            return out;
        }

        return Collections.<String> emptyList();
    }

    // TODO This is the big one!
    @RequestMapping ( method = POST, value = "startGame/{gameID}" )
    @ResponseBody
    public List<String> startGame( @PathVariable String gameID )
    {
        ShabaUser user = USER_MAN.getShabaUser();

        if ( !GAME_TRACKER.getGame( gameID ).startGame( new Player( user.getUsername(), gameID ) ) )
        {
            ArrayList<String> out = new ArrayList<String>();
            out.add( "You cannot start this game!" );
            return out;
        }

        return Collections.<String> emptyList();
    }

}
