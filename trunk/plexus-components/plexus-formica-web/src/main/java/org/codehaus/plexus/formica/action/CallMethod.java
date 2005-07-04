package org.codehaus.plexus.formica.action;

import ognl.Ognl;
import ognl.MethodFailedException;

import org.codehaus.plexus.formica.Form;

import java.util.Map;

/**
 * Just use parameters passed in to call a method in the application model directly. There is
 * no need here to populate a target object.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class CallMethod
    extends AbstractEntityAction
{
    protected void uponSuccessfulValidation( Form form, String entityId, Map parameters )
        throws Exception
    {
        try
        {
            Ognl.getValue( validateExpression( form.getAdd().getExpression() ), parameters, getApplicationComponent( form ) );
        }
        catch ( MethodFailedException e )
        {
            Throwable t = e.getReason();

            if ( t instanceof Exception )
            {
                throw (Exception) t;
            }
        }
    }
}
