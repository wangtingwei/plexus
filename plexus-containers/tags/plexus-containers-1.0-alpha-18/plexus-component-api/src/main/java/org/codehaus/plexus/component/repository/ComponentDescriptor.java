package org.codehaus.plexus.component.repository;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * Component instantiation description.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class ComponentDescriptor
{
    private String alias = null;

    private String role = null;

    private String roleHint = null;

    private String implementation = null;

    private String version = null;

    private String componentType = null;

    private PlexusConfiguration configuration = null;

    private String instantiationStrategy = null;

    private String lifecycleHandler = null;

    private String componentProfile = null;

    private List requirements;

    private String componentFactory;

    private String componentComposer;

    private String componentConfigurator;

    private String description;

    private String realmId;

    // ----------------------------------------------------------------------
    // These two fields allow for the specification of an isolated class realm
    // and dependencies that might be specified in a component configuration
    // setup by a user i.e. this is here to allow isolation for components
    // that are not picked up by the discovery mechanism.
    // ----------------------------------------------------------------------

    private boolean isolatedRealm;

    private List dependencies;

    // ----------------------------------------------------------------------

    private ComponentSetDescriptor componentSetDescriptor;

    // ----------------------------------------------------------------------
    //  Instance methods
    // ----------------------------------------------------------------------

    /**
     * Returns a unique key created as a combination of role+hint.
     * @return a unique key for this component
     */
    public String getComponentKey()
    {
        if ( getRoleHint() != null )
        {
            return getRole() + getRoleHint();
        }

        return getRole();
    }

    /**
     * Returns a human-friendly key, suitable for display.
     * @return a human-friendly key
     */
    public String getHumanReadableKey()
    {
        StringBuffer key = new StringBuffer();

        key.append("role: '" + role + "'" );

        key.append( ", implementation: '" + implementation  + "'" );

        if ( roleHint != null )
        {
            key.append( ", role hint: '" + roleHint  + "'" );
        }

        if ( alias != null )
        {
            key.append( ", alias: '" + alias  + "'" );
        }

        return key.toString();
    }

    /**
     * Returns an alias for this component. An alias as an
     * alternate name other than the normal key.
     * @return an alias for this component
     */
    public String getAlias()
    {
        return alias;
    }

    /**
     * Sets the alias for this component.
     * @param alias alternate name to set
     */
    public void setAlias( String alias )
    {
        this.alias = alias;
    }

    /**
     * Returns the role of this component.
     * @return the role of this component
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Sets the role of this component.
     * @param role this component's role
     */
    public void setRole( String role )
    {
        this.role = role;
    }

    /**
     * Returns the role-hint of this component.
     * @return the role-hint of this component
     */
    public String getRoleHint()
    {
        return roleHint;
    }

    /**
     * Sets the role-hint of this component.
     * @param roleHint this component's role-hint
     */
    public void setRoleHint( String roleHint )
    {
        this.roleHint = roleHint;
    }

    /**
     * Returns the implementation of this componet. Implementation
     * is a string denoting a FQCN in normal Java components, or
     * some other name or file for other component factory implementations.
     * @return the implementation of this componet's role.
     */
    public String getImplementation()
    {
        return implementation;
    }

    /**
     * Sets the implementation of this componet.
     * @param implementation string denoting a FQCN in normal Java components,
     *  or some other name or file for other component factory implementations
     */
    public void setImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    /**
     * Returns a specific point in a components's project timeline.
     * i.e. version 1, or 2.1.4
     * @return a specific point in a components's project timeline
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the point in a components's project development timeline
     * @param version the components's version
     */
    public void setVersion( String version )
    {
        this.version = version;
    }

    /**
     * Returns the type of this component.
     * @return the type of this component
     */
    public String getComponentType()
    {
        return componentType;
    }

    /**
     * Sets this component's type.
     * @param componentType the type to set
     */
    public void setComponentType( String componentType )
    {
        this.componentType = componentType;
    }

    /**
     * Returns the type of instantiation strategy for this component.
     * @return the type of instantiation strategy for this component
     */
    public String getInstantiationStrategy()
    {
        return instantiationStrategy;
    }

    /**
     * Returns configuration values defined for this component.
     * @return configuration values defined for this component
     */
    public PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * Sets the configuration hierarchy for this component.
     * @param configuration the configuration hierarchy to set
     */
    public void setConfiguration( PlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }

    /**
     * Returns true if this component has a configuration.
     * @return true if this component has a configuration
     */
    public boolean hasConfiguration()
    {
        return configuration != null;
    }

    /**
     * Returns the lifecycle-handler for this component.
     * @return the lifecycle-handler for this component
     */
    public String getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    /**
     * Sets the lifecycle-handler for this component. For example,
     * "basic", "passive", "bootstrap".
     * @param lifecycleHandler the lifecycle handler string to set
     */
    public void setLifecycleHandler( String lifecycleHandler )
    {
        this.lifecycleHandler = lifecycleHandler;
    }

    public String getComponentProfile()
    {
        return componentProfile;
    }

    public void setComponentProfile( String componentProfile )
    {
        this.componentProfile = componentProfile;
    }

    /**
     * Add a project requirement to this component.
     * @param requirement the requirement to add
     */
    public void addRequirement( final ComponentRequirement requirement )
    {
        getRequirements().add( requirement );
    }

    /**
     * Adds a list of requirements to this component.
     * @param requirements the requirements to add
     */
    public void addRequirements( List requirements )
    {
        getRequirements().addAll( requirements );
    }

    /**
     * Returns all project requirements of this component.
     * @return all project requirements of this component
     */
    public List getRequirements()
    {
        if ( requirements == null )
        {
            requirements = new ArrayList();
        }
        return requirements;
    }

    /**
     * Returns an id of the factory used to create this component.
     * @return an id of the factory used to create this component
     */
    public String getComponentFactory()
    {
        return componentFactory;
    }

    /**
     * Sets the id of the factory to use to create this component. For
     * example, "jruby" will use a JRuby factory.
     * @param componentFactory
     */
    public void setComponentFactory( String componentFactory )
    {
        this.componentFactory = componentFactory;
    }

    /**
     * Returns the ID of the type of composer this component will use. For example,
     * "setter" or "field" for the different types of dependency injection.
     * @return the ID of the type of composer this component will use
     */
    public String getComponentComposer()
    {
        return componentComposer;
    }

    /**
     * Sets a representation of the composer this component uses.
     * @param componentComposer string representation of the composer to use
     */
    public void setComponentComposer( String componentComposer )
    {
        this.componentComposer = componentComposer;
    }

    /**
     * Return a human-readable description of this component.
     * @return a human-readable description of this component
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets a description of this component for users to read.
     * @param description a human-readable description of this component
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /**
     * Sets the instantiation-strategy for this component. For example,
     * "container".
     * @param instantiationStrategy
     */
    public void setInstantiationStrategy( String instantiationStrategy )
    {
        this.instantiationStrategy = instantiationStrategy;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Returns true if this may be in an isolated classrealm.
     * @return true if this may be in an isolated classrealm
     */
    public boolean isIsolatedRealm()
    {
        return isolatedRealm;
    }

    /**
     * Sets the component set descriptor of components and dependencies for
     * this component.
     * @param componentSetDescriptor the component set descriptor of components
     *  and dependencies
     */
    public void setComponentSetDescriptor( ComponentSetDescriptor componentSetDescriptor )
    {
        this.componentSetDescriptor = componentSetDescriptor;
    }

    /**
     * Returns the component set descriptor.
     * @return the component set descriptor
     */
    public ComponentSetDescriptor getComponentSetDescriptor()
    {
        return componentSetDescriptor;
    }

    /**
     * Sets that this component may be in an isolated classrealm.
     * @param isolatedRealm true if this component may be in an isolated
     *  classrealm
     */
    public void setIsolatedRealm( boolean isolatedRealm )
    {
        this.isolatedRealm = isolatedRealm;
    }

    /**
     * Returns a List of dependencies of this component.
     * @return a List of dependencies of this component
     */
    public List getDependencies()
    {
        return dependencies;
    }

    /**
     * Returns the type of component configurator for this project. For
     * example "basic" for normal, or "map-oriented" for map oriented
     * components.
     * @return the type of component configurator for this project
     */
    public String getComponentConfigurator()
    {
        return componentConfigurator;
    }

    /**
     * Sets the type of component configurator for this project.
     * @param componentConfigurator 
     */
    public void setComponentConfigurator( String componentConfigurator )
    {
        this.componentConfigurator = componentConfigurator;
    }

    /**
     * The ClassRealm that this component lives under.
     * @return ClassRealm that this component lives under
     */
    public String getRealmId()
    {
        return realmId;
    }

    /**
     * Set the id of the ClassRealm that this component lives under.
     * @param realmId ClassRealm id
     */
    public void setRealmId( String realmId )
    {
        this.realmId = realmId;
    }

    // Component identity established here!
    public boolean equals(Object other)
    {
        if(!(other instanceof ComponentDescriptor))
        {
            return false;
        }
        else
        {
            ComponentDescriptor otherDescriptor = (ComponentDescriptor) other;

            boolean isEqual = true;

            String role = getRole();
            String otherRole = otherDescriptor.getRole();

            isEqual = isEqual && ( role == otherRole || role.equals( otherRole ) );

            String roleHint = getRoleHint();
            String otherRoleHint = otherDescriptor.getRoleHint();

            isEqual = isEqual && ( roleHint == otherRoleHint || roleHint.equals( otherRoleHint ) );

            return isEqual;
        }
    }

    public String toString()
    {
        return getClass().getName() + " [role: '" + getRole() + "', hint: '" + getRoleHint() + "', realm: "
            + ( realmId == null ? "NULL" : "'" + realmId + "'" ) + "]";
    }

    public int hashCode()
    {
        int result = getRole().hashCode() + 1;

        String hint = getRoleHint();

        if( hint != null )
        {
            result += hint.hashCode();
        }

        return result;
    }
}
