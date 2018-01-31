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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Represents list of arguments parsed against a {@link Options} descriptor.
 * <p>
 * It allows querying of a boolean {@link #hasOption(String opt)},
 * in addition to retrieving the {@link #getOptionValue(String opt)}
 * for options requiring arguments.
 * <p>
 * Additionally, any left-over or unrecognized arguments,
 * are available for further processing.
 *
 * @version $Id: CommandLine.java 1444365 2013-02-09 14:21:27Z tn $
 */
public class CommandLine implements Serializable
{
    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** the unrecognised options/arguments */
    private final List<String> args = new LinkedList<String>();

    /** the processed options */
    private final List<Option> options = new ArrayList<Option>();

    /**
     * Creates a command line.
     */
    protected CommandLine()
    {
        // nothing to do
    }

    /** 
     * Query to see if an option has been set.
     *
     * @param opt Short name of the option
     * @return true if set, false if not
     */
    public boolean hasOption(String opt)
    {
        return options.contains(resolveOption(opt));
    }

    /** 
     * Query to see if an option has been set.
     *
     * @param opt character name of the option
     * @return true if set, false if not
     */
    public boolean hasOption(char opt)
    {
        return hasOption(String.valueOf(opt));
    }

    /**
     * Return the <code>Object</code> type of this <code>Option</code>.
     *
     * @param opt the name of the option
     * @return the type of this <code>Option</code>
     * @deprecated due to System.err message. Instead use getParsedOptionValue(String)
     */
    @Deprecated
    public Object getOptionObject(String opt)
    {
        try
        {
            return getParsedOptionValue(opt);
        }
        catch (ParseException pe)
        {
            System.err.println("Exception found converting " + opt + " to desired type: " + pe.getMessage());
            return null;
        }
    }

    /**
     * Return a version of this <code>Option</code> converted to a particular type. 
     *
     * @param opt the name of the option
     * @return the value parsed into a particular object
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.2
     */
    public Object getParsedOptionValue(String opt) throws ParseException
    {
        String res = getOptionValue(opt);
        Option option = resolveOption(opt);
        
        if (option == null || res == null)
        {
            return null;
        }
        
        return TypeHandler.createValue(res, option.getType());
    }

    /**
     * Return the <code>Object</code> type of this <code>Option</code>.
     *
     * @param opt the name of the option
     * @return the type of opt
     */
    public Object getOptionObject(char opt)
    {
        return getOptionObject(String.valueOf(opt));
    }

    /** 
     * Retrieve the first argument, if any, of this option.
     *
     * @param opt the name of the option
     * @return Value of the argument if option is set, and has an argument,
     * otherwise null.
     */
    public String getOptionValue(String opt)
    {
        String[] values = getOptionValues(opt);

        return (values == null) ? null : values[0];
    }

    /** 
     * Retrieve the first argument, if any, of this option.
     *
     * @param opt the character name of the option
     * @return Value of the argument if option is set, and has an argument,
     * otherwise null.
     */
    public String getOptionValue(char opt)
    {
        return getOptionValue(String.valueOf(opt));
    }

    /** 
     * Retrieves the array of values, if any, of an option.
     *
     * @param opt string name of the option
     * @return Values of the argument if option is set, and has an argument,
     * otherwise null.
     */
    public String[] getOptionValues(String opt)
    {
        List<String> values = new ArrayList<String>();

        for (Option option : options)
        {
            if (opt.equals(option.getOpt()) || opt.equals(option.getLongOpt()))
            {
                values.addAll(option.getValuesList());
            }
        }

        return values.isEmpty() ? null : values.toArray(new String[values.size()]);
    }

    /**
     * Retrieves the option object given the long or short option as a String
     * 
     * @param opt short or long name of the option
     * @return Canonicalized option
     */
    private Option resolveOption(String opt)
    {
        opt = Util.stripLeadingHyphens(opt);
        for (Option option : options)
        {
            if (opt.equals(option.getOpt()))
            {
                return option;
            }

            if (opt.equals(option.getLongOpt()))
            {
                return option;
            }

        }
        return null;
    }

    /** 
     * Retrieves the array of values, if any, of an option.
     *
     * @param opt character name of the option
     * @return Values of the argument if option is set, and has an argument,
     * otherwise null.
     */
    public String[] getOptionValues(char opt)
    {
        return getOptionValues(String.valueOf(opt));
    }

    /** 
     * Retrieve the first argument, if any, of an option.
     *
     * @param opt name of the option
     * @param defaultValue is the default value to be returned if the option
     * is not specified
     * @return Value of the argument if option is set, and has an argument,
     * otherwise <code>defaultValue</code>.
     */
    public String getOptionValue(String opt, String defaultValue)
    {
        String answer = getOptionValue(opt);

        return (answer != null) ? answer : defaultValue;
    }

    /** 
     * Retrieve the argument, if any, of an option.
     *
     * @param opt character name of the option
     * @param defaultValue is the default value to be returned if the option
     * is not specified
     * @return Value of the argument if option is set, and has an argument,
     * otherwise <code>defaultValue</code>.
     */
    public String getOptionValue(char opt, String defaultValue)
    {
        return getOptionValue(String.valueOf(opt), defaultValue);
    }

    /**
     * Retrieve the map of values associated to the option. This is convenient
     * for options specifying Java properties like <tt>-Dparam1=value1
     * -Dparam2=value2</tt>. The first argument of the option is the key, and
     * the 2nd argument is the value. If the option has only one argument
     * (<tt>-Dfoo</tt>) it is considered as a boolean flag and the value is
     * <tt>"true"</tt>.
     *
     * @param opt name of the option
     * @return The Properties mapped by the option, never <tt>null</tt>
     *         even if the option doesn't exists
     * @since 1.2
     */
    public Properties getOptionProperties(String opt)
    {
        Properties props = new Properties();

        for (Option option : options)
        {
            if (opt.equals(option.getOpt()) || opt.equals(option.getLongOpt()))
            {
                List<String> values = option.getValuesList();
                if (values.size() >= 2)
                {
                    // use the first 2 arguments as the key/value pair
                    props.put(values.get(0), values.get(1));
                }
                else if (values.size() == 1)
                {
                    // no explicit value, handle it as a boolean
                    props.put(values.get(0), "true");
                }
            }
        }

        return props;
    }

    /** 
     * Retrieve any left-over non-recognized options and arguments
     *
     * @return remaining items passed in but not parsed as an array
     */
    public String[] getArgs()
    {
        String[] answer = new String[args.size()];

        args.toArray(answer);

        return answer;
    }

    /** 
     * Retrieve any left-over non-recognized options and arguments
     *
     * @return remaining items passed in but not parsed as a <code>List</code>.
     */
    public List<String> getArgList()
    {
        return args;
    }

    /** 
     * jkeyes
     * - commented out until it is implemented properly
     * <p>Dump state, suitable for debugging.</p>
     *
     * @return Stringified form of this object
     */

    /*
    public String toString() {
        StringBuilder buf = new StringBuilder();
            
        buf.append("[ CommandLine: [ options: ");
        buf.append(options.toString());
        buf.append(" ] [ args: ");
        buf.append(args.toString());
        buf.append(" ] ]");
            
        return buf.toString();
    }
    */

    /**
     * Add left-over unrecognized option/argument.
     *
     * @param arg the unrecognised option/argument.
     */
    protected void addArg(String arg)
    {
        args.add(arg);
    }

    /**
     * Add an option to the command line.  The values of the option are stored.
     *
     * @param opt the processed option
     */
    protected void addOption(Option opt)
    {
        options.add(opt);
    }

    /**
     * Returns an iterator over the Option members of CommandLine.
     *
     * @return an <code>Iterator</code> over the processed {@link Option}
     * members of this {@link CommandLine}
     */
    public Iterator<Option> iterator()
    {
        return options.iterator();
    }

    /**
     * Returns an array of the processed {@link Option}s.
     *
     * @return an array of the processed {@link Option}s.
     */
    public Option[] getOptions()
    {
        Collection<Option> processed = options;

        // reinitialise array
        Option[] optionsArray = new Option[processed.size()];

        // return the array
        return processed.toArray(optionsArray);
    }
}
