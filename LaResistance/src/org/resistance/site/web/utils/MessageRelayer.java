package org.resistance.site.web.utils;

import org.apache.commons.logging.Log;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public abstract class MessageRelayer <T>
{
    private static final String     SUBSCRIBE_LOG          = "\n\t\t%s has subscribed to [%s]";

    private static final String     BROADCAST_LOG          = "Broadcasting Payload to Relay @:[%s]";

    private static final String     USER_BROADCAST_LOG     = BROADCAST_LOG + " for user %s";

    public static final int         NO_UPDATE              = 0;

    public static final int         UPDATE_USER            = 1;

    public static final int         UPDATE_ALL_SUBSCRIBERS = 2;

    protected SimpMessagingTemplate TEMPLATE;

    protected final Log             LOGGER;

    protected MessageRelayer( Log logger )
    {
        this( logger, null );
    }

    protected MessageRelayer( Log logger, SimpMessagingTemplate template )
    {
        LOGGER = logger;
        TEMPLATE = template;
    }

    protected final void broadcastPayload()
    {
        doBeforeBroadcastingPayload();
        LOGGER.debug( String.format( BROADCAST_LOG, getRelayDestination() ) );
        TEMPLATE.convertAndSend( getRelayDestination(), getPayload() );
    }

    protected final void broadcastPayload( String user )
    {
        doBeforeBroadcastingPayload();
        LOGGER.debug( String.format( USER_BROADCAST_LOG, getRelayDestination(), user ) );
        TEMPLATE.convertAndSendToUser( user, getRelayDestination(), getPayload() );
    }

    /**
     * Optional overriding to perform task before broadcasting payload
     */
    protected void doBeforeBroadcastingPayload()
    {}

    protected abstract T getPayload();

    protected abstract String getRelayDestination();


    /**
     * Intercept the subscription event from a client in a Controller and call
     * this method to enable logging of subscriptions
     * 
     * @param user
     */
    protected final void onSubscription( ShabaUser user, int initialAction )
    {
        LOGGER.debug( String.format( SUBSCRIBE_LOG, user.getUsername(), getRelayDestination() ) );

        switch ( initialAction )
        {
        case UPDATE_ALL_SUBSCRIBERS:
            broadcastPayload();
            break;
        case UPDATE_USER:
            broadcastPayload( user.getUsername() );
            break;
        case NO_UPDATE:
        default:
        }
    }

    public abstract void onSubscription( ShabaUser user );

    public final void setTemplate( SimpMessagingTemplate template )
    {
        TEMPLATE = template;
    }
}
