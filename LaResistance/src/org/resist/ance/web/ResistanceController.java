package org.resist.ance.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.resist.ance.BoardFactory;
import org.resist.ance.utils.VoteCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResistanceController
{
    private static final String          VOTED = "_voted_";

    private final Log                    LOGGER;

    private final VoteCounter            VOTE_COUNTER;

    private final ArrayList<HttpSession> SESSION_TRACKER;

    private final BoardFactory           BOARD_FACTORY;

    @Autowired
    public ResistanceController(
        @Qualifier ( "Resistance_Logger" ) Log logger,
        VoteCounter voteCounter,
        BoardFactory boardFactory )
    {
        LOGGER = logger;
        VOTE_COUNTER = voteCounter;
        SESSION_TRACKER = new ArrayList<HttpSession>();
        BOARD_FACTORY = boardFactory;
    }

    public void sayHello()
    {
        LOGGER.info( "HELLO!" );
    }

    @RequestMapping ( "/" )
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

    public void createGame()
    {

    }
}
