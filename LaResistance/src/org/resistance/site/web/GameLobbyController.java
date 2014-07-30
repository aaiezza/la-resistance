package org.resistance.site.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.apache.commons.logging.Log;
import org.resistance.site.web.utils.PrintBeans;
import org.resistance.site.web.utils.ShabaJdbcUserDetailsManager;
import org.resistance.site.web.utils.ShabaUser;
import org.resistance.site.web.utils.UserTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Autowired
    public GameLobbyController(
        @Qualifier ( "GameLobby_Logger" ) Log logger,
        UserTracker userTracker,
        ShabaJdbcUserDetailsManager userManager,
        PrintBeans beanPrinter )
    {
        LOGGER = logger;

        USER_TRACKER = userTracker;

        USER_MAN = userManager;

        LOGGER.debug( beanPrinter.printBeans() );
    }

    @RequestMapping ( method = GET, value = "/gameLobby" )
    public ModelAndView getGameLobbyPage()
    {
        ShabaUser user = USER_MAN.getShabaUser();

        LOGGER.info( String.format( "%s is looking for recruitment!", user.getUsername() ) );

        return new ModelAndView( "gameLobby" );
    }

    @RequestMapping ( method = POST, value = "usersOnline/{blank}" )
    @ResponseBody
    public DeferredResult<List<ShabaUser>> updateUsersOnline( @PathVariable boolean blank )
            throws InterruptedException
    {
        ShabaUser user = USER_MAN.getShabaUser();

        return USER_TRACKER.registerRequest( user, blank );
    }

}
