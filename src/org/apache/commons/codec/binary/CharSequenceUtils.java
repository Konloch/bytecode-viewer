/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec.binary;

/**
 * <p>
 * Operations on {@link CharSequence} that are <code>null</code> safe.
 * </p>
 * <p>
 * Copied from Apache Commons Lang r1586295 on April 10, 2014 (day of 3.3.2 release).
 * </p>
 *
 * @see CharSequence
 * @since 1.10
 */
public class CharSequenceUtils {

    /**
     * Green implementation of regionMatches.
     *
     * @param cs
     *            the <code>CharSequence</code> to be processed
     * @param ignoreCase
     *            whether or not to be case insensitive
     * @param thisStart
     *            the index to start on the <code>cs</code> CharSequence
     * @param substring
     *            the <code>CharSequence</code> to be looked for
     * @param start
     *            the index to start on the <code>substring</code> CharSequence
     * @param length
     *            character length of the region
     * @return whether the region matched
     */
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
            final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        while (tmpLen-- > 0) {
            char c1 = cs.charAt(index1++);
            char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The same check as in String.regionMatches():
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2) &&
                    Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }
}
