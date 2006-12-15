/**
 *
 * Copyright 2006
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.codehaus.plexus.artifact;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.velocity.VelocityComponent;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * @author John Tolentino
 */
public class DefaultArtifactReport
    extends AbstractLogEnabled
    implements ArtifactReport, Startable
{
    /**
     * Velocity context to use
     */
    private VelocityContext context;

    /**
     * Velocity component to use
     */
    private VelocityComponent velocity;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
    {
        getLogger().info( "Starting DefaultArtifactReport component." );
    }

    public void stop()
    {
        getLogger().info( "Stopping DefaultArtifactReport component." );
    }

    // ----------------------------------------------------------------------
    // ArtifactReport Implementation
    // ----------------------------------------------------------------------


    public void generate( PrintStream result )
    {
        context.put( "artifactName", "sample" );
        context.put( "artifactGroupId", "sample" );
        context.put( "artifactId", "sample" );
        context.put( "artifactVersion", "sample" );
        context.put( "artifactRepository", "sample" );

        try
        {
            Template template = velocity.getEngine().getTemplate( "org/codehaus/plexus/artifact/ArtifactReport.vm" );
            PrintWriter writer = new PrintWriter( result );
            template.merge( context, writer );
            writer.flush();

        }
        catch ( Exception e )
        {

        }


    }
}