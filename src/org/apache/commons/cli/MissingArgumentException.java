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
 * Thrown when an option requiring an argument
 * is not provided with an argument.
 *
 * @version $Id: MissingArgumentException.java 1443102 2013-02-06 18:12:16Z tn $
 */
public class MissingArgumentException extends ParseException
{
    /**
     * This exception {@code serialVersionUID}.
     */
    private static final long serialVersionUID = -7098538588704965017L;

    /** The option requiring additional arguments */
    private Option option;

    /**
     * Construct a new <code>MissingArgumentException</code>
     * with the specified detail message.
     *
     * @param message the detail message
     */
    public MissingArgumentException(String message)
    {
        super(message);
    }

    /**
     * Construct a new <code>MissingArgumentException</code>
     * with the specified detail message.
     *
     * @param option the option requiring an argument
     * @since 1.2
     */
    public MissingArgumentException(Option option)
    {
        this("Missing argument for option: " + option.getKey());
        this.option = option;
    }

    /**
     * Return the option requiring an argument that wasn't provided
     * on the command line.
     *
     * @return the related option
     * @since 1.2
     */
    public Option getOption()
    {
        return option;
    }
}
