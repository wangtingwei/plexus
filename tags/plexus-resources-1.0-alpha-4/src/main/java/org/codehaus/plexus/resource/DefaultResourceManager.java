package org.codehaus.plexus.resource;

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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;
import org.codehaus.plexus.resource.loader.ResourceLoader;
import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.codehaus.plexus.resource.loader.FileResourceLoader;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 * @plexus.component
 */
public class DefaultResourceManager
    extends AbstractLogEnabled
    implements ResourceManager
{
    /** @plexus.requirement role="org.codehaus.plexus.resource.loader.ResourceLoader" */
    private Map resourceLoaders;

    /** @plexus.configuration */
    private File outputDirectory;

    // ----------------------------------------------------------------------
    // ResourceManager Implementation
    // ----------------------------------------------------------------------

    //TODO: we should tell the user where we have searched for the resource so if it fails they can
    // debug easier (jvz)
    public InputStream getResourceAsInputStream( String name )
        throws ResourceNotFoundException
    {
        InputStream is = null;

        for ( Iterator i = resourceLoaders.values().iterator(); i.hasNext(); )
        {
            ResourceLoader resourceLoader = (ResourceLoader) i.next();

            try
            {
                is = resourceLoader.getResourceAsInputStream( name );

                getLogger().debug( "The resource " + "'" + name + "'" + " found using the " + resourceLoader + "." );

                break;
            }
            catch ( ResourceNotFoundException e )
            {
                // ignore and continue to the next loader
            }
        }

        if ( is == null )
        {
            throw new ResourceNotFoundException( name );
        }

        return is;
    }

    public File getResourceAsFile( String name )
        throws ResourceNotFoundException, FileResourceCreationException
    {
        return getResourceAsFile( name, null );
    }

    public File getResourceAsFile( String name,
                                   String outputPath )
        throws ResourceNotFoundException, FileResourceCreationException
    {
        // Optimization for File to File fetches
        File f = FileResourceLoader.getResourceAsFile( name, outputPath, outputDirectory );

        if ( f != null )
        {
            return f;
        }

        // End optimization

        InputStream is = getResourceAsInputStream( name );

        InputStreamReader reader = new InputStreamReader( is );

        Writer writer;

        File outputFile;

        if ( outputPath == null )
        {
            outputFile = FileUtils.createTempFile( "plexus-resources", "tmp", null );
        }
        else
        {
            if ( outputDirectory != null )
            {
                outputFile = new File( outputDirectory, outputPath );
            }
            else
            {
                outputFile = new File( outputPath );
            }
        }

        try
        {
            if ( !outputFile.getParentFile().exists() )
            {
                outputFile.getParentFile().mkdirs();
            }

            writer = new FileWriter( outputFile );

            IOUtil.copy( reader, writer );
        }
        catch ( IOException e )
        {
            throw new FileResourceCreationException( "Cannot create file-based resource.", e );
        }

        return outputFile;
    }

    public File resolveLocation( String name,
                                 String outputPath )
        throws IOException
    {
        // Honour what the original locator does and return null ...
        try
        {
            return getResourceAsFile( name, outputPath );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    public File resolveLocation( String name )
        throws IOException
    {
        // Honour what the original locator does and return null ...

        System.out.println( "name = " + name );

        try
        {
            return getResourceAsFile( name );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    public void addSearchPath( String id,
                               String path )
    {
        ResourceLoader loader = (ResourceLoader) resourceLoaders.get( id );

        loader.addSearchPath( path );
    }
}
