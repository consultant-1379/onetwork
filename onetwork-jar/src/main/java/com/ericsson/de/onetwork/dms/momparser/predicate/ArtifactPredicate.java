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

package com.ericsson.de.onetwork.dms.momparser.predicate;

import org.apache.commons.collections.Predicate;

/**
 * Provides a generic implementation of the Predicate interface. Used to
 * define the type of the artifacts (ENUM, CLASS, RELATIONSHIP and STRUCT) used
 * from the MOM file. MOM elements which do not match predicates will be ignored
 * by the 1Network service.
 *
 * @author eaefhiq
 */
public class ArtifactPredicate<T> implements Predicate {

    /** The expected type for this predicate. */
    private final Class<T> expectedType;

    /**
     * Instantiates a new predicate.
     *
     * @param expectedType
     *            the expected type for this predicate
     */
    public ArtifactPredicate(final Class<T> expectedType) {
        this.expectedType = expectedType;
    }

    /**
     * Gets the expected type. That is, the type the input of the
     * {@link #evaluate(Object)} method
     * must be in order for method to return true.
     *
     * @return the expected type
     */
    public Class<T> getExpectedType() {
        return this.expectedType;
    }

    /**
     * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
     */
    @Override
    public boolean evaluate(final Object objectToCheck) {
        if (this.expectedType.isInstance(objectToCheck)) {
            return true;
        }
        return false;
    }

}
