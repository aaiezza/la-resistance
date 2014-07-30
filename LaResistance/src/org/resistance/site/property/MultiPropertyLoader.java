package org.resistance.site.property;

import java.io.IOException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @author Alex Aiezza
 */
public class MultiPropertyLoader
{

    protected static final String PROPERTY_FILE_SYSTEM_PROPERTY = "applicationProperties";
    protected static final String RESISTANCE_PROPERTY_FILE      = "local-resources/laResistance.properties";

    private static final String   PROPERTY_NOT_DEFINED_ERROR    = "No properties defined.  Properties are critical for the application to function.";
    private static final String   PROPERTY_NOT_LOADED_ERROR     = "The properties were not loaded. Properties are critical for the application to function.";

    private boolean load( Resource resource )
    {
        try
        {
            System.getProperties().load( resource.getInputStream() );
            return true;
        }
        catch ( IOException e )
        {
            return false;
        }
    }

    public static void loadProperty()
    {
        new MultiPropertyLoader().load();
    }

    public void load()
    {
        String [] propertyFiles;
        String propertyValue;
        propertyValue = System.getProperty( PROPERTY_FILE_SYSTEM_PROPERTY );
        propertyValue = ( propertyValue == null ) ? "" : propertyValue + ";";
        propertyValue += RESISTANCE_PROPERTY_FILE;
        propertyValue = ( propertyValue == null ) ? "" : propertyValue + ";";

        if ( !propertyValue.equals( "" ) )
        {
            propertyFiles = propertyValue.split( ";" );
            load( propertyFiles );
        }
        else
        {
            throw new IllegalArgumentException( PROPERTY_NOT_DEFINED_ERROR );
        }
    }

    public void load( String... propertyFiles )
    {
        for ( String file : propertyFiles )
        {
            Resource resource = new FileSystemResource( file );
            boolean loaded = load( resource );
            if ( !loaded )
            {
                throw new IllegalArgumentException( PROPERTY_NOT_LOADED_ERROR );
            }
        }
    }
}
