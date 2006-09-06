package org.codehaus.plexus.security.authorization.rbac.web.action;

import org.codehaus.plexus.xwork.action.PlexusActionSupport;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStore;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStoreException;
import org.codehaus.plexus.security.authorization.rbac.Role;
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

/**
 * PermissionActions:
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="plexusSecurityPermission"

 */
public class PermissionActions
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private RbacStore store;

    private int roleId;

    private Role role;

    public String display()
        throws RbacStoreException
    {
        role = store.getRole( roleId );

        return SUCCESS;
    }

}
