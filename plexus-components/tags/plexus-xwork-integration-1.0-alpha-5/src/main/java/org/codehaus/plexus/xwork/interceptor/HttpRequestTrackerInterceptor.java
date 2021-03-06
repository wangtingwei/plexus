package org.codehaus.plexus.xwork.interceptor;

/*
 * Copyright 2006-2007 The Codehaus Foundation.
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

import com.opensymphony.xwork.ActionInvocation;

/**
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor"
 * role-hint="httpRequestTrackerInterceptor"
 */
public class HttpRequestTrackerInterceptor
    extends AbstractHttpRequestTrackerInterceptor
{
    /**
     * @plexus.configuration default-value="simple"
     */
    private String trackerName;

    public void destroy()
    {
    }

    public void init()
    {
        getLogger().info( this.getClass().getName() + " initialized!" );
    }

    protected String getTrackerName()
    {
        return trackerName;
    }

    /**
     * @noinspection ProhibitedExceptionDeclared
     */
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        addActionInvocation( invocation );

        return invocation.invoke();
    }

    public void setTrackerName( String trackerName )
    {
        this.trackerName = trackerName;
    }
}
