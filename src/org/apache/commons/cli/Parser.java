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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

/**
 * <code>Parser</code> creates {@link CommandLine}s.
 *
 * @version $Id: Parser.java 1677406 2015-05-03 14:27:31Z britter $
 * @deprecated since 1.3, the two-pass parsing with the flatten method is not enough flexible to handle complex cases
 */
@Deprecated
public abstract class Parser implements CommandLineParser
{
    /** commandline instance */
    protected CommandLine cmd;

    /** current Options */
    private Options options;

    /** list of required options strings */
    private List requiredOptions;

    protected void setOptions(Options options)
    {
        this.options = options;
        this.requiredOptions = new ArrayList(options.getRequiredOptions());
    }

    protected Options getOptions()
    {
        return options;
    }

    protected List getRequiredOptions()
    {
        return requiredOptions;
    }

    /**
     * Subclasses must implement this method to reduce
     * the <code>arguments</code> that have been passed to the parse method.
     *
     * @param opts The Options to parse the arguments by.
     * @param arguments The arguments that have to be flattened.
     * @param stopAtNonOption specifies whether to stop
     * flattening when a non option has been encountered
     * @return a String array of the flattened arguments
     * @throws ParseException if there are any problems encountered
     *                        while parsing the command line tokens.
     */
    protected abstract String[] flatten(Options opts, String[] arguments, boolean stopAtNonOption)
            throws ParseException;

    /**
     * Parses the specified <code>arguments</code> based
     * on the specified {@link Options}.
     *
     * @param options the <code>Options</code>
     * @param arguments the <code>arguments</code>
     * @return the <code>CommandLine</code>
     * @throws ParseException if there are any problems encountered
     *                        while parsing the command line tokens.
     */
    public CommandLine parse(Options options, String[] arguments) throws ParseException
    {
        return parse(options, arguments, null, false);
    }

    /**
     * Parse the arguments according to the specified options and properties.
     *
     * @param options    the specified Options
     * @param arguments  the command line arguments
     * @param properties command line option name-value pairs
     * @return the list of atomic option and value tokens
     * @throws ParseException if there are any problems encountered
     *                        while parsing the command line tokens.
     *
     * @since 1.1
     */
    public CommandLine parse(Options options, String[] arguments, Properties properties) throws ParseException
    {
        return parse(options, arguments, properties, false);
    }

    /**
     * Parses the specified <code>arguments</code>
     * based on the specified {@link Options}.
     *
     * @param options         the <code>Options</code>
     * @param arguments       the <code>arguments</code>
     * @param stopAtNonOption if <tt>true</tt> an unrecognized argument stops
     *     the parsing and the remaining arguments are added to the 
     *     {@link CommandLine}s args list. If <tt>false</tt> an unrecognized
     *     argument triggers a ParseException.
     * @return the <code>CommandLine</code>
     * @throws ParseException if an error occurs when parsing the arguments.
     */
    public CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException
    {
        return parse(options, arguments, null, stopAtNonOption);
    }

    /**
     * Parse the arguments according to the specified options and
     * properties.
     *
     * @param options the specified Options
     * @param arguments the command line arguments
     * @param properties command line option name-value pairs
     * @param stopAtNonOption if <tt>true</tt> an unrecognized argument stops
     *     the parsing and the remaining arguments are added to the 
     *     {@link CommandLine}s args list. If <tt>false</tt> an unrecognized
     *     argument triggers a ParseException.
     *
     * @return the list of atomic option and value tokens
     *
     * @throws ParseException if there are any problems encountered
     * while parsing the command line tokens.
     *
     * @since 1.1
     */
    public CommandLine parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption)
            throws ParseException
    {
        // clear out the data in options in case it's been used before (CLI-71)
        for (Option opt : options.helpOptions())
        {
            opt.clearValues();
        }
        
        // clear the data from the groups
        for (OptionGroup group : options.getOptionGroups())
        {
            group.setSelected(null);
        }        

        // initialise members
        setOptions(options);

        cmd = new CommandLine();

        boolean eatTheRest = false;

        if (arguments == null)
        {
            arguments = new String[0];
        }

        List<String> tokenList = Arrays.asList(flatten(getOptions(), arguments, stopAtNonOption));

        ListIterator<String> iterator = tokenList.listIterator();

        // process each flattened token
        while (iterator.hasNext())
        {
            String t = iterator.next();

            // the value is the double-dash
            if ("--".equals(t))
            {
                eatTheRest = true;
            }

            // the value is a single dash
            else if ("-".equals(t))
            {
                if (stopAtNonOption)
                {
                    eatTheRest = true;
                }
                else
                {
                    cmd.addArg(t);
                }
            }

            // the value is an option
            else if (t.startsWith("-"))
            {
                if (stopAtNonOption && !getOptions().hasOption(t))
                {
                    eatTheRest = true;
                    cmd.addArg(t);
                }
                else
                {
                    processOption(t, iterator);
                }
            }

            // the value is an argument
            else
            {
                cmd.addArg(t);

                if (stopAtNonOption)
                {
                    eatTheRest = true;
                }
            }

            // eat the remaining tokens
            if (eatTheRest)
            {
                while (iterator.hasNext())
                {
                    String str = iterator.next();

                    // ensure only one double-dash is added
                    if (!"--".equals(str))
                    {
                        cmd.addArg(str);
                    }
                }
            }
        }

        processProperties(properties);
        checkRequiredOptions();

        return cmd;
    }

    /**
     * Sets the values of Options using the values in <code>properties</code>.
     *
     * @param properties The value properties to be processed.
     * @throws ParseException if there are any problems encountered
     *                        while processing the properties.
     */
    protected void processProperties(Properties properties) throws ParseException
    {
        if (properties == null)
        {
            return;
        }

        for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();)
        {
            String option = e.nextElement().toString();
            
            Option opt = options.getOption(option);
            if (opt == null)
            {
                throw new UnrecognizedOptionException("Default option wasn't defined", option);
            }
            
            // if the option is part of a group, check if another option of the group has been selected
            OptionGroup group = options.getOptionGroup(opt);
            boolean selected = group != null && group.getSelected() != null;
            
            if (!cmd.hasOption(option) && !selected)
            {
                // get the value from the properties instance
                String value = properties.getProperty(option);

                if (opt.hasArg())
                {
                    if (opt.getValues() == null || opt.getValues().length == 0)
                    {
                        try
                        {
                            opt.addValueForProcessing(value);
                        }
                        catch (RuntimeException exp) //NOPMD
                        {
                            // if we cannot add the value don't worry about it
                        }
                    }
                }
                else if (!("yes".equalsIgnoreCase(value)
                        || "true".equalsIgnoreCase(value)
                        || "1".equalsIgnoreCase(value)))
                {
                    // if the value is not yes, true or 1 then don't add the
                    // option to the CommandLine
                    continue;
                }

                cmd.addOption(opt);
                updateRequiredOptions(opt);
            }
        }
    }

    /**
     * Throws a {@link MissingOptionException} if all of the required options
     * are not present.
     *
     * @throws MissingOptionException if any of the required Options are not present.
     */
    protected void checkRequiredOptions() throws MissingOptionException
    {
        // if there are required options that have not been processed
        if (!getRequiredOptions().isEmpty())
        {
            throw new MissingOptionException(getRequiredOptions());
        }
    }

    /**
     * Process the argument values for the specified Option
     * <code>opt</code> using the values retrieved from the
     * specified iterator <code>iter</code>.
     *
     * @param opt The current Option
     * @param iter The iterator over the flattened command line Options.
     *
     * @throws ParseException if an argument value is required
     * and it is has not been found.
     */
    public void processArgs(Option opt, ListIterator<String> iter) throws ParseException
    {
        // loop until an option is found
        while (iter.hasNext())
        {
            String str = iter.next();
            
            // found an Option, not an argument
            if (getOptions().hasOption(str) && str.startsWith("-"))
            {
                iter.previous();
                break;
            }

            // found a value
            try
            {
                opt.addValueForProcessing(Util.stripLeadingAndTrailingQuotes(str));
            }
            catch (RuntimeException exp)
            {
                iter.previous();
                break;
            }
        }

        if (opt.getValues() == null && !opt.hasOptionalArg())
        {
            throw new MissingArgumentException(opt);
        }
    }

    /**
     * Process the Option specified by <code>arg</code> using the values
     * retrieved from the specified iterator <code>iter</code>.
     *
     * @param arg The String value representing an Option
     * @param iter The iterator over the flattened command line arguments.
     *
     * @throws ParseException if <code>arg</code> does not represent an Option
     */
    protected void processOption(String arg, ListIterator<String> iter) throws ParseException
    {
        boolean hasOption = getOptions().hasOption(arg);

        // if there is no option throw an UnrecognisedOptionException
        if (!hasOption)
        {
            throw new UnrecognizedOptionException("Unrecognized option: " + arg, arg);
        }

        // get the option represented by arg
        Option opt = (Option) getOptions().getOption(arg).clone();
        
        // update the required options and groups
        updateRequiredOptions(opt);
        
        // if the option takes an argument value
        if (opt.hasArg())
        {
            processArgs(opt, iter);
        }
        
        // set the option on the command line
        cmd.addOption(opt);
    }

    /**
     * Removes the option or its group from the list of expected elements.
     * 
     * @param opt
     */
    private void updateRequiredOptions(Option opt) throws ParseException
    {
        // if the option is a required option remove the option from
        // the requiredOptions list
        if (opt.isRequired())
        {
            getRequiredOptions().remove(opt.getKey());
        }

        // if the option is in an OptionGroup make that option the selected
        // option of the group
        if (getOptions().getOptionGroup(opt) != null)
        {
            OptionGroup group = getOptions().getOptionGroup(opt);

            if (group.isRequired())
            {
                getRequiredOptions().remove(group);
            }

            group.setSelected(opt);
        }
    }
}
