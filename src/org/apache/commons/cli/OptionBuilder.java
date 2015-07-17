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
 * OptionBuilder allows the user to create Options using descriptive methods.
 * <p>
 * Details on the Builder pattern can be found at
 * <a href="http://c2.com/cgi-bin/wiki?BuilderPattern">http://c2.com/cgi-bin/wiki?BuilderPattern</a>.
 * <p>
 * This class is NOT thread safe. See <a href="https://issues.apache.org/jira/browse/CLI-209">CLI-209</a>
 * 
 * @version $Id: OptionBuilder.java 1677400 2015-05-03 13:46:08Z britter $
 * @since 1.0
 * @deprecated since 1.3, use {@link Option#builder(String)} instead
 */
@Deprecated
public final class OptionBuilder
{
    /** long option */
    private static String longopt;

    /** option description */
    private static String description;

    /** argument name */
    private static String argName;

    /** is required? */
    private static boolean required;

    /** the number of arguments */
    private static int numberOfArgs = Option.UNINITIALIZED;

    /** option type */
    private static Class<?> type;

    /** option can have an optional argument value */
    private static boolean optionalArg;

    /** value separator for argument value */
    private static char valuesep;

    /** option builder instance */
    private static final OptionBuilder INSTANCE = new OptionBuilder();

    static
    {
        // ensure the consistency of the initial values
        reset();
    }

    /**
     * private constructor to prevent instances being created
     */
    private OptionBuilder()
    {
        // hide the constructor
    }

    /**
     * Resets the member variables to their default values.
     */
    private static void reset()
    {
        description = null;
        argName = null;
        longopt = null;
        type = String.class;
        required = false;
        numberOfArgs = Option.UNINITIALIZED;
        optionalArg = false;
        valuesep = (char) 0;
    }

    /**
     * The next Option created will have the following long option value.
     *
     * @param newLongopt the long option value
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withLongOpt(String newLongopt)
    {
        OptionBuilder.longopt = newLongopt;

        return INSTANCE;
    }

    /**
     * The next Option created will require an argument value.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasArg()
    {
        OptionBuilder.numberOfArgs = 1;

        return INSTANCE;
    }

    /**
     * The next Option created will require an argument value if
     * <code>hasArg</code> is true.
     *
     * @param hasArg if true then the Option has an argument value
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasArg(boolean hasArg)
    {
        OptionBuilder.numberOfArgs = hasArg ? 1 : Option.UNINITIALIZED;

        return INSTANCE;
    }

    /**
     * The next Option created will have the specified argument value name.
     *
     * @param name the name for the argument value
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withArgName(String name)
    {
        OptionBuilder.argName = name;

        return INSTANCE;
    }

    /**
     * The next Option created will be required.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder isRequired()
    {
        OptionBuilder.required = true;

        return INSTANCE;
    }

    /**
     * The next Option created uses <code>sep</code> as a means to
     * separate argument values.
     * <p>
     * <b>Example:</b>
     * <pre>
     * Option opt = OptionBuilder.withValueSeparator('=')
     *                           .create('D');
     *
     * String args = "-Dkey=value";
     * CommandLine line = parser.parse(args);
     * String propertyName = opt.getValue(0);  // will be "key"
     * String propertyValue = opt.getValue(1); // will be "value"
     * </pre>
     *
     * @param sep The value separator to be used for the argument values.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withValueSeparator(char sep)
    {
        OptionBuilder.valuesep = sep;

        return INSTANCE;
    }

    /**
     * The next Option created uses '<code>=</code>' as a means to
     * separate argument values.
     *
     * <b>Example:</b>
     * <pre>
     * Option opt = OptionBuilder.withValueSeparator()
     *                           .create('D');
     *
     * CommandLine line = parser.parse(args);
     * String propertyName = opt.getValue(0);
     * String propertyValue = opt.getValue(1);
     * </pre>
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withValueSeparator()
    {
        OptionBuilder.valuesep = '=';

        return INSTANCE;
    }

    /**
     * The next Option created will be required if <code>required</code>
     * is true.
     *
     * @param newRequired if true then the Option is required
     * @return the OptionBuilder instance
     */
    public static OptionBuilder isRequired(boolean newRequired)
    {
        OptionBuilder.required = newRequired;

        return INSTANCE;
    }

    /**
     * The next Option created can have unlimited argument values.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasArgs()
    {
        OptionBuilder.numberOfArgs = Option.UNLIMITED_VALUES;

        return INSTANCE;
    }

    /**
     * The next Option created can have <code>num</code> argument values.
     *
     * @param num the number of args that the option can have
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasArgs(int num)
    {
        OptionBuilder.numberOfArgs = num;

        return INSTANCE;
    }

    /**
     * The next Option can have an optional argument.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasOptionalArg()
    {
        OptionBuilder.numberOfArgs = 1;
        OptionBuilder.optionalArg = true;

        return INSTANCE;
    }

    /**
     * The next Option can have an unlimited number of optional arguments.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasOptionalArgs()
    {
        OptionBuilder.numberOfArgs = Option.UNLIMITED_VALUES;
        OptionBuilder.optionalArg = true;

        return INSTANCE;
    }

    /**
     * The next Option can have the specified number of optional arguments.
     *
     * @param numArgs - the maximum number of optional arguments
     * the next Option created can have.
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasOptionalArgs(int numArgs)
    {
        OptionBuilder.numberOfArgs = numArgs;
        OptionBuilder.optionalArg = true;

        return INSTANCE;
    }

    /**
     * The next Option created will have a value that will be an instance
     * of <code>type</code>.
     * <p>
     * <b>Note:</b> this method is kept for binary compatibility and the
     * input type is supposed to be a {@link Class} object. 
     *
     * @param newType the type of the Options argument value
     * @return the OptionBuilder instance
     * @deprecated since 1.3, use {@link #withType(Class)} instead
     */
    @Deprecated
    public static OptionBuilder withType(Object newType)
    {
        return withType((Class<?>) newType);
    }

    /**
     * The next Option created will have a value that will be an instance
     * of <code>type</code>.
     *
     * @param newType the type of the Options argument value
     * @return the OptionBuilder instance
     * @since 1.3
     */
    public static OptionBuilder withType(Class<?> newType)
    {
        OptionBuilder.type = newType;

        return INSTANCE;
    }

    /**
     * The next Option created will have the specified description
     *
     * @param newDescription a description of the Option's purpose
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withDescription(String newDescription)
    {
        OptionBuilder.description = newDescription;

        return INSTANCE;
    }

    /**
     * Create an Option using the current settings and with
     * the specified Option <code>char</code>.
     *
     * @param opt the character representation of the Option
     * @return the Option instance
     * @throws IllegalArgumentException if <code>opt</code> is not
     * a valid character.  See Option.
     */
    public static Option create(char opt) throws IllegalArgumentException
    {
        return create(String.valueOf(opt));
    }

    /**
     * Create an Option using the current settings
     *
     * @return the Option instance
     * @throws IllegalArgumentException if <code>longOpt</code> has not been set.
     */
    public static Option create() throws IllegalArgumentException
    {
        if (longopt == null)
        {
            OptionBuilder.reset();
            throw new IllegalArgumentException("must specify longopt");
        }

        return create(null);
    }

    /**
     * Create an Option using the current settings and with
     * the specified Option <code>char</code>.
     *
     * @param opt the <code>java.lang.String</code> representation
     * of the Option
     * @return the Option instance
     * @throws IllegalArgumentException if <code>opt</code> is not
     * a valid character.  See Option.
     */
    public static Option create(String opt) throws IllegalArgumentException
    {
        Option option = null;
        try
        {
            // create the option
            option = new Option(opt, description);

            // set the option properties
            option.setLongOpt(longopt);
            option.setRequired(required);
            option.setOptionalArg(optionalArg);
            option.setArgs(numberOfArgs);
            option.setType(type);
            option.setValueSeparator(valuesep);
            option.setArgName(argName);
        }
        finally
        {
            // reset the OptionBuilder properties
            OptionBuilder.reset();
        }

        // return the Option instance
        return option;
    }
}
