/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.gnm;

/**
 * Thrown when a requested <code>Gnm</code> cannot be returned.
 *
 * @author ecasjim
 */
public class GnmRequestException extends Exception {

    private static final long serialVersionUID = -903976722704359590L;

    /**
     * Constructs a GnmRequestException with no detail message.
     */
    public GnmRequestException() {
        super();
    }

    /**
     * Used to send message with custom exception.
     *
     * @param message
     *            the message associated with the exception.
     */
    public GnmRequestException(final String message) {
        super(message);
    }

    /**
     * Used to send message and Throwable with custom exception.
     *
     * @param message
     *            the message associated with the exception.
     * @param cause
     *            the throwable associated with the exception.
     */
    public GnmRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Used to send Throwable with custom exception.
     *
     * @param cause
     *            the throwable associated with the exception.
     */
    public GnmRequestException(final Throwable cause) {
        super(cause);
    }

}
