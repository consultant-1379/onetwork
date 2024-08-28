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

import com.ericsson.de.onetwork.dms.generics.Mo;

/**
 * {@code ChildNotFoundException} extends {@link DataModellerServiceException}.
 * Thrown when a parent {@link Mo} does not contain a child {@code Mo} of
 * a specific type.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class ChildNotFoundException extends DataModellerServiceException {

    /**
     * Default serial version Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@link ChildNotFoundException} with no detail message.
     */
    public ChildNotFoundException() {
        super();
    }

    /**
     * Constructs a new {@link ChildNotFoundException} with the specified detail
     * message.
     *
     * @param message
     *            Description of the specific exception
     */
    public ChildNotFoundException(final String message) {
        super(message);
    }

}
