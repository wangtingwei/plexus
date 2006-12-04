package org.codehaus.plexus.maven.plugin;

/*
 * Copyright (c) 2004-2005, Codehaus.org
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

import java.util.List;

import com.sun.org.apache.xpath.internal.operations.And;

/**
 * @goal test-descriptor
 *
 * @phase process-sources
 *
 * @description Processes the specificed list of Java unit test source directories {@link And} builds a Plexus descriptor.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @since 1.3.4
 */
public class PlexusTestDescriptorMojo
    extends AbstractDescriptorMojo
{
    // ----------------------------------------------------------------------
    // Parameters
    // ----------------------------------------------------------------------

    /**
     * List of Java unit test source directories to process.
     * 
     * @parameter expression="${project.testCompileSourceRoots}"
     * @required
     */
    private List sourceDirectories;

    /**
     * {@inheritDoc}
     * 
     * @see org.codehaus.plexus.maven.plugin.AbstractDescriptorMojo#getSourceDirectories()
     */
    protected List getSourceDirectories()
    {
        return this.sourceDirectories;
    }

}
