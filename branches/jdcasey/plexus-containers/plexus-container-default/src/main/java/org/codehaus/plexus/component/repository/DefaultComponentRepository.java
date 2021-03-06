package org.codehaus.plexus.component.repository;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.CompositionResolver;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @todo We need to process component descriptors from a specified configuration file in addition
 * to component descriptors that are stored in the JAR along with the component. So we need to be
 * able to process a directory of components as we now can package any number of components
 * in a JAR which will all be described by a components.xml file in the top-level of the JAR.
 */
public class DefaultComponentRepository
    extends AbstractLogEnabled
    implements ComponentRepository
{
    private static String COMPONENTS = "components";

    private static String COMPONENT = "component";

    private PlexusConfiguration configuration;

    private Map componentDescriptorMaps;

    private Map componentDescriptors;

    private CompositionResolver compositionResolver;

    public DefaultComponentRepository()
    {
        componentDescriptors = new HashMap();

        componentDescriptorMaps = new HashMap();
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    protected PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public boolean hasComponent( String role )
    {
        return componentDescriptors.containsKey( role );
    }

    public boolean hasComponent( String role, String roleHint )
    {
        return componentDescriptors.containsKey( role + roleHint );
    }

    public Map getComponentDescriptorMap( String role )
    {
        return (Map) componentDescriptorMaps.get( role );
    }

    public ComponentDescriptor getComponentDescriptor( String key )
    {
        return (ComponentDescriptor) componentDescriptors.get( key );
    }

    public void setClassLoader( ClassLoader classLoader )
    {
        // do nothing...
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void configure( PlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }

    public void initialize()
        throws ComponentRepositoryException
    {
        initializeComponentDescriptors();
    }

    public void initializeComponentDescriptors()
        throws ComponentRepositoryException
    {
        initializeComponentDescriptorsFromUserConfiguration();
    }

    private void initializeComponentDescriptorsFromUserConfiguration()
        throws ComponentRepositoryException
    {
        PlexusConfiguration[] componentConfigurations = configuration.getChild( COMPONENTS ).getChildren( COMPONENT );

        for ( int i = 0; i < componentConfigurations.length; i++ )
        {
            addComponentDescriptor( componentConfigurations[i] );
        }
    }

    // ----------------------------------------------------------------------
    //  Component Descriptor processing.
    // ----------------------------------------------------------------------

    public void addComponentDescriptor( PlexusConfiguration configuration )
        throws ComponentRepositoryException
    {
        ComponentDescriptor componentDescriptor = null;

        try
        {
            componentDescriptor = PlexusTools.buildComponentDescriptor( configuration );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ComponentRepositoryException( "Cannot unmarshall component descriptor:", e );
        }

        addComponentDescriptor( componentDescriptor );
    }

    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException
    {
        try
        {
            validateComponentDescriptor( componentDescriptor );
        }
        catch ( ComponentImplementationNotFoundException e )
        {
            throw new ComponentRepositoryException( "Component descriptor validation failed: ", e );
        }

        String role = componentDescriptor.getRole();

        String roleHint = componentDescriptor.getRoleHint();

        if ( roleHint != null )
        {
            if ( componentDescriptors.containsKey( role ) )
            {
                ComponentDescriptor desc = (ComponentDescriptor) componentDescriptors.get( role );
                if ( desc.getRoleHint() == null )
                {
                    String message = "Component descriptor " + componentDescriptor.getHumanReadableKey() +
                        " has a hint, but there are other implementations that don't";
                    throw new ComponentRepositoryException( message );
                }
            }

            Map map = (Map) componentDescriptorMaps.get( role );

            if ( map == null )
            {
                map = new HashMap();

                componentDescriptorMaps.put( role, map );
            }

            map.put( roleHint, componentDescriptor );
        }
        else
        {
            if ( componentDescriptorMaps.containsKey( role ) )
            {
                String message = "Component descriptor " + componentDescriptor.getHumanReadableKey() +
                    " has no hint, but there are other implementations that do";
                throw new ComponentRepositoryException( message );
            }
            else if ( componentDescriptors.containsKey( role ) )
            {
                if ( !componentDescriptors.get( role ).equals( componentDescriptor ) )
                {
                    String message = "Component role " + role +
                        " is already in the repository and different to attempted addition of " +
                        componentDescriptor.getHumanReadableKey();
                    throw new ComponentRepositoryException( message );
                }
            }
        }

        try
        {
            compositionResolver.addComponentDescriptor( componentDescriptor );
        }
        catch ( CompositionException e )
        {
            throw new ComponentRepositoryException( e.getMessage(), e );
        }

        componentDescriptors.put( componentDescriptor.getComponentKey(), componentDescriptor );
        
        // We need to be able to lookup by role only (in non-collection situations), even when the 
        // component has a roleHint.
        if ( !componentDescriptors.containsKey( role ) )
        {
            componentDescriptors.put( role, componentDescriptor );
        }
    }

    public void validateComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentImplementationNotFoundException
    {
        // Make sure the component implementation classes can be found.
        // Make sure ComponentManager implementation can be found.
        // Validate lifecycle.
        // Validate the component configuration.
        // Validate the component profile if one is used.
    }

    public List getComponentDependencies( ComponentDescriptor componentDescriptor )
    {
        return compositionResolver.getRequirements( componentDescriptor.getComponentKey() );
    }
}
