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

package com.ericsson.de.onetwork.dms.momparser.trim;

import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataType;

/**
 * Trims unused elements in a {@code DerivedDataType} object.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class DerivedDataTypeTrimmer implements Trimmer {

    /** The MIM derivedDataType to be trimmed. */
    private final DerivedDataType mimDerivedDataType;

    /**
     * Instantiates a new derivedDataType trimmer.
     *
     * @param mimDerivedDataType
     *            the MIM derivedDataType to be trimmed
     */
    public DerivedDataTypeTrimmer(final DerivedDataType mimDerivedDataType) {
        this.mimDerivedDataType = mimDerivedDataType;
    }

    /**
     * Removes any existing values for these properties. For example, removes
     * any dependencies already set and content within <description> tags.
     */
    @Override
    public void trim() {
        mimDerivedDataType.setDescription(null);
    }

}
