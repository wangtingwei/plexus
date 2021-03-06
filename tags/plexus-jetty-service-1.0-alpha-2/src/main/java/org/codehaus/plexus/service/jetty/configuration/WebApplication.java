package org.codehaus.plexus.service.jetty.configuration;

import java.util.ArrayList;
import java.util.List;

/*
 * The MIT License
 *
 * Copyright (c) 2004-2005, The Codehaus
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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class WebApplication
{
    private String file;

    private String path;

    private String extractionPath;

    private String context;

    private List listeners;

    private String virtualHost;

    public WebApplication( String file,
                           String path,
                           String extractionPath,
                           String context,
                           String virtualHost )
    {
        this.file = file;

        this.path = path;

        this.extractionPath = extractionPath;

        this.context = context;

        this.virtualHost = virtualHost;

        this.listeners = new ArrayList();
    }

    public String getFile()
    {
        return file;
    }

    public String getPath()
    {
        return path;
    }

    public String getExtractionPath()
    {
        return extractionPath;
    }

    public String getContext()
    {
        return context;
    }

    public String getVirtualHost()
    {
        return virtualHost;
    }

    public List getListeners()
    {
        return listeners;
    }
}
