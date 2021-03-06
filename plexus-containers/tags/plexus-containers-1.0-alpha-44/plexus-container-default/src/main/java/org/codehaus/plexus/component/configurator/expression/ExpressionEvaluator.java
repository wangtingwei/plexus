package org.codehaus.plexus.component.configurator.expression;

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

import java.io.File;

/**
 * Evaluate an expression.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public interface ExpressionEvaluator
{
    String ROLE = ExpressionEvaluator.class.getName();
    
    /**
     * Evaluate an expression.
     *
     * @param expression the expression
     * @return the value of the expression
     */
    Object evaluate( String expression )
        throws ExpressionEvaluationException;

    /**
     * Align a given path to the base directory that can be evaluated by this expression evaluator, if known.
     *
     * @param file the file
     * @return the aligned file
     */
    File alignToBaseDirectory( File file );
}
