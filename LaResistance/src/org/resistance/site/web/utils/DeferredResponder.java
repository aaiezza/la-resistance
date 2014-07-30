package org.resistance.site.web.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.springframework.web.context.request.async.DeferredResult;

public abstract class DeferredResponder <T, R> extends Observable
{
    protected final Map<DeferredResult<List<T>>, Pair<ShabaUser, R>> REQUESTS;

    protected final Log                                              LOGGER;

    protected DeferredResponder( Log logger )
    {
        REQUESTS = new ConcurrentHashMap<DeferredResult<List<T>>, Pair<ShabaUser, R>>();
        LOGGER = logger;
    }

    /**
     * Call this method whenever this object is updated for everyone to see
     * 
     * @param requestBiConsumer
     */
    protected synchronized final void sendResults()
    {
        REQUESTS.forEach( ( deferredResult, userAndRestrictor ) -> {
            doBeforeSendingSingleResult( deferredResult, userAndRestrictor );
            List<T> messages = getResult( userAndRestrictor.getValue() );
            deferredResult.setResult( messages );
        } );
    }

    /**
     * Optional overriding to perform task before each result is set
     */
    protected synchronized void doBeforeSendingSingleResult(
            DeferredResult<List<T>> deferredResult,
            Pair<ShabaUser, R> userAndRestrictor )
    {}

    /**
     * Need to implement this method to get the List of objects that were
     * deferred.
     * 
     * @param resultRestrictor
     *            may be <code>null</code>, but code accordingly.
     * @return The list of deferred objects that are the result.
     */
    protected abstract List<T> getResult( R resultRestrictor );

    /**
     * 
     * @param user
     * @param resultRestrictor
     * @return
     */
    public synchronized final DeferredResult<List<T>> registerRequest( ShabaUser user, R resultRestrictor )
    {
        final DeferredResult<List<T>> deferredResult = new DeferredResult<List<T>>( null,
                Collections.emptyList() );
        REQUESTS.put( deferredResult, new Pair<ShabaUser, R>( user, resultRestrictor ) );
        LOGGER.debug( String.format( "\n\t\t%s is WAITING for a result", user.getUsername() ) );

        deferredResult.onCompletion( new Runnable()
        {
            @Override
            public void run()
            {
                LOGGER.debug( String.format( "\n\t\tThe wait is OVER for %s", user.getUsername() ) );
                REQUESTS.remove( deferredResult );
            }
        } );

        List<T> result = getResult( resultRestrictor );
        if ( !result.isEmpty() )
        {
            deferredResult.setResult( result );
        }

        return deferredResult;
    }

    public static class Pair <K, V>
    {

        /**
         * Key of this <code>Pair</code>.
         */
        private K key;

        /**
         * Gets the key for this pair.
         * 
         * @return key for this pair
         */
        public K getKey()
        {
            return key;
        }

        /**
         * Value of this this <code>Pair</code>.
         */
        private V value;

        /**
         * Gets the value for this pair.
         * 
         * @return value for this pair
         */
        public V getValue()
        {
            return value;
        }

        /**
         * Sets the value for this pair.
         */
        public void setValue( V value )
        {
            this.value = value;
        }

        /**
         * Creates a new pair
         * 
         * @param key
         *            The key for this pair
         * @param value
         *            The value to use for this pair
         */
        public Pair( K key, V value )
        {
            this.key = key;
            this.value = value;
        }

        /**
         * <p>
         * <code>String</code> representation of this <code>Pair</code>.
         * </p>
         *
         * <p>
         * The default name/value delimiter '=' is always used.
         * </p>
         *
         * @return <code>String</code> representation of this <code>Pair</code>
         */
        @Override
        public String toString()
        {
            return key + "=" + value;
        }

        /**
         * <p>
         * Generate a hash code for this <code>Pair</code>.
         * </p>
         *
         * <p>
         * The hash code is calculated using both the name and the value of the
         * <code>Pair</code>.
         * </p>
         *
         * @return hash code for this <code>Pair</code>
         */
        @Override
        public int hashCode()
        {
            // name's hashCode is multiplied by an arbitrary prime number (13)
            // in order to make sure there is a difference in the hashCode
            // between
            // these two parameters:
            // name: a value: aa
            // name: aa value: a
            return key.hashCode() * 13 + ( value == null ? 0 : value.hashCode() );
        }

        /**
         * <p>
         * Test this <code>Pair</code> for equality with another
         * <code>Object</code>.
         * </p>
         *
         * <p>
         * If the <code>Object</code> to be tested is not a <code>Pair</code> or
         * is <code>null</code>, then this method returns <code>false</code>.
         * </p>
         *
         * <p>
         * Two <code>Pair</code>s are considered equal if and only if both the
         * names and values are equal.
         * </p>
         *
         * @param o
         *            the <code>Object</code> to test for equality with this
         *            <code>Pair</code>
         * @return <code>true</code> if the given <code>Object</code> is equal
         *         to this <code>Pair</code> else <code>false</code>
         */
        @Override
        public boolean equals( Object o )
        {
            if ( this == o )
                return true;
            if ( o instanceof Pair )
            {
                @SuppressWarnings ( "rawtypes" )
                Pair pair = (Pair) o;
                if ( key != null ? !key.equals( pair.key ) : pair.key != null )
                    return false;
                if ( value != null ? !value.equals( pair.value ) : pair.value != null )
                    return false;
                return true;
            }
            return false;
        }

    }
}
