/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.ss.util;

import java.util.Arrays;

/**
 * A specialized StringBuilder wrapper class capable of adding "\n" at the end
 * of each line and specified amount of empty spaces in front of the each line.
 *
 * @author qfatonu
 */
public class SimServiceStringBuilder {

    /** The new line constant */
    public static final String NEW_LINE = "\n";

    /** Holds the MML commands. */
    private final StringBuilder sb;

    /**
     * Constructs a new <code>SimServiceStringBuilder</code> object.
     */
    public SimServiceStringBuilder() {
        sb = new StringBuilder();
    }

    /**
     * Appends the specified string to this character sequence plus "\n" at the
     * end the line.
     *
     * @param data
     *            a string
     */
    public void append(final String data) {
        sb.append(data + NEW_LINE);
    }

    /**
     * Appends the specified string after specified prefix space this character
     * sequence plus "\n" at the end the line.
     *
     * @param spaceLength
     *            the length of the prefix space
     * @param data
     *            a string
     */
    public void append(final int spaceLength, final String data) {
        // faster approach than String.format
        final char[] charArray = new char[spaceLength];
        Arrays.fill(charArray, ' ');
        final String space = new String(charArray);

        sb.append(space + data + NEW_LINE);
    }

    /**
     * Appends the specified string to this character sequence.
     *
     * @param data
     *            a string
     */
    public void appendWithoutNewLine(final String data) {
        sb.append(data);
    }

    /**
     * Appends the specified string after specified prefix space this character
     * sequence.
     *
     * @param spaceLength
     *            the length of the prefix space
     * @param data
     *            a string
     */
    public void appendWithoutNewLine(final int spaceLength, final String data) {
        // faster approach than String.format
        final char[] charArray = new char[spaceLength];
        Arrays.fill(charArray, ' ');
        final String space = new String(charArray);

        sb.append(space + data);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
