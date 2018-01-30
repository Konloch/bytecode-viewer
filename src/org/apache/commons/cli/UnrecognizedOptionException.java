/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli;

/**
 * Exception thrown during parsing signalling an unrecognized
 * option was seen.
 *
 * @version $Id: UnrecognizedOptionException.java 1443102 2013-02-06 18:12:16Z tn $
 */
public class UnrecognizedOptionException extends ParseException
{
    /**
     * This exception {@code serialVersionUID}.
     */
    private static final long serialVersionUID = -252504690284625623L;

    /** The  unrecognized option */
    private String option;

    /**
     * Construct a new <code>UnrecognizedArgumentException</code>
     * with the specified detail message.
     *
     * @param message the detail message
     */
    public UnrecognizedOptionException(String message)
    {
        super(message);
    }

    /**
     * Construct a new <code>UnrecognizedArgumentException</code>
     * with the specified option and detail message.
     *
     * @param message the detail message
     * @param option  the unrecognized option
     * @since 1.2
     */
    public UnrecognizedOptionException(String message, String option)
    {
        this(message);
        this.option = option;
    }

    /**
     * Returns the unrecognized option.
     *
     * @return the related option
     * @since 1.2
     */
    public String getOption()
    {
        return option;
    }
}
