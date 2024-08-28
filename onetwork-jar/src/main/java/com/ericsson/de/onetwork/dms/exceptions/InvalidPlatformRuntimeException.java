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

package com.ericsson.de.onetwork.dms.exceptions;

/**
 * {@code InvalidPlatformRuntimeException} extends {@code RuntimeException}.
 * Thrown when a valid platform cannot be found. This is not recoverable from
 * and indicates a mistake in the internal logic.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class InvalidPlatformRuntimeException extends RuntimeException {

    /**
     * Default serial version Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@code InvalidPlatformRuntimeException} with no detail
     * message.
     */
    public InvalidPlatformRuntimeException() {
        super();
    }

    /**
     * Constructs a new {@code InvalidPlatformRuntimeException} with the
     * specified detail message.
     *
     * @param message
     *            Description of the specific exception
     */
    public InvalidPlatformRuntimeException(final String message) {
        super(message);
    }

}
