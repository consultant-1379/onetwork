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

package com.ericsson.de.onetwork.dms.util;

import static com.ericsson.de.onetwork.dms.constants.RelationshipConstants.RELATIONSHIP_COM_ECIM_SEPARATOR;

import com.ericsson.de.onetwork.dms.generics.Mo;

/**
 * Formats data related to {@link Mo} types and attributes.
 *
 * @author edalrey
 * @since 1NETWORK_15.14
 */
public class MoFormatter {

    /**
     * Removes the namespace that precedes the type of an {@link Mo}; they are
     * separated by a colon. Has no affect if moType is for CPP.
     *
     * @param moType
     *            the type of managed object
     * @return returns the moType with its corresponding namespce removed
     */
    public static String removeComEcimNamespaceFrom(final String moType) {
        final String[] childComponents = moType.split(RELATIONSHIP_COM_ECIM_SEPARATOR);
        final int lastElementIndex = childComponents.length - 1;
        return childComponents[lastElementIndex];
    }

}
