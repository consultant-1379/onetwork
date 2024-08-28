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

import java.util.List;

import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Struct;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.StructMember;

/**
 * Trims unused elements in a {@code Struct} object.
 *
 * @author eaefhiq
 */
public class StructTrimmer implements Trimmer {

    /** The MIM struct to be trimmed. */
    private final Struct mimStruct;

    /**
     * Instantiates a new struct trimmer.
     *
     * @param mimStruct
     *            the MIM struct to be trimmed
     */
    public StructTrimmer(final Struct mimStruct) {
        this.mimStruct = mimStruct;
    }

    /**
     * Removes any existing values for these properties. For example, removes
     * any dependencies already set and content within <description> tags.
     */
    @Override
    public void trim() {
        mimStruct.setDescription(null);
        final List<StructMember> structMembers = mimStruct.getStructMember();
        for (final StructMember structMember : structMembers) {
            structMember.setDescription(null);
            structMember.setDecisionPoint(null);
            structMember.setDependencies(null);
            structMember.setDependenciesScript(null);
        }
    }

}
