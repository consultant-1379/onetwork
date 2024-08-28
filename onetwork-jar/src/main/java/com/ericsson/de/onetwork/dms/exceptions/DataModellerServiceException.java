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
 * {@code DataModellerServiceException} extends {@link Exception}. This is the
 * base for custom, checked {@code Exception}s in the
 * com.ericsson.de.onetwork.dms package.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class DataModellerServiceException extends Exception {

    /**
     * Default serial version Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@link DataModellerServiceException} with no detail
     * message.
     */
    public DataModellerServiceException() {
        super();
    }

    /**
     * Constructs a new {@link DataModellerServiceException} with the specified
     * detail message.
     *
     * @param message
     *            Description of the specific exception
     */
    public DataModellerServiceException(final String message) {
        super(message);
    }

}
