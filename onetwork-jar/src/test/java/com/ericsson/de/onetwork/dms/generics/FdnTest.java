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

package com.ericsson.de.onetwork.dms.generics;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * Unit tests for {@link Fdn}
 *
 * @author edalrey
 * @since 1Network_15.11
 */
public class FdnTest {

    private final Fdn rootLevel = new Fdn(null, "ManagedElement", "LTE01ERBS001");
    private final Fdn firstLevel = new Fdn(rootLevel, "ENodeBFunction", "1");
    private final Fdn secondLevel = new Fdn(firstLevel, "EUtranCellFDD", "EUtranCellFDD001-1");

    @Test
    public void whenEquals_withValidFdn_thenReturnTrue() {
        final Fdn rootFdn = new Fdn(rootLevel, "ENodeBFunction", "1");

        final boolean isEqual = firstLevel.equals(rootFdn);

        assertTrue(isEqual, "Fdn objects do not match.");
    }

    @Test
    public void whenGetLength_withValidFdn_thenReturnFdnLength() {
        final int actualLength = secondLevel.getLength();

        assertEquals(actualLength, 3, "The Fdn length does not match '3'.");
    }

    @Test
    public void whenGetParent_withSecondLevelFdn_thenReturnFirstLevelFdn() {
        assertEquals(secondLevel.getParentFdn(), firstLevel, "The parent of the seconds-level FDN does not match the first-level FDN.");
    }

    @Test
    public void whenGetType_withValidFdn_thenReturnFdnType() {
        final String expectedFdnType = "EUtranCellFDD";

        assertEquals(secondLevel.getType(), expectedFdnType, "The type of RDN does not match expected value.");
    }

    @Test
    public void whenGetName_with_then() {
        final String expectedFdnName = "EUtranCellFDD001-1";

        assertEquals(secondLevel.getName(), expectedFdnName, "The name of RDN does not match expected value.");
    }

    @Test
    public void whenToString_withFdnHavingNoParent_thenReturnLengthOneFdn() {
        final String expectedStringValue = "ManagedElement=LTE01ERBS001";

        assertEquals(rootLevel.toString(), expectedStringValue, "The FDN as a string does not match expected value.");
    }

    @Test
    public void whenToString_withFdnHavingTwoParentLevels_thenReturnLengthThreeFdn() {
        final String expectedStringValue = "ManagedElement=LTE01ERBS001,ENodeBFunction=1,EUtranCellFDD=EUtranCellFDD001-1";

        assertEquals(secondLevel.toString(), expectedStringValue, "The FDN as a string does not match expected value.");
    }

}
