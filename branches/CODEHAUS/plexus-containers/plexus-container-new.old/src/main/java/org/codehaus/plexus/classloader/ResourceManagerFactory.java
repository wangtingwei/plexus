package org.codehaus.plexus.classloader;

import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.factory.AbstractPlexusFactory;
import org.codehaus.plexus.logging.LoggerManager;

public class ResourceManagerFactory
    extends AbstractPlexusFactory
{
    public static DefaultResourceManager create( Configuration defaultConfiguration,
                                          Configuration configuration,
                                          LoggerManager loggerManager,
                                          ClassLoader classLoader )
        throws Exception
    {
        String implementation;

        if ( configuration.getChild( "resource-manager", false ) != null )
        {
            implementation =
                configuration.getChild( "resource-manager" ).getChild( "implementation" ).getValue();
        }
        else
        {
            implementation =
                defaultConfiguration.getChild( "resource-manager" ).getChild( "implementation" ).getValue();
        }


        DefaultResourceManager rm =
            (DefaultResourceManager) getInstance( implementation, classLoader );

        rm.setClassLoader( classLoader );
        rm.enableLogging( loggerManager.getLogger( "resource-manager" ) );
        rm.configure( configuration.getChild( "resources" ) );

        return rm;
    }
}
