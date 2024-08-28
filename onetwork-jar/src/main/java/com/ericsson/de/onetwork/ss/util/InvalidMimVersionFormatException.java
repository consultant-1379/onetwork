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

/**
 * Thrown when network element MIM version in wrong format.
 *
 * @author qfatonu
 */
public class InvalidMimVersionFormatException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -6390785816365449861L;

    /**
     * Constructs an InvalidMimVersionFormatException without detail message.
     */
    public InvalidMimVersionFormatException() {
        super();
    }

    /**
     * Constructs an InvalidMimVersionFormatException with a detail message.
     *
     * @param message
     *            the detail message which describes the possible problem
     */
    public InvalidMimVersionFormatException(final String message) {
        super(message);
    }
}
