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
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Default parser.
 * 
 * @version $Id: DefaultParser.java 1677454 2015-05-03 17:13:54Z ggregory $
 * @since 1.3
 */
public class DefaultParser implements CommandLineParser
{
    /** The command-line instance. */
    protected CommandLine cmd;
    
    /** The current options. */
    protected Options options;

    /**
     * Flag indicating how unrecognized tokens are handled. <tt>true</tt> to stop
     * the parsing and add the remaining tokens to the args list.
     * <tt>false</tt> to throw an exception. 
     */
    protected boolean stopAtNonOption;

    /** The token currently processed. */
    protected String currentToken;
 
    /** The last option parsed. */
    protected Option currentOption;
 
    /** Flag indicating if tokens should no longer be analyzed and simply added as arguments of the command line. */
    protected boolean skipParsing;
 
    /** The required options and groups expected to be found when parsing the command line. */
    protected List expectedOpts;
 
    public CommandLine parse(Options options, String[] arguments) throws ParseException
    {
        return parse(options, arguments, null);
    }

    /**
     * Parse the arguments according to the specified options and properties.
     *
     * @param options    the specified Options
     * @param arguments  the command line arguments
     * @param properties command line option name-value pairs
     * @return the list of atomic option and value tokens
     *
     * @throws ParseException if there are any problems encountered
     * while parsing the command line tokens.
     */
    public CommandLine parse(Options options, String[] arguments, Properties properties) throws ParseException
    {
        return parse(options, arguments, properties, false);
    }

    public CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException
    {
        return parse(options, arguments, null, stopAtNonOption);
    }

    /**
     * Parse the arguments according to the specified options and properties.
     *
     * @param options         the specified Options
     * @param arguments       the command line arguments
     * @param properties      command line option name-value pairs
     * @param stopAtNonOption if <tt>true</tt> an unrecognized argument stops
     *     the parsing and the remaining arguments are added to the 
     *     {@link CommandLine}s args list. If <tt>false</tt> an unrecognized
     *     argument triggers a ParseException.
     *
     * @return the list of atomic option and value tokens
     * @throws ParseException if there are any problems encountered
     * while parsing the command line tokens.
     */
    public CommandLine parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption)
            throws ParseException
    {
        this.options = options;
        this.stopAtNonOption = stopAtNonOption;
        skipParsing = false;
        currentOption = null;
        expectedOpts = new ArrayList(options.getRequiredOptions());

        // clear the data from the groups
        for (OptionGroup group : options.getOptionGroups())
        {
            group.setSelected(null);
        }

        cmd = new CommandLine();

        if (arguments != null)
        {
            for (String argument : arguments)
            {
                handleToken(argument);
            }
        }

        // check the arguments of the last option
        checkRequiredArgs();

        // add the default options
        handleProperties(properties);

        checkRequiredOptions();

        return cmd;
    }

    /**
     * Sets the values of Options using the values in <code>properties</code>.
     *
     * @param properties The value properties to be processed.
     */
    private void handleProperties(Properties properties) throws ParseException
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
                // get the value from the properties
                String value = properties.getProperty(option);

                if (opt.hasArg())
                {
                    if (opt.getValues() == null || opt.getValues().length == 0)
                    {
                        opt.addValueForProcessing(value);
                    }
                }
                else if (!("yes".equalsIgnoreCase(value)
                        || "true".equalsIgnoreCase(value)
                        || "1".equalsIgnoreCase(value)))
                {
                    // if the value is not yes, true or 1 then don't add the option to the CommandLine
                    continue;
                }

                handleOption(opt);
                currentOption = null;
            }
        }
    }

    /**
     * Throws a {@link MissingOptionException} if all of the required options
     * are not present.
     *
     * @throws MissingOptionException if any of the required Options
     * are not present.
     */
    private void checkRequiredOptions() throws MissingOptionException
    {
        // if there are required options that have not been processed
        if (!expectedOpts.isEmpty())
        {
            throw new MissingOptionException(expectedOpts);
        }
    }

    /**
     * Throw a {@link MissingArgumentException} if the current option
     * didn't receive the number of arguments expected.
     */
    private void checkRequiredArgs() throws ParseException
    {
        if (currentOption != null && currentOption.requiresArg())
        {
            throw new MissingArgumentException(currentOption);
        }
    }

    /**
     * Handle any command line token.
     *
     * @param token the command line token to handle
     * @throws ParseException
     */
    private void handleToken(String token) throws ParseException
    {
        currentToken = token;

        if (skipParsing)
        {
            cmd.addArg(token);
        }
        else if ("--".equals(token))
        {
            skipParsing = true;
        }
        else if (currentOption != null && currentOption.acceptsArg() && isArgument(token))
        {
            currentOption.addValueForProcessing(Util.stripLeadingAndTrailingQuotes(token));
        }
        else if (token.startsWith("--"))
        {
            handleLongOption(token);
        }
        else if (token.startsWith("-") && !"-".equals(token))
        {
            handleShortAndLongOption(token);
        }
        else
        {
            handleUnknownToken(token);
        }

        if (currentOption != null && !currentOption.acceptsArg())
        {
            currentOption = null;
        }
    }

    /**
     * Returns true is the token is a valid argument.
     *
     * @param token
     */
    private boolean isArgument(String token)
    {
        return !isOption(token) || isNegativeNumber(token);
    }

    /**
     * Check if the token is a negative number.
     *
     * @param token
     */
    private boolean isNegativeNumber(String token)
    {
        try
        {
            Double.parseDouble(token);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * Tells if the token looks like an option.
     *
     * @param token
     */
    private boolean isOption(String token)
    {
        return isLongOption(token) || isShortOption(token);
    }

    /**
     * Tells if the token looks like a short option.
     * 
     * @param token
     */
    private boolean isShortOption(String token)
    {
        // short options (-S, -SV, -S=V, -SV1=V2, -S1S2)
        return token.startsWith("-") && token.length() >= 2 && options.hasShortOption(token.substring(1, 2));
    }

    /**
     * Tells if the token looks like a long option.
     *
     * @param token
     */
    private boolean isLongOption(String token)
    {
        if (!token.startsWith("-") || token.length() == 1)
        {
            return false;
        }

        int pos = token.indexOf("=");
        String t = pos == -1 ? token : token.substring(0, pos);

        if (!options.getMatchingOptions(t).isEmpty())
        {
            // long or partial long options (--L, -L, --L=V, -L=V, --l, --l=V)
            return true;
        }
        else if (getLongPrefix(token) != null && !token.startsWith("--"))
        {
            // -LV
            return true;
        }

        return false;
    }

    /**
     * Handles an unknown token. If the token starts with a dash an 
     * UnrecognizedOptionException is thrown. Otherwise the token is added 
     * to the arguments of the command line. If the stopAtNonOption flag 
     * is set, this stops the parsing and the remaining tokens are added 
     * as-is in the arguments of the command line.
     *
     * @param token the command line token to handle
     */
    private void handleUnknownToken(String token) throws ParseException
    {
        if (token.startsWith("-") && token.length() > 1 && !stopAtNonOption)
        {
            throw new UnrecognizedOptionException("Unrecognized option: " + token, token);
        }

        cmd.addArg(token);
        if (stopAtNonOption)
        {
            skipParsing = true;
        }
    }

    /**
     * Handles the following tokens:
     *
     * --L
     * --L=V
     * --L V
     * --l
     *
     * @param token the command line token to handle
     */
    private void handleLongOption(String token) throws ParseException
    {
        if (token.indexOf('=') == -1)
        {
            handleLongOptionWithoutEqual(token);
        }
        else
        {
            handleLongOptionWithEqual(token);
        }
    }

    /**
     * Handles the following tokens:
     *
     * --L
     * -L
     * --l
     * -l
     * 
     * @param token the command line token to handle
     */
    private void handleLongOptionWithoutEqual(String token) throws ParseException
    {
        List<String> matchingOpts = options.getMatchingOptions(token);
        if (matchingOpts.isEmpty())
        {
            handleUnknownToken(currentToken);
        }
        else if (matchingOpts.size() > 1)
        {
            throw new AmbiguousOptionException(token, matchingOpts);
        }
        else
        {
            handleOption(options.getOption(matchingOpts.get(0)));
        }
    }

    /**
     * Handles the following tokens:
     *
     * --L=V
     * -L=V
     * --l=V
     * -l=V
     *
     * @param token the command line token to handle
     */
    private void handleLongOptionWithEqual(String token) throws ParseException
    {
        int pos = token.indexOf('=');

        String value = token.substring(pos + 1);

        String opt = token.substring(0, pos);

        List<String> matchingOpts = options.getMatchingOptions(opt);
        if (matchingOpts.isEmpty())
        {
            handleUnknownToken(currentToken);
        }
        else if (matchingOpts.size() > 1)
        {
            throw new AmbiguousOptionException(opt, matchingOpts);
        }
        else
        {
            Option option = options.getOption(matchingOpts.get(0));

            if (option.acceptsArg())
            {
                handleOption(option);
                currentOption.addValueForProcessing(value);
                currentOption = null;
            }
            else
            {
                handleUnknownToken(currentToken);
            }
        }
    }

    /**
     * Handles the following tokens:
     *
     * -S
     * -SV
     * -S V
     * -S=V
     * -S1S2
     * -S1S2 V
     * -SV1=V2
     *
     * -L
     * -LV
     * -L V
     * -L=V
     * -l
     *
     * @param token the command line token to handle
     */
    private void handleShortAndLongOption(String token) throws ParseException
    {
        String t = Util.stripLeadingHyphens(token);

        int pos = t.indexOf('=');

        if (t.length() == 1)
        {
            // -S
            if (options.hasShortOption(t))
            {
                handleOption(options.getOption(t));
            }
            else
            {
                handleUnknownToken(token);
            }
        }
        else if (pos == -1)
        {
            // no equal sign found (-xxx)
            if (options.hasShortOption(t))
            {
                handleOption(options.getOption(t));
            }
            else if (!options.getMatchingOptions(t).isEmpty())
            {
                // -L or -l
                handleLongOptionWithoutEqual(token);
            }
            else
            {
                // look for a long prefix (-Xmx512m)
                String opt = getLongPrefix(t);

                if (opt != null && options.getOption(opt).acceptsArg())
                {
                    handleOption(options.getOption(opt));
                    currentOption.addValueForProcessing(t.substring(opt.length()));
                    currentOption = null;
                }
                else if (isJavaProperty(t))
                {
                    // -SV1 (-Dflag)
                    handleOption(options.getOption(t.substring(0, 1)));
                    currentOption.addValueForProcessing(t.substring(1));
                    currentOption = null;
                }
                else
                {
                    // -S1S2S3 or -S1S2V
                    handleConcatenatedOptions(token);
                }
            }
        }
        else
        {
            // equal sign found (-xxx=yyy)
            String opt = t.substring(0, pos);
            String value = t.substring(pos + 1);

            if (opt.length() == 1)
            {
                // -S=V
                Option option = options.getOption(opt);
                if (option != null && option.acceptsArg())
                {
                    handleOption(option);
                    currentOption.addValueForProcessing(value);
                    currentOption = null;
                }
                else
                {
                    handleUnknownToken(token);
                }
            }
            else if (isJavaProperty(opt))
            {
                // -SV1=V2 (-Dkey=value)
                handleOption(options.getOption(opt.substring(0, 1)));
                currentOption.addValueForProcessing(opt.substring(1));
                currentOption.addValueForProcessing(value);
                currentOption = null;
            }
            else
            {
                // -L=V or -l=V
                handleLongOptionWithEqual(token);
            }
        }
    }

    /**
     * Search for a prefix that is the long name of an option (-Xmx512m)
     *
     * @param token
     */
    private String getLongPrefix(String token)
    {
        String t = Util.stripLeadingHyphens(token);

        int i;
        String opt = null;
        for (i = t.length() - 2; i > 1; i--)
        {
            String prefix = t.substring(0, i);
            if (options.hasLongOption(prefix))
            {
                opt = prefix;
                break;
            }
        }
        
        return opt;
    }

    /**
     * Check if the specified token is a Java-like property (-Dkey=value).
     */
    private boolean isJavaProperty(String token)
    {
        String opt = token.substring(0, 1);
        Option option = options.getOption(opt);

        return option != null && (option.getArgs() >= 2 || option.getArgs() == Option.UNLIMITED_VALUES);
    }

    private void handleOption(Option option) throws ParseException
    {
        // check the previous option before handling the next one
        checkRequiredArgs();

        option = (Option) option.clone();

        updateRequiredOptions(option);

        cmd.addOption(option);

        if (option.hasArg())
        {
            currentOption = option;
        }
        else
        {
            currentOption = null;
        }
    }

    /**
     * Removes the option or its group from the list of expected elements.
     *
     * @param option
     */
    private void updateRequiredOptions(Option option) throws AlreadySelectedException
    {
        if (option.isRequired())
        {
            expectedOpts.remove(option.getKey());
        }

        // if the option is in an OptionGroup make that option the selected option of the group
        if (options.getOptionGroup(option) != null)
        {
            OptionGroup group = options.getOptionGroup(option);

            if (group.isRequired())
            {
                expectedOpts.remove(group);
            }

            group.setSelected(option);
        }
    }

    /**
     * Breaks <code>token</code> into its constituent parts
     * using the following algorithm.
     *
     * <ul>
     *  <li>ignore the first character ("<b>-</b>")</li>
     *  <li>foreach remaining character check if an {@link Option}
     *  exists with that id.</li>
     *  <li>if an {@link Option} does exist then add that character
     *  prepended with "<b>-</b>" to the list of processed tokens.</li>
     *  <li>if the {@link Option} can have an argument value and there
     *  are remaining characters in the token then add the remaining
     *  characters as a token to the list of processed tokens.</li>
     *  <li>if an {@link Option} does <b>NOT</b> exist <b>AND</b>
     *  <code>stopAtNonOption</code> <b>IS</b> set then add the special token
     *  "<b>--</b>" followed by the remaining characters and also
     *  the remaining tokens directly to the processed tokens list.</li>
     *  <li>if an {@link Option} does <b>NOT</b> exist <b>AND</b>
     *  <code>stopAtNonOption</code> <b>IS NOT</b> set then add that
     *  character prepended with "<b>-</b>".</li>
     * </ul>
     *
     * @param token The current token to be <b>burst</b>
     * at the first non-Option encountered.
     * @throws ParseException if there are any problems encountered
     *                        while parsing the command line token.
     */
    protected void handleConcatenatedOptions(String token) throws ParseException
    {
        for (int i = 1; i < token.length(); i++)
        {
            String ch = String.valueOf(token.charAt(i));

            if (options.hasOption(ch))
            {
                handleOption(options.getOption(ch));

                if (currentOption != null && token.length() != i + 1)
                {
                    // add the trail as an argument of the option
                    currentOption.addValueForProcessing(token.substring(i + 1));
                    break;
                }
            }
            else
            {
                handleUnknownToken(stopAtNonOption && i > 1 ? token.substring(i) : token);
                break;
            }
        }
    }
}
