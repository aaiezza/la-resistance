package org.resistance.site.web.utils;

import org.apache.commons.logging.Log;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;

/**
 * @author Alex Aiezza
 * @version Aug 5, 2014
 * @param <T>
 */
public abstract class MessageRelayer <T>
{
    private static final String     SUBSCRIBE_LOG          = "\n\t\t%s has subscribed to [%s]";

    private static final String     BROADCAST_LOG          = "Broadcasting Payload to Relay @:[%s]";

    private static final String     USER_BROADCAST_LOG     = BROADCAST_LOG + " for user %s";

    /**
     * This value can be used in the initalAction parameter for the
     * <code>protected final {@link #onSubscription}</code> method. <br/>
     * On initial subscription to a {@link MessageRelayer} instance, using this
     * value in that subclass will result in there being no updates broadcast.
     */
    public static final int         NO_UPDATE              = 0;

    /**
     * This value can be used in the initalAction parameter for the
     * <code>protected final {@link #onSubscription}</code> method. <br/>
     * On initial subscription to a {@link MessageRelayer} instance, using this
     * value in that subclass will result in the user who subscribed to be
     * updated of the object's {@link #getPayload payload}.
     */
    public static final int         UPDATE_USER            = 1;

    /**
     * This value can be used in the initalAction parameter for the
     * <code>protected final {@link #onSubscription}</code> method. <br/>
     * On initial subscription to a {@link MessageRelayer} instance, using this
     * value in that subclass will result in everyone who is subscribed to be
     * updated of the object's {@link #getPayload payload}.
     */
    public static final int         UPDATE_ALL_SUBSCRIBERS = 2;

    protected SimpMessagingTemplate TEMPLATE;

    protected final Log             LOGGER;

    private final String            relayDestination;

    /**
     * The reason this method exists and is called in all of the subclasses
     * (which are all spring beans getting instantiated at server startup) is
     * because those subclass beans need to be defined at the root context in
     * order for injection into the custom
     * {@link TrackingUsernamePasswordAuthenticationFilter Authentication
     * Filter} to occur. <br/>
     * <br/>
     * In the constructors of the Controllers however, the
     * {@link #setTemplate(SimpMessagingTemplate)} method provides a way of
     * setting the {@link SimpMessagingTemplate} for the MessageRelayer subtype. <br/>
     * <br/>
     * The {@link SimpleBrokerMessageHandler Simple Message Broker} spring
     * provides needs to be declared in the Resistance Dispatcher servlet and
     * not at root context. That means the SimpMessagingTemplate for that
     * broker's message relayer is not a created bean until this context is
     * declared. This circular dependency makes it impossible to inject those
     * beans at server start up.
     * 
     * @param logger
     */
    protected MessageRelayer( Log logger, String defaultDestination )
    {
        this( logger, null, defaultDestination );
    }

    protected MessageRelayer( Log logger, SimpMessagingTemplate template, String defaultDestination )
    {
        LOGGER = logger;
        relayDestination = defaultDestination;

        if ( template != null )
        {
            setTemplate( template );
        } else
        {
            TEMPLATE = template;
        }
    }

    /**
     * If there is no default destination, this will set it to null
     * 
     * @param logger
     * @param template
     */
    protected MessageRelayer( Log logger, SimpMessagingTemplate template )
    {
        LOGGER = logger;
        TEMPLATE = template;
        relayDestination = null;
    }

    /**
     * Broadcast payload to all subscribers
     */
    protected final void broadcastPayload()
    {
        doBeforeBroadcastingPayload();
        LOGGER.debug( String.format( BROADCAST_LOG, getRelayDestination() ) );
        TEMPLATE.convertAndSend( getRelayDestination(), getPayload() );
    }

    /**
     * @param user
     *            The user to broadcast payload to
     */
    protected final void broadcastPayload( String user )
    {
        doBeforeBroadcastingPayload();
        LOGGER.debug( String.format( USER_BROADCAST_LOG, getRelayDestination(), user ) );
        TEMPLATE.convertAndSendToUser( user, getRelayDestination(), getPayload() );
    }

    /**
     * @param user
     *            The user to broadcast payload to
     * @param customPayload
     *            A custom payload for this specific time and user
     */
    protected final void broadcastPayload( String user, T customPayload )
    {
        doBeforeBroadcastingPayload();
        LOGGER.debug( String.format( USER_BROADCAST_LOG, getRelayDestination(), user ) );
        TEMPLATE.convertAndSendToUser( user, getRelayDestination(), customPayload );
    }

    /**
     * Optional overriding to perform task before broadcasting payload
     */
    protected void doBeforeBroadcastingPayload()
    {}

    /**
     * @return The message to be broadcast
     */
    protected abstract T getPayload();

    /**
     * The only reason to override this method in a subclass is if instances
     * that are updating their subscribers are unique every time.
     * 
     * @return The URL corresponding to the subscription channel that a STOMP
     *         client would be subscribed to to receive updates.
     */
    protected String getRelayDestination()
    {
        return relayDestination;
    }

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

    /**
     * 
     * @param user
     *            The user subscribing to this
     */
    public abstract void onSubscription( ShabaUser user );

    /**
     * @param template
     *            The template to use for message relaying
     */
    public final void setTemplate( SimpMessagingTemplate template )
    {
        TEMPLATE = template;
    }
}
