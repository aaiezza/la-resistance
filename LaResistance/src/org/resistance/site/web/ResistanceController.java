package org.resistance.site.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.resistance.site.BoardFactory;
import org.resistance.site.Game;
import org.resistance.site.Player;
import org.resistance.site.utils.GameTracker;
import org.resistance.site.utils.VoteCounter;
import org.resistance.site.web.utils.ShabaJdbcUserDetailsManager;
import org.resistance.site.web.utils.ShabaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alex Aiezza
 */
@Controller
public class ResistanceController
{
    private static final String               VOTED = "_voted_";

    private final Log                         LOGGER;

    private final ShabaJdbcUserDetailsManager USER_MAN;

    private final VoteCounter                 VOTE_COUNTER;

    private final ArrayList<HttpSession>      SESSION_TRACKER;

    private final BoardFactory                BOARD_FACTORY;

    private final GameTracker                 GAME_TRACKER;

    @Autowired
    public ResistanceController(
        @Qualifier ( "Resistance_Logger" ) Log logger,
        ShabaJdbcUserDetailsManager userManager,
        VoteCounter voteCounter,
        BoardFactory boardFactory,
        GameTracker gameTracker )
    {
        LOGGER = logger;
        USER_MAN = userManager;
        VOTE_COUNTER = voteCounter;
        SESSION_TRACKER = new ArrayList<HttpSession>();
        BOARD_FACTORY = boardFactory;
        GAME_TRACKER = gameTracker;
    }

    @RequestMapping ( "/voter" )
    public ModelAndView getVotePanel( HttpSession session )
    {
        LOGGER.info( "Someone wants to vote!" );

        return new ModelAndView( "voter", "canVote", session.getAttribute( VOTED ) );
    }

    @RequestMapping ( method = POST, value = "/vote", headers = { "vote" } )
    @ResponseBody
    public HashMap<String, String> vote( HttpSession session, @RequestHeader (
        required = true,
        value = "vote" ) boolean _vote )
    {
        if ( !SESSION_TRACKER.contains( session ) )
            SESSION_TRACKER.add( session );

        HashMap<String, String> json = new HashMap<String, String>();

        if ( session.getAttribute( VOTED ) == null || !(boolean) ( session.getAttribute( VOTED ) ) )
        {
            VOTE_COUNTER.vote( _vote );
            session.setAttribute( VOTED, true );
            json.put( "response", "You Voted!" );
            LOGGER.info( "Someone voted!" );
        } else
        {
            LOGGER.info( "Someone who already voted tried voting!" );
            json.put( "response", "You've already voted..." );
        }

        return json;
    }

    @RequestMapping ( method = POST, value = "/results" )
    @ResponseBody
    public HashMap<String, Integer> getVoteResults()
    {
        LOGGER.debug( "Grab Those Results" );
        VoteCounter.VoteResults results = VOTE_COUNTER.getResults();

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put( "approves", results.approves() );
        map.put( "denies", results.denies() );

        return map;
    }

    @RequestMapping ( method = GET, value = "/results" )
    public ModelAndView getVoteResultsPage()
    {
        LOGGER.info( "Grab That Results Page" );

        return new ModelAndView( "results", getVoteResults() );
    }


    @RequestMapping ( method = POST, value = "/reset" )
    @ResponseStatus ( value = HttpStatus.OK )
    public void resetResults()
    {
        VOTE_COUNTER.resetVoteCounter();

        for ( HttpSession session : SESSION_TRACKER )
        {
            session.setAttribute( VOTED, false );
        }

        LOGGER.info( "Vote Counter Reset" );
    }

    @RequestMapping ( method = POST, value = "createGame" )
    @ResponseBody
    public Game createGame()
    {
        ShabaUser user = USER_MAN.getShabaUser();
        
        Game g = new Game( new Player( user.getUsername() ) );
        
        if( !GAME_TRACKER.registerGame( g ) )
        {
            g = null;
        }
        
        return g;
    }
}
