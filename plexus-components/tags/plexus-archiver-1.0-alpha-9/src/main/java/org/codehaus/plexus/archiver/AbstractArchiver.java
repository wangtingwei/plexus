package org.codehaus.plexus.archiver;

/**
 *
 * Copyright 2004 The Apache Software Foundation
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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.util.DefaultArchivedFileSet;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.util.FilterSupport;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;

/**
 * @version $Id$
 */
public abstract class AbstractArchiver
    extends AbstractLogEnabled
    implements Archiver, Contextualizable, FilterEnabled, FinalizerEnabled
{
    /**
     * Default value for the dirmode attribute.
     */
    public static final int DEFAULT_DIR_MODE = UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;

    /**
     * Default value for the filemode attribute.
     */
    public static final int DEFAULT_FILE_MODE = UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;

    private Logger logger;

    private File destFile;

    private Map filesMap = new LinkedHashMap();

    private Map dirsMap = new LinkedHashMap();

    private int defaultFileMode = DEFAULT_FILE_MODE;

    private boolean includeEmptyDirs = true;

    private int defaultDirectoryMode = DEFAULT_DIR_MODE;

    private boolean forced = true;

    private FilterSupport filterSupport;

    private List finalizers;

    private File dotFileDirectory;

    // contextualized.
    private ArchiverManager archiverManager;

    private Set explodedArchiveDirs = new HashSet();

    public void setDefaultFileMode( int mode )
    {
        defaultFileMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.FILE_FLAG;
    }

    public int getDefaultFileMode()
    {
        return defaultFileMode;
    }

    public void setDefaultDirectoryMode( int mode )
    {
        defaultDirectoryMode = ( mode & UnixStat.PERM_MASK ) | UnixStat.DIR_FLAG;
    }

    public int getDefaultDirectoryMode()
    {
        return defaultDirectoryMode;
    }

    public boolean getIncludeEmptyDirs()
    {
        return includeEmptyDirs;
    }

    public void setIncludeEmptyDirs( boolean includeEmptyDirs )
    {
        this.includeEmptyDirs = includeEmptyDirs;
    }

    public void addDirectory( File directory )
        throws ArchiverException
    {
        addDirectory( directory, "" );
    }

    public void addDirectory( File directory, String prefix )
        throws ArchiverException
    {
        addDirectory( directory, prefix, null, null );
    }

    public void addDirectory( File directory, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addDirectory( directory, "", includes, excludes );
    }

    public void addDirectory( File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        DefaultFileSet fileSet = new DefaultFileSet();
        fileSet.setDirectory( directory );
        fileSet.setPrefix( prefix );
        fileSet.setIncludes( includes );
        fileSet.setExcludes( excludes );
        fileSet.setIncludingEmptyDirectories( includeEmptyDirs );
        addFileSet( fileSet );
    }

    public void addFileSet( FileSet fileSet )
        throws ArchiverException
    {
        DirectoryScanner scanner = new DirectoryScanner();

        if ( fileSet.getIncludes() != null )
        {
            scanner.setIncludes( fileSet.getIncludes() );
        }

        if ( fileSet.getExcludes() != null )
        {
            scanner.setExcludes( fileSet.getExcludes() );
        }

        File directory = fileSet.getDirectory();
        if ( directory == null )
        {
            throw new ArchiverException( "The file sets base directory is null." );
        }
        if ( !directory.isDirectory() )
        {
            throw new ArchiverException( directory.getAbsolutePath() + " isn't a directory." );
        }

        String basedir = directory.getAbsolutePath();
        scanner.setBasedir( basedir );
        scanner.scan();

        String prefix = fileSet.getPrefix() == null ? "" : fileSet.getPrefix();
        ArchiverFileInfo fileInfo = new ArchiverFileInfo();
        FileSelector[] fileSelectors = fileSet.getFileSelectors();
        if ( fileSet.isIncludingEmptyDirectories() )
        {
            String[] dirs = scanner.getIncludedDirectories();

            for ( int i = 0; i < dirs.length; i++ )
            {
                String name = dirs[i];
                String sourceDir = name.replace( '\\', '/' );
                File dir = new File( basedir, sourceDir );
                fileInfo.setFile( dir );
                fileInfo.setName( name );
                if ( isSelected( fileSelectors, fileInfo ) )
                {
                    String targetDir = prefix + sourceDir;

                    getDirs().put( targetDir,
                                   ArchiveEntry.createDirectoryEntry( targetDir, dir,
                                                                      getDefaultDirectoryMode() ) );
                }
            }
        }

        String[] files = scanner.getIncludedFiles();

        for ( int i = 0; i < files.length; i++ )
        {
            String file = files[i];
            String sourceFile = file.replace( '\\', '/' );
            File source = new File( basedir, sourceFile );
            fileInfo.setName( file );
            fileInfo.setFile( source );
            if ( isSelected( fileSelectors, fileInfo ) )
            {
                String targetFile = prefix + sourceFile;
                addFile( source, targetFile );
            }
        }
    }

    private boolean isSelected( FileSelector[] fileSelectors, FileInfo fileInfo )
        throws ArchiverException
    {
        if ( fileSelectors != null )
        {
            for ( int i = 0;  i < fileSelectors.length;  i++ )
            {
                try
                {
                    if ( !fileSelectors[i].isSelected( fileInfo ) )
                    {
                        return false;
                    }
                }
                catch ( IOException e )
                {
                    throw new ArchiverException( "Failed to check, whether "
                                                 + fileInfo.getName()
                                                 + " is selected.", e );
                }
            }
        }
        return true;
    }

    private static class ArchiverFileInfo implements FileInfo
    {
        private String name;
        private File file;

        void setFile( File file )
        {
            this.file = file;
        }

        void setName( String name )
        {
            this.name = name;
        }

        public InputStream getContents() throws IOException
        {
            return new FileInputStream( file );
        }

        public String getName()
        {
            return name;
        }

        public boolean isDirectory()
        {
            return file.isDirectory();
        }

        public boolean isFile()
        {
            return file.isFile();
        }
    }

    public void addFile( File inputFile, String destFileName )
        throws ArchiverException
    {
        addFile( inputFile, destFileName, getDefaultFileMode() );
    }

    public void addFile( File inputFile, String destFileName, int permissions )
        throws ArchiverException
    {
        if ( !inputFile.isFile() || !inputFile.exists() )
        {
            throw new ArchiverException( inputFile.getAbsolutePath() + " isn't a file." );
        }

        FileInputStream fileStream = null;

        destFileName = destFileName.replace( '\\', '/' );

        try
        {
            // do a null check here, to avoid creating a file stream if there are no filters...
            if ( filterSupport != null )
            {
                fileStream = new FileInputStream( inputFile );

                if ( include( fileStream, destFileName ) )
                {
                    filesMap.put( destFileName, ArchiveEntry.createFileEntry( destFileName, inputFile, permissions ) );
                }
            }
            else
            {
                filesMap.put( destFileName, ArchiveEntry.createFileEntry( destFileName, inputFile, permissions ) );
            }
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Failed to determine inclusion status for: " + inputFile, e );
        }
        catch ( ArchiveFilterException e )
        {
            throw new ArchiverException( "Failed to determine inclusion status for: " + inputFile, e );
        }
        finally
        {
            IOUtil.close( fileStream );
        }
    }

    // TODO: convert this to Collection?
    // Only con is that some archivers change the filename
    // to contain just forward slashes; they could update
    // the Name of the ArchiveEntry..?
    public Map getFiles()
    {
        if ( !includeEmptyDirs )
        {
            return filesMap;
        }

        Map resources = new LinkedHashMap();

        resources.putAll( getDirs() );

        resources.putAll( filesMap );

        return resources;
    }

    public File getDestFile()
    {
        return destFile;
    }

    public void setDestFile( File destFile )
    {
        this.destFile = destFile;

        if ( destFile != null )
        {
            destFile.getParentFile().mkdirs();
        }
    }

    protected Logger getLogger()
    {
        if ( logger == null )
        {
            if ( super.getLogger() != null )
            {
                logger = super.getLogger();
            }
            else
            {
                logger = new ConsoleLogger( Logger.LEVEL_INFO, "console" );
            }
        }

        return logger;
    }

    public Map getDirs()
    {
        return dirsMap;
    }

    public void addArchivedFileSet( final ArchivedFileSet fileSet )
        throws ArchiverException
    {
        File archiveFile = fileSet.getArchive();
        UnArchiver unArchiver;
        try
        {
            unArchiver = archiverManager.getUnArchiver( archiveFile );

            if ( unArchiver instanceof FilterEnabled )
            {
                ( (FilterEnabled) unArchiver ).setArchiveFilters( Collections.EMPTY_LIST );
            }
        }
        catch ( NoSuchArchiverException e )
        {
            throw new ArchiverException( "Error adding archived file-set. UnArchiver not found for: " + archiveFile, e );
        }

        final File tempDir = FileUtils.createTempFile( "archived-file-set.", ".tmp", null );
        explodedArchiveDirs.add( tempDir );

        // FIXME: It's not a good idea to litter the temp dir with these file-set directories...
        // However, the IDEs cannot accommodate shutdown hooks, so I'm taking this out.
//        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable(){
//
//            public void run()
//            {
//                try
//                {
//                    FileUtils.deleteDirectory( tempDir );
//                }
//                catch ( IOException e )
//                {
//                    getLogger().debug( "Error deleting temp directory: " + tempDir, e );
//                }
//            }
//
//        } ) );

        tempDir.mkdirs();

        unArchiver.setSourceFile( archiveFile );
        unArchiver.setDestDirectory( tempDir );
        unArchiver.extract();

        addFileSet( new FileSet(){
            public File getDirectory()
            {
                return tempDir;
            }

            public String[] getExcludes()
            {
                return fileSet.getExcludes();
            }

            public FileSelector[] getFileSelectors()
            {
                return fileSet.getFileSelectors();
            }

            public String[] getIncludes()
            {
                return fileSet.getIncludes();
            }

            public String getPrefix()
            {
                return fileSet.getPrefix();
            }

            public boolean isCaseSensitive()
            {
                return fileSet.isCaseSensitive();
            }

            public boolean isIncludingEmptyDirectories()
            {
                return fileSet.isIncludingEmptyDirectories();
            }

            public boolean isUsingDefaultExcludes()
            {
                return fileSet.isUsingDefaultExcludes();
            }
        } );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( File archiveFile, String prefix, String[] includes, String[] excludes )
        throws ArchiverException
    {
        DefaultArchivedFileSet fileSet = new DefaultArchivedFileSet();
        fileSet.setArchive( archiveFile );
        fileSet.setPrefix( prefix );
        fileSet.setIncludes( includes );
        fileSet.setExcludes( excludes );
        fileSet.setIncludingEmptyDirectories( includeEmptyDirs );
        addArchivedFileSet( fileSet );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( File archiveFile, String prefix )
        throws ArchiverException
    {
        addArchivedFileSet( archiveFile, prefix, null, null );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( File archiveFile, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addArchivedFileSet( archiveFile, null, includes, excludes );
    }

    /**
     * @since 1.0-alpha-7
     */
    public void addArchivedFileSet( File archiveFile )
        throws ArchiverException
    {
        addArchivedFileSet( archiveFile, null, null, null );
    }

    /**
     * Allows us to pull the ArchiverManager instance out of the container without
     * causing a chicken-and-egg instantiation/composition problem.
     */
    public void contextualize( Context context )
        throws ContextException
    {
        PlexusContainer container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

        try
        {
            archiverManager = (ArchiverManager) container.lookup( ArchiverManager.ROLE );
        }
        catch ( ComponentLookupException e )
        {
            throw new ContextException( "Error retrieving ArchiverManager instance: " + e.getMessage(), e );
        }
    }

    public boolean isForced()
    {
        return forced;
    }

    public void setForced( boolean forced )
    {
        this.forced = forced;
    }

    public void setArchiveFilters( List filters )
    {
        filterSupport = new FilterSupport( filters, getLogger() );
    }

    public void addArchiveFinalizer( ArchiveFinalizer finalizer )
    {
        if ( finalizers == null )
        {
            finalizers = new ArrayList();
        }

        finalizers.add( finalizer );
    }

    public void setArchiveFinalizers( List archiveFinalizers )
    {
        finalizers = archiveFinalizers;
    }

    public void setDotFileDirectory( File dotFileDirectory )
    {
        this.dotFileDirectory = dotFileDirectory;
    }

    protected boolean isUptodate()
    {
        File zipFile = getDestFile();
        long destTimestamp = zipFile.lastModified();
        if ( destTimestamp == 0 )
        {
            getLogger().debug( "isUp2date: false (Destination " + zipFile.getPath() + " not found.)" );
            return false; // File doesn't yet exist
        }

        Map archiveEntries = getFiles();
        if ( ( archiveEntries == null ) || archiveEntries.isEmpty() )
        {
            getLogger().debug( "isUp2date: false (No input files.)" );
            return false; // No timestamp to compare
        }

        for ( Iterator iter = archiveEntries.values().iterator(); iter.hasNext(); )
        {
            ArchiveEntry entry = (ArchiveEntry) iter.next();
            long l = entry.getFile().lastModified();
            if ( l == 0 )
            {
                // Don't know what to do. Safe thing is to assume not up2date.
                getLogger().debug( "isUp2date: false (Input file " + entry.getFile().getPath() + " not found.)" );
                return false;
            }
            if ( l > destTimestamp )
            {
                getLogger().debug( "isUp2date: false (Input file " + entry.getFile().getPath() + " is newer.)" );
                return false;
            }
        }

        getLogger().debug( "isUp2date: true" );
        return true;
    }

    protected boolean checkForced()
    {
        if ( !isForced() && isSupportingForced() && isUptodate() )
        {
            getLogger().debug( "Archive " + getDestFile() + " is uptodate." );
            return false;
        }
        return true;
    }

    public boolean isSupportingForced()
    {
        return false;
    }

    protected List getArchiveFinalizers()
    {
        return finalizers;
    }

    protected void runArchiveFinalizers()
        throws ArchiverException
    {
        if ( finalizers != null )
        {
            for ( Iterator it = finalizers.iterator(); it.hasNext(); )
            {
                ArchiveFinalizer finalizer = (ArchiveFinalizer) it.next();

                finalizer.finalizeArchiveCreation( this );
            }
        }
    }

    private boolean include( InputStream in, String path )
        throws ArchiveFilterException
    {
        return ( filterSupport == null ) || filterSupport.include( in, path );
    }

    public final void createArchive()
        throws ArchiverException, IOException
    {
        validate();
        try
        {
            try
            {
                if ( dotFileDirectory != null )
                {
                    addArchiveFinalizer( new DotDirectiveArchiveFinalizer( dotFileDirectory ) );
                }

                runArchiveFinalizers();

                execute();
            }
            finally
            {
                close();
            }
        }
        catch ( IOException e )
        {
            String msg = "Problem creating " + getArchiveType() + ": " + e.getMessage();

            StringBuffer revertBuffer = new StringBuffer();
            if ( !revert( revertBuffer ) )
            {
                msg += revertBuffer.toString();
            }

            throw new ArchiverException( msg, e );
        }
        finally
        {
            cleanUpInternal();

            cleanUp();
        }
    }

    protected boolean hasVirtualFiles()
    {
        if ( finalizers != null )
        {
            for ( Iterator it = finalizers.iterator(); it.hasNext(); )
            {
                ArchiveFinalizer finalizer = (ArchiveFinalizer) it.next();

                List virtualFiles = finalizer.getVirtualFiles();

                if ( ( virtualFiles != null ) && !virtualFiles.isEmpty() )
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean revert( StringBuffer messageBuffer )
    {
        return true;
    }

    protected void validate()
        throws ArchiverException, IOException
    {
    }

    private void cleanUpInternal()
    {
        for ( Iterator it = explodedArchiveDirs.iterator(); it.hasNext(); )
        {
            File dir = (File) it.next();

            if ( dir.exists() )
            {
                try
                {
                    FileUtils.forceDelete( dir );
                }
                catch ( IOException e )
                {
                    getLogger().debug( "Failed to delete exploded-archive temp directory: " + dir, e );
                }
            }
        }
    }

    protected abstract String getArchiveType();

    protected abstract void close()
        throws IOException;

    protected abstract void cleanUp();

    protected abstract void execute()
        throws ArchiverException, IOException;

}
