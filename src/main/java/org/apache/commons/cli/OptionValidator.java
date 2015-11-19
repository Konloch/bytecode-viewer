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
 * Validates an Option string.
 *
 * @version $Id: OptionValidator.java 1544819 2013-11-23 15:34:31Z tn $
 * @since 1.1
 */
final class OptionValidator
{
    /**
     * Validates whether <code>opt</code> is a permissible Option
     * shortOpt.  The rules that specify if the <code>opt</code>
     * is valid are:
     *
     * <ul>
     *  <li>a single character <code>opt</code> that is either
     *  ' '(special case), '?', '@' or a letter</li>
     *  <li>a multi character <code>opt</code> that only contains
     *  letters.</li>
     * </ul>
     * <p>
     * In case {@code opt} is {@code null} no further validation is performed.
     *
     * @param opt The option string to validate, may be null
     * @throws IllegalArgumentException if the Option is not valid.
     */
    static void validateOption(String opt) throws IllegalArgumentException
    {
        // if opt is NULL do not check further
        if (opt == null)
        {
            return;
        }
        
        // handle the single character opt
        if (opt.length() == 1)
        {
            char ch = opt.charAt(0);

            if (!isValidOpt(ch))
            {
                throw new IllegalArgumentException("Illegal option name '" + ch + "'");
            }
        }

        // handle the multi character opt
        else
        {
            for (char ch : opt.toCharArray())
            {
                if (!isValidChar(ch))
                {
                    throw new IllegalArgumentException("The option '" + opt + "' contains an illegal "
                                                       + "character : '" + ch + "'");
                }
            }
        }
    }

    /**
     * Returns whether the specified character is a valid Option.
     *
     * @param c the option to validate
     * @return true if <code>c</code> is a letter, '?' or '@', otherwise false.
     */
    private static boolean isValidOpt(char c)
    {
        return isValidChar(c) || c == '?' || c == '@';
    }

    /**
     * Returns whether the specified character is a valid character.
     *
     * @param c the character to validate
     * @return true if <code>c</code> is a letter.
     */
    private static boolean isValidChar(char c)
    {
        return Character.isJavaIdentifierPart(c);
    }
}
