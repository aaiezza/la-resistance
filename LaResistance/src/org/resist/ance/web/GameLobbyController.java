package org.resist.ance.web;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.resist.ance.web.utils.PrintBeans;
import org.resist.ance.web.utils.ShabaJdbcUserDetailsManager;
import org.resist.ance.web.utils.ShabaUser;
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

    @RequestMapping ( method = POST, value = "updateUsersOnline" )
    @ResponseBody
    public HashMap<String, Object> updateUsersOnline() throws InterruptedException
    {
        ShabaUser user = USER_MAN.getShabaUser();

        synchronized ( USER_TRACKER )
        {
            LOGGER.debug( String.format( "\n\t\t%s is waiting for the chat to update!!!",
                user.getUsername() ) );
            USER_TRACKER.wait();
        }

        LOGGER.debug( String.format( "\n\t\tThe wait is OVER for %s", user.getUsername() ) );

        return getUsersOnline();
    }

    @RequestMapping ( method = POST, value = "usersOnline" )
    @ResponseBody
    public HashMap<String, Object> getUsersOnline()
    {
        HashMap<String, Object> json = new HashMap<String, Object>();

        json.put( "users", USER_TRACKER.getLoggedInUsers() );

        return json;
    }
}
