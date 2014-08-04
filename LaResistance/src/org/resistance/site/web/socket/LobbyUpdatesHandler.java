package org.resistance.site.web.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.resistance.site.web.utils.ChatLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class LobbyUpdatesHandler extends TextWebSocketHandler
{
    @Autowired
    private ChatLogger CHAT_LOGGER;

    private final Log  LOGGER = LogFactory.getLog( LobbyUpdatesHandler.class );

    @Override
    public void afterConnectionEstablished( WebSocketSession session ) throws Exception
    {
        super.afterConnectionEstablished( session );
        LOGGER.info( "WE is IN!" );
        session.sendMessage( new TextMessage( "[\"hey\", \"hoe!\"]" ) );
    }

    @Override
    public void afterConnectionClosed( WebSocketSession session, CloseStatus status )
            throws Exception
    {
        super.afterConnectionClosed( session, status );
        LOGGER.info( "WE is OUT!" );
    }

    @Override
    public void handleTextMessage( WebSocketSession session, TextMessage message ) throws Exception
    {
        super.handleTextMessage( session, message );

        LOGGER.info( String.format( "Not sure what to do with this...\n\n%s\n\n", message ) );
    }

}
