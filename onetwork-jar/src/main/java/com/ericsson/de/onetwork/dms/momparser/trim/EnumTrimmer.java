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

import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Enum;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.EnumMember;

/**
 * Trims unused elements in an {@code Enum} object.
 *
 * @author eaefhiq
 */
public class EnumTrimmer implements Trimmer {

    /** The MIM enum to be trimmed. */
    private final Enum mimEnum;

    /**
     * Instantiates a new enum trimmer.
     *
     * @param mimEnum
     *            the mimEnum to be trimmed
     */
    public EnumTrimmer(final Enum mimEnum) {
        this.mimEnum = mimEnum;
    }

    /**
     * Removes any existing values for these properties. For example, removes
     * any dependencies already set and content within <description> tags.
     */
    @Override
    public void trim() {
        mimEnum.setDescription(null);
        final List<EnumMember> list = mimEnum.getEnumMember();
        for (final EnumMember em : list) {
            em.setDependencies(null);
            em.setDependenciesScript(null);
            em.setDescription(null);
            em.setDisturbances(null);
        }

    }

}
