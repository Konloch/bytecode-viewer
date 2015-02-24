
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino.util;

import java.util.Stack;

/**
 * Implements a scheme for benchmarking, i.e. for determining and/or reporting the time elapsed
 * between the beginning and the end of an activity.
 * <p>
 * The measurement is done by invoking {@link #begin()} and later calling {@link #end()} whichs
 * returns the time elapsed since the call to {@link #begin()}.
 * <p>
 * Notice that calls to {@link #begin()} and {@link #end()} can be nested, and each call to
 * {@link #end()} refers to the matching {@link #begin()} call. To ensure that all calls match,
 * the preferred way to write a benchmark is
 * <pre>
 * ...
 * Benchmark b = new Benchmark();
 * ...
 * b.begin();
 * try {
 *     ....
 * } finally {
 *     long ms = b.end();
 * }
 * </pre>
 * This code layout also makes it visually easy to write correct pairs of {@link #begin()} /
 * {@link #end()} pairs.
 * <p>
 * The pair {@link #beginReporting()} and {@link #endReporting()} do basically the same, but
 * report the benchmarking information through an internal {@link Reporter} object. The default
 * {@link Reporter} prints its messages by <code>System.out.println()</code>.
 * <p>
 * Reporting is only enabled if the Benchmark object was created through {@link #Benchmark(boolean)}
 * with a <code>true</code> argument.
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class Benchmark {
    private final Stack beginTimes = new Stack(); // Long

    public
    Benchmark() {
        this.reportingEnabled = false;
        this.reporter         = null;
    }

    /** @see Benchmark */
    public void
    begin() { this.beginTimes.push(new Long(System.currentTimeMillis())); }

    /** @see Benchmark */
    public long
    end() { return System.currentTimeMillis() - ((Long) this.beginTimes.pop()).longValue(); }

    // Reporting-related methods and fields.

    /** Sets up a {@link Benchmark} with a default {@link Reporter} that reports to {@code System.out}. */
    public
    Benchmark(boolean reportingEnabled) {
        this.reportingEnabled = reportingEnabled;
        this.reporter         = new Reporter() {
            @Override public void report(String message) { System.out.println(message); }
        };
    }

    /** Set up a {@link Benchmark} with a custom {@link Reporter}. */
    public
    Benchmark(boolean reportingEnabled, Reporter reporter) {
        this.reportingEnabled = reportingEnabled;
        this.reporter         = reporter;
    }

    private final boolean  reportingEnabled;
    private final Reporter reporter;

    /** Interface used to report messages. */
    public
    interface Reporter {

        /** Reports the given {@code message}. */
        void report(String message);
    }

    /** Begin a benchmark (see {@link #begin()}) and report the fact. */
    public void
    beginReporting() {
        if (!this.reportingEnabled) return;

        this.reportIndented("Beginning...");
        this.begin();
    }

    /** Begin a benchmark (see {@link #begin()}) and report the fact. */
    public void
    beginReporting(String message) {
        if (!this.reportingEnabled) return;
        this.reportIndented(message + "...");
        this.begin();
    }

    /** End a benchmark (see {@link #end()}) and report the fact. */
    public void
    endReporting() {
        if (!this.reportingEnabled) return;
        this.reportIndented("... took " + this.end() + " ms");
    }

    /** End a benchmark (see {@link #begin()}) and report the fact. */
    public void
    endReporting(String message) {
        if (!this.reportingEnabled) return;
        this.reportIndented("... took " + this.end() + " ms: " + message);
    }

    /** Report the given message. */
    public void
    report(String message) {
        if (!this.reportingEnabled) return;
        this.reportIndented(message);
    }

    /**
     * Report the <code>title</code>, a colon, a space, and the pretty-printed
     * {@link Object}.
     * @param optionalTitle
     * @param o
     */
    public void
    report(String optionalTitle, Object o) {
        if (!this.reportingEnabled) return;

        String prefix = optionalTitle == null ? "" : (
            optionalTitle
            + ": "
            + (optionalTitle.length() < Benchmark.PAD.length() ? Benchmark.PAD.substring(optionalTitle.length()) : "")
        );

        if (o == null) {
            this.reportIndented(prefix + "(undefined)");
        } else
        if (o.getClass().isArray()) {
            Object[] oa = (Object[]) o;
            if (oa.length == 0) {
                this.reportIndented(prefix + "(empty)");
            } else
            if (oa.length == 1) {
                this.reportIndented(prefix + oa[0].toString());
            } else {
                this.reportIndented(optionalTitle == null ? "Array:" : optionalTitle + ':');
                this.begin();
                try {
                    for (Object o2 : oa) this.report(null, o2);
                } finally {
                    this.end();
                }
            }
        } else
        {
            this.reportIndented(prefix + o.toString());
        }
    }
    private static final String PAD = "                       ";

    /** Report a message through {@link #reporter}, indent by N spaces where N is the current benchmark stack depth. */
    private void
    reportIndented(String message) {
        StringBuilder sb = new StringBuilder();
        for (int i = this.beginTimes.size(); i > 0; --i) sb.append("  ");
        sb.append(message);
        this.reporter.report(sb.toString());
    }
}
