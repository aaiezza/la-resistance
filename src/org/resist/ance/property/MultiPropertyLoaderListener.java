package org.resist.ance.property;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class MultiPropertyLoaderListener implements ServletContextListener
{

    private final MultiPropertyLoader _propertyLoader;

    public MultiPropertyLoaderListener()
    {
        _propertyLoader = new MultiPropertyLoader();
    }

    public MultiPropertyLoaderListener( MultiPropertyLoader doPropertyLoader )
    {
        _propertyLoader = doPropertyLoader;
    }

    @Override
    public void contextDestroyed( ServletContextEvent servletContextEvent )
    {}

    @Override
    public void contextInitialized( ServletContextEvent servletContextEvent )
    {
        try
        {
            _propertyLoader.load();
        }
        catch ( Exception e )
        {
            System.err.println( e.getMessage() );
            System.exit( 1 );
        }
    }
}
