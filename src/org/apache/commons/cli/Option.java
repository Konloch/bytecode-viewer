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
import java.util.List;

/**
 * Describes a single command-line option.  It maintains
 * information regarding the short-name of the option, the long-name,
 * if any exists, a flag indicating if an argument is required for
 * this option, and a self-documenting description of the option.
 * <p>
 * An Option is not created independently, but is created through
 * an instance of {@link Options}. An Option is required to have
 * at least a short or a long-name.
 * <p>
 * <b>Note:</b> once an {@link Option} has been added to an instance
 * of {@link Options}, it's required flag may not be changed anymore.
 *
 * @see org.apache.commons.cli.Options
 * @see org.apache.commons.cli.CommandLine
 *
 * @version $Id: Option.java 1677406 2015-05-03 14:27:31Z britter $
 */
public class Option implements Cloneable, Serializable
{
    /** constant that specifies the number of argument values has not been specified */
    public static final int UNINITIALIZED = -1;

    /** constant that specifies the number of argument values is infinite */
    public static final int UNLIMITED_VALUES = -2;

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** the name of the option */
    private final String opt;

    /** the long representation of the option */
    private String longOpt;

    /** the name of the argument for this option */
    private String argName;

    /** description of the option */
    private String description;

    /** specifies whether this option is required to be present */
    private boolean required;

    /** specifies whether the argument value of this Option is optional */
    private boolean optionalArg;

    /** the number of argument values this option can have */
    private int numberOfArgs = UNINITIALIZED;

    /** the type of this Option */
    private Class<?> type = String.class;

    /** the list of argument values **/
    private List<String> values = new ArrayList<String>();

    /** the character that is the value separator */
    private char valuesep;

    /**
     * Private constructor used by the nested Builder class.
     * 
     * @param builder builder used to create this option
     */
    private Option(final Builder builder)
    {
        this.argName = builder.argName;
        this.description = builder.description;
        this.longOpt = builder.longOpt;
        this.numberOfArgs = builder.numberOfArgs;
        this.opt = builder.opt;
        this.optionalArg = builder.optionalArg;
        this.required = builder.required;
        this.type = builder.type;
        this.valuesep = builder.valuesep;
    }
    
    /**
     * Creates an Option using the specified parameters.
     * The option does not take an argument.
     *
     * @param opt short representation of the option
     * @param description describes the function of the option
     *
     * @throws IllegalArgumentException if there are any non valid
     * Option characters in <code>opt</code>.
     */
    public Option(String opt, String description) throws IllegalArgumentException
    {
        this(opt, null, false, description);
    }

    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt short representation of the option
     * @param hasArg specifies whether the Option takes an argument or not
     * @param description describes the function of the option
     *
     * @throws IllegalArgumentException if there are any non valid
     * Option characters in <code>opt</code>.
     */
    public Option(String opt, boolean hasArg, String description) throws IllegalArgumentException
    {
        this(opt, null, hasArg, description);
    }

    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt short representation of the option
     * @param longOpt the long representation of the option
     * @param hasArg specifies whether the Option takes an argument or not
     * @param description describes the function of the option
     *
     * @throws IllegalArgumentException if there are any non valid
     * Option characters in <code>opt</code>.
     */
    public Option(String opt, String longOpt, boolean hasArg, String description)
           throws IllegalArgumentException
    {
        // ensure that the option is valid
        OptionValidator.validateOption(opt);

        this.opt = opt;
        this.longOpt = longOpt;

        // if hasArg is set then the number of arguments is 1
        if (hasArg)
        {
            this.numberOfArgs = 1;
        }

        this.description = description;
    }

    /**
     * Returns the id of this Option.  This is only set when the
     * Option shortOpt is a single character.  This is used for switch
     * statements.
     *
     * @return the id of this Option
     */
    public int getId()
    {
        return getKey().charAt(0);
    }

    /**
     * Returns the 'unique' Option identifier.
     * 
     * @return the 'unique' Option identifier
     */
    String getKey()
    {
        // if 'opt' is null, then it is a 'long' option
        return (opt == null) ? longOpt : opt;
    }

    /** 
     * Retrieve the name of this Option.
     *
     * It is this String which can be used with
     * {@link CommandLine#hasOption(String opt)} and
     * {@link CommandLine#getOptionValue(String opt)} to check
     * for existence and argument.
     *
     * @return The name of this option
     */
    public String getOpt()
    {
        return opt;
    }

    /**
     * Retrieve the type of this Option.
     * 
     * @return The type of this option
     */
    public Object getType()
    {
        return type;
    }

    /**
     * Sets the type of this Option.
     * <p>
     * <b>Note:</b> this method is kept for binary compatibility and the
     * input type is supposed to be a {@link Class} object. 
     *
     * @param type the type of this Option
     * @deprecated since 1.3, use {@link #setType(Class)} instead
     */
    @Deprecated
    public void setType(Object type)
    {
        setType((Class<?>) type);
    }

    /**
     * Sets the type of this Option.
     *
     * @param type the type of this Option
     * @since 1.3
     */
    public void setType(Class<?> type)
    {
        this.type = type;
    }

    /** 
     * Retrieve the long name of this Option.
     *
     * @return Long name of this option, or null, if there is no long name
     */
    public String getLongOpt()
    {
        return longOpt;
    }

    /**
     * Sets the long name of this Option.
     *
     * @param longOpt the long name of this Option
     */
    public void setLongOpt(String longOpt)
    {
        this.longOpt = longOpt;
    }

    /**
     * Sets whether this Option can have an optional argument.
     *
     * @param optionalArg specifies whether the Option can have
     * an optional argument.
     */
    public void setOptionalArg(boolean optionalArg)
    {
        this.optionalArg = optionalArg;
    }

    /**
     * @return whether this Option can have an optional argument
     */
    public boolean hasOptionalArg()
    {
        return optionalArg;
    }

    /** 
     * Query to see if this Option has a long name
     *
     * @return boolean flag indicating existence of a long name
     */
    public boolean hasLongOpt()
    {
        return longOpt != null;
    }

    /** 
     * Query to see if this Option requires an argument
     *
     * @return boolean flag indicating if an argument is required
     */
    public boolean hasArg()
    {
        return numberOfArgs > 0 || numberOfArgs == UNLIMITED_VALUES;
    }

    /** 
     * Retrieve the self-documenting description of this Option
     *
     * @return The string description of this option
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the self-documenting description of this Option
     *
     * @param description The description of this option
     * @since 1.1
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /** 
     * Query to see if this Option is mandatory
     *
     * @return boolean flag indicating whether this Option is mandatory
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * Sets whether this Option is mandatory.
     *
     * @param required specifies whether this Option is mandatory
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    /**
     * Sets the display name for the argument value.
     *
     * @param argName the display name for the argument value.
     */
    public void setArgName(String argName)
    {
        this.argName = argName;
    }

    /**
     * Gets the display name for the argument value.
     *
     * @return the display name for the argument value.
     */
    public String getArgName()
    {
        return argName;
    }

    /**
     * Returns whether the display name for the argument value has been set.
     *
     * @return if the display name for the argument value has been set.
     */
    public boolean hasArgName()
    {
        return argName != null && argName.length() > 0;
    }

    /** 
     * Query to see if this Option can take many values.
     *
     * @return boolean flag indicating if multiple values are allowed
     */
    public boolean hasArgs()
    {
        return numberOfArgs > 1 || numberOfArgs == UNLIMITED_VALUES;
    }

    /** 
     * Sets the number of argument values this Option can take.
     *
     * @param num the number of argument values
     */
    public void setArgs(int num)
    {
        this.numberOfArgs = num;
    }

    /**
     * Sets the value separator.  For example if the argument value
     * was a Java property, the value separator would be '='.
     *
     * @param sep The value separator.
     */
    public void setValueSeparator(char sep)
    {
        this.valuesep = sep;
    }

    /**
     * Returns the value separator character.
     *
     * @return the value separator character.
     */
    public char getValueSeparator()
    {
        return valuesep;
    }

    /**
     * Return whether this Option has specified a value separator.
     * 
     * @return whether this Option has specified a value separator.
     * @since 1.1
     */
    public boolean hasValueSeparator()
    {
        return valuesep > 0;
    }

    /** 
     * Returns the number of argument values this Option can take.
     *
     * @return num the number of argument values
     */
    public int getArgs()
    {
        return numberOfArgs;
    }

    /**
     * Adds the specified value to this Option.
     * 
     * @param value is a/the value of this Option
     */
    void addValueForProcessing(String value)
    {
        if (numberOfArgs == UNINITIALIZED)
        {
            throw new RuntimeException("NO_ARGS_ALLOWED");
        }
        processValue(value);
    }

    /**
     * Processes the value.  If this Option has a value separator
     * the value will have to be parsed into individual tokens.  When
     * n-1 tokens have been processed and there are more value separators
     * in the value, parsing is ceased and the remaining characters are
     * added as a single token.
     *
     * @param value The String to be processed.
     *
     * @since 1.0.1
     */
    private void processValue(String value)
    {
        // this Option has a separator character
        if (hasValueSeparator())
        {
            // get the separator character
            char sep = getValueSeparator();

            // store the index for the value separator
            int index = value.indexOf(sep);

            // while there are more value separators
            while (index != -1)
            {
                // next value to be added 
                if (values.size() == numberOfArgs - 1)
                {
                    break;
                }

                // store
                add(value.substring(0, index));

                // parse
                value = value.substring(index + 1);

                // get new index
                index = value.indexOf(sep);
            }
        }

        // store the actual value or the last value that has been parsed
        add(value);
    }

    /**
     * Add the value to this Option.  If the number of arguments
     * is greater than zero and there is enough space in the list then
     * add the value.  Otherwise, throw a runtime exception.
     *
     * @param value The value to be added to this Option
     *
     * @since 1.0.1
     */
    private void add(String value)
    {
        if (!acceptsArg())
        {
            throw new RuntimeException("Cannot add value, list full.");
        }

        // store value
        values.add(value);
    }

    /**
     * Returns the specified value of this Option or 
     * <code>null</code> if there is no value.
     *
     * @return the value/first value of this Option or 
     * <code>null</code> if there is no value.
     */
    public String getValue()
    {
        return hasNoValues() ? null : values.get(0);
    }

    /**
     * Returns the specified value of this Option or 
     * <code>null</code> if there is no value.
     *
     * @param index The index of the value to be returned.
     *
     * @return the specified value of this Option or 
     * <code>null</code> if there is no value.
     *
     * @throws IndexOutOfBoundsException if index is less than 1
     * or greater than the number of the values for this Option.
     */
    public String getValue(int index) throws IndexOutOfBoundsException
    {
        return hasNoValues() ? null : values.get(index);
    }

    /**
     * Returns the value/first value of this Option or the 
     * <code>defaultValue</code> if there is no value.
     *
     * @param defaultValue The value to be returned if there
     * is no value.
     *
     * @return the value/first value of this Option or the 
     * <code>defaultValue</code> if there are no values.
     */
    public String getValue(String defaultValue)
    {
        String value = getValue();

        return (value != null) ? value : defaultValue;
    }

    /**
     * Return the values of this Option as a String array 
     * or null if there are no values
     *
     * @return the values of this Option as a String array 
     * or null if there are no values
     */
    public String[] getValues()
    {
        return hasNoValues() ? null : values.toArray(new String[values.size()]);
    }

    /**
     * @return the values of this Option as a List
     * or null if there are no values
     */
    public List<String> getValuesList()
    {
        return values;
    }

    /** 
     * Dump state, suitable for debugging.
     *
     * @return Stringified form of this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder().append("[ option: ");

        buf.append(opt);

        if (longOpt != null)
        {
            buf.append(" ").append(longOpt);
        }

        buf.append(" ");

        if (hasArgs())
        {
            buf.append("[ARG...]");
        }
        else if (hasArg())
        {
            buf.append(" [ARG]");
        }

        buf.append(" :: ").append(description);

        if (type != null)
        {
            buf.append(" :: ").append(type);
        }

        buf.append(" ]");

        return buf.toString();
    }

    /**
     * Returns whether this Option has any values.
     *
     * @return whether this Option has any values.
     */
    private boolean hasNoValues()
    {
        return values.isEmpty();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Option option = (Option) o;


        if (opt != null ? !opt.equals(option.opt) : option.opt != null)
        {
            return false;
        }
        if (longOpt != null ? !longOpt.equals(option.longOpt) : option.longOpt != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        result = opt != null ? opt.hashCode() : 0;
        result = 31 * result + (longOpt != null ? longOpt.hashCode() : 0);
        return result;
    }

    /**
     * A rather odd clone method - due to incorrect code in 1.0 it is public 
     * and in 1.1 rather than throwing a CloneNotSupportedException it throws 
     * a RuntimeException so as to maintain backwards compat at the API level. 
     *
     * After calling this method, it is very likely you will want to call 
     * clearValues(). 
     *
     * @return a clone of this Option instance
     * @throws RuntimeException if a {@link CloneNotSupportedException} has been thrown
     * by {@code super.clone()}
     */
    @Override
    public Object clone()
    {
        try
        {
            Option option = (Option) super.clone();
            option.values = new ArrayList<String>(values);
            return option;
        }
        catch (CloneNotSupportedException cnse)
        {
            throw new RuntimeException("A CloneNotSupportedException was thrown: " + cnse.getMessage());
        }
    }

    /**
     * Clear the Option values. After a parse is complete, these are left with
     * data in them and they need clearing if another parse is done.
     *
     * See: <a href="https://issues.apache.org/jira/browse/CLI-71">CLI-71</a>
     */
    void clearValues()
    {
        values.clear();
    }

    /**
     * This method is not intended to be used. It was a piece of internal 
     * API that was made public in 1.0. It currently throws an UnsupportedOperationException.
     *
     * @param value the value to add
     * @return always throws an {@link UnsupportedOperationException}
     * @throws UnsupportedOperationException always
     * @deprecated
     */
    @Deprecated
    public boolean addValue(String value)
    {
        throw new UnsupportedOperationException("The addValue method is not intended for client use. "
                + "Subclasses should use the addValueForProcessing method instead. ");
    }

    /**
     * Tells if the option can accept more arguments.
     * 
     * @return false if the maximum number of arguments is reached
     * @since 1.3
     */
    boolean acceptsArg()
    {
        return (hasArg() || hasArgs() || hasOptionalArg()) && (numberOfArgs <= 0 || values.size() < numberOfArgs);
    }

    /**
     * Tells if the option requires more arguments to be valid.
     * 
     * @return false if the option doesn't require more arguments
     * @since 1.3
     */
    boolean requiresArg()
    {
        if (optionalArg)
        {
            return false;
        }
        if (numberOfArgs == UNLIMITED_VALUES)
        {
            return values.isEmpty();
        }
        return acceptsArg();
    }
    
    /**
     * Returns a {@link Builder} to create an {@link Option} using descriptive
     * methods.  
     * 
     * @return a new {@link Builder} instance
     * @since 1.3
     */
    public static Builder builder()
    {
        return builder(null);
    }
    
    /**
     * Returns a {@link Builder} to create an {@link Option} using descriptive
     * methods.  
     *
     * @param opt short representation of the option
     * @return a new {@link Builder} instance
     * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}
     * @since 1.3
     */
    public static Builder builder(final String opt)
    {
        return new Builder(opt);
    }
    
    /**
     * A nested builder class to create <code>Option</code> instances
     * using descriptive methods.
     * <p>
     * Example usage:
     * <pre>
     * Option option = Option.builder("a")
     *     .required(true)
     *     .longOpt("arg-name")
     *     .build();
     * </pre>
     * 
     * @since 1.3
     */
    public static final class Builder 
    {
        /** the name of the option */
        private final String opt;

        /** description of the option */
        private String description;

        /** the long representation of the option */
        private String longOpt;

        /** the name of the argument for this option */
        private String argName;

        /** specifies whether this option is required to be present */
        private boolean required;

        /** specifies whether the argument value of this Option is optional */
        private boolean optionalArg;

        /** the number of argument values this option can have */
        private int numberOfArgs = UNINITIALIZED;

        /** the type of this Option */
        private Class<?> type = String.class;

        /** the character that is the value separator */
        private char valuesep;

        /**
         * Constructs a new <code>Builder</code> with the minimum
         * required parameters for an <code>Option</code> instance.
         * 
         * @param opt short representation of the option
         * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}
         */
        private Builder(final String opt) throws IllegalArgumentException
        {
            OptionValidator.validateOption(opt);
            this.opt = opt;
        }
        
        /**
         * Sets the display name for the argument value.
         *
         * @param argName the display name for the argument value.
         * @return this builder, to allow method chaining
         */
        public Builder argName(final String argName)
        {
            this.argName = argName;
            return this;
        }

        /**
         * Sets the description for this option.
         *
         * @param description the description of the option.
         * @return this builder, to allow method chaining
         */
        public Builder desc(final String description)
        {
            this.description = description;
            return this;
        }

        /**
         * Sets the long name of the Option.
         *
         * @param longOpt the long name of the Option
         * @return this builder, to allow method chaining
         */        
        public Builder longOpt(final String longOpt)
        {
            this.longOpt = longOpt;
            return this;
        }
        
        /** 
         * Sets the number of argument values the Option can take.
         *
         * @param numberOfArgs the number of argument values
         * @return this builder, to allow method chaining
         */        
        public Builder numberOfArgs(final int numberOfArgs)
        {
            this.numberOfArgs = numberOfArgs;
            return this;
        }
        
        /**
         * Sets whether the Option can have an optional argument.
         *
         * @param isOptional specifies whether the Option can have
         * an optional argument.
         * @return this builder, to allow method chaining
         */
        public Builder optionalArg(final boolean isOptional)
        {
            this.optionalArg = isOptional;
            return this;
        }
        
        /**
         * Marks this Option as required.
         *
         * @return this builder, to allow method chaining
         */
        public Builder required()
        {
            return required(true);
        }

        /**
         * Sets whether the Option is mandatory.
         *
         * @param required specifies whether the Option is mandatory
         * @return this builder, to allow method chaining
         */
        public Builder required(final boolean required)
        {
            this.required = required;
            return this;
        }
        
        /**
         * Sets the type of the Option.
         *
         * @param type the type of the Option
         * @return this builder, to allow method chaining
         */
        public Builder type(final Class<?> type)
        {
            this.type = type;
            return this;
        }

        /**
         * The Option will use '=' as a means to separate argument value.
         *
         * @return this builder, to allow method chaining
         */
        public Builder valueSeparator()
        {
            return valueSeparator('=');
        }

        /**
         * The Option will use <code>sep</code> as a means to
         * separate argument values.
         * <p>
         * <b>Example:</b>
         * <pre>
         * Option opt = Option.builder("D").hasArgs()
         *                                 .valueSeparator('=')
         *                                 .build();
         * Options options = new Options();
         * options.addOption(opt);
         * String[] args = {"-Dkey=value"};
         * CommandLineParser parser = new DefaultParser();
         * CommandLine line = parser.parse(options, args);
         * String propertyName = line.getOptionValues("D")[0];  // will be "key"
         * String propertyValue = line.getOptionValues("D")[1]; // will be "value"
         * </pre>
         *
         * @param sep The value separator.
         * @return this builder, to allow method chaining
         */
        public Builder valueSeparator(final char sep)
        {
            valuesep = sep;
            return this;
        }
        
        /**
         * Indicates that the Option will require an argument.
         * 
         * @return this builder, to allow method chaining
         */
        public Builder hasArg()
        {
            return hasArg(true);
        }

        /**
         * Indicates if the Option has an argument or not.
         * 
         * @param hasArg specifies whether the Option takes an argument or not
         * @return this builder, to allow method chaining
         */
        public Builder hasArg(final boolean hasArg)
        {
            // set to UNINITIALIZED when no arg is specified to be compatible with OptionBuilder
            numberOfArgs = hasArg ? 1 : Option.UNINITIALIZED;
            return this;
        }

        /**
         * Indicates that the Option can have unlimited argument values.
         * 
         * @return this builder, to allow method chaining
         */
        public Builder hasArgs()
        {
            numberOfArgs = Option.UNLIMITED_VALUES;
            return this;
        }

        /**
         * Constructs an Option with the values declared by this {@link Builder}.
         * 
         * @return the new {@link Option}
         * @throws IllegalArgumentException if neither {@code opt} or {@code longOpt} has been set
         */
        public Option build()
        {
            if (opt == null && longOpt == null)
            {
                throw new IllegalArgumentException("Either opt or longOpt must be specified");
            }
            return new Option(this);
        }
    }
}
