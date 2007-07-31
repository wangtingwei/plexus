package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;

/**
 * @author Jason van Zyl
 */
public class AppInitializationPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        String name = "plexus.application." + context.getApplicationId();

        // ----------------------------------------------------------------------------
        // Create the container and start
        // ----------------------------------------------------------------------------

        DefaultPlexusContainer applicationContainer;

        try
        {
            addLibJars( context, name );

            // This call will initialise and start the container

            InputStream in = context.getAppConfigurationFile() == null ? null : new FileInputStream( context
                .getAppConfigurationFile().getAbsoluteFile() );

            ContainerConfiguration cc= new DefaultContainerConfiguration();
            cc.setName( name );
            cc.setContext( context.getContextValues() );
            cc.setClassWorld( context
                .getAppRuntimeProfile().getApplicationWorld() );
            cc.setContainerConfiguration( context.getAppConfigurationFile()==null?null:context.getAppConfigurationFile().getAbsolutePath() );
            cc.setParentContainer( context.getAppServerContainer() );

            applicationContainer = new DefaultPlexusContainer( cc );

//            applicationContainer = new DefaultPlexusContainer( name, context.getContextValues(), context
//                .getAppRuntimeProfile().getApplicationWorld(), in, context.getAppServerContainer() );
        }
        catch ( PlexusContainerException e )
        {
            throw new AppDeploymentException( "Error starting container.", e );
        }
        catch ( FileNotFoundException e )
        {
            throw new AppDeploymentException( "Cannot find application.xml configuration file", e );
        }

        context.getAppRuntimeProfile().setApplicationContainer( applicationContainer );
    }

    private void addLibJars( AppDeploymentContext context, String realmName )
        throws AppDeploymentException
    {
        // not using fileutils here since that can throw an IOException
        File[] jars = context.getAppLibDirectory().listFiles( new FileFilter()
        {
            public boolean accept( File pathname )
            {
                return pathname.isFile() && pathname.toString().endsWith( ".jar" );
            }
        } );

        // should not happen
        if ( jars == null )
        {
            getLogger().warn( "Not a directory: " + context.getAppLibDirectory() );
        }
        else
        {
            ClassWorld appWorld = context.getAppRuntimeProfile().getApplicationWorld();
            ClassRealm realm;

            try
            {
                realm = appWorld.getRealm( realmName );
            }
            catch ( NoSuchRealmException e1 )
            {
                throw new AppDeploymentException( "Realm not found: " + realmName, e1 );
            }

            for ( int i = 0; i < jars.length; i++ )
            {
                try
                {
                    realm.addURL( jars[i].toURI().toURL() );
                }
                catch ( MalformedURLException e )
                {
                    getLogger().warn( "Error converting file " + jars[i] + " to URL", e );
                }
            }
        }
    }
}
