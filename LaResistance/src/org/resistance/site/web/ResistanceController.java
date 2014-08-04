package org.resistance.site.web;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.resistance.site.Game;
import org.resistance.site.GameTracker;
import org.resistance.site.Player;
import org.resistance.site.web.utils.ShabaJdbcUserDetailsManager;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alex Aiezza
 */
@Controller
public class ResistanceController
{
    private final Log                         LOGGER;

    private final ShabaJdbcUserDetailsManager USER_MAN;

    private final GameTracker                 GAME_TRACKER;

    @Autowired
    public ResistanceController(
        @Qualifier ( "Resistance_Logger" ) Log logger,
        ShabaJdbcUserDetailsManager userManager,
        GameTracker gameTracker )
    {
        LOGGER = logger;
        USER_MAN = userManager;
        GAME_TRACKER = gameTracker;
    }

    @RequestMapping ( "/voter" )
    public ModelAndView getVotePanel()
    {
        LOGGER.info( "Someone wants to vote!" );

        return new ModelAndView( "voter" );
    }

    @RequestMapping ( method = POST, value = "vote/{gameID}", headers = { "_vote" } )
    @ResponseBody
    public HashMap<String, String> vote( @PathVariable String gameID, @RequestHeader (
        required = true ) boolean _vote )
    {
        HashMap<String, String> json = new HashMap<String, String>();

        ShabaUser user = USER_MAN.getShabaUser();

        Game game = GAME_TRACKER.getGame( gameID );

        Player player = game.getPlayerFromUsername( user.getUsername() );

        json.put( "successVoting", String.valueOf( game.submitVote( player, _vote ) ) );

        return json;
    }
}
