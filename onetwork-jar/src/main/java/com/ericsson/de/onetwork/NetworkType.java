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

package com.ericsson.de.onetwork;

/**
 * Defines the four different network types.
 *
 * @author ecasjim
 */
public enum NetworkType {
    CORE, GRAN, WRAN, LTE;

    /**
     * Verifies that network type string can be used to create enum.
     *
     * @param networkTypeString
     *            network type
     * @return boolean representing (in)valid network type
     */
    public static boolean isMember(final String networkTypeString) {
        final NetworkType[] networkTypes = NetworkType.values();
        for (final NetworkType networkType : networkTypes) {
            if (networkType.name().equals(networkTypeString)) {
                return true;
            }
        }
        return false;
    }
}
