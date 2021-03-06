package org.codehaus.plexus.jdo;

/*
 * Copyright 2005 The Apache Software Foundation.
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

import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ConfigurableJdoFactory
    extends JdoFactory
{
    String ROLE = ConfigurableJdoFactory.class.getName();

    void setPersistenceManagerFactoryClass( String persistenceManagerFactoryClass );

    void setDriverName( String driverName );

    void setUrl( String url );

    void setUserName( String userName );

    void setPassword( String password );

    void setProperty( String key, String value );

    Properties getProperties();
}
