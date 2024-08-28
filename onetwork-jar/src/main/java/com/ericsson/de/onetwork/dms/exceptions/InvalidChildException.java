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
 * {@code InvalidChildException} extends {@link DataModellerServiceException}.
 * Thrown when a directed parent-child relationship between two {@link Mo} s
 * does not exist, i.e., the parent {@code Mo} does not list the child
 * {@code Mo} as one if its valid children.
 * <p>
 * Thrown when an {@code Mo} of a given type is added to a parent {@code Mo} and
 * this parent does not have a child type that match new {@code Mo}'s
 * type.
 *
 * @author edalrey
 * @since 1Network_15.12
 */
public class InvalidChildException extends DataModellerServiceException {

    /**
     * Default serial version Id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@link InvalidChildException} with no detail message.
     */
    public InvalidChildException() {
        super();
    }

    /**
     * Constructs a new {@link InvalidChildException} with the specified detail
     * message.
     *
     * @param message
     *            Description of the specific exception
     */
    public InvalidChildException(final String message) {
        super(message);
    }

}
