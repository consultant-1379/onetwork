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

package com.ericsson.de.onetwork.bs;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Verification of NameGenerator method.
 *
 * @author ecasjim
 */
public class NameGeneratorTest {

    @Test
    public final void verifyLTEMacroNodeNameReturnedFromLTEMimName() {
        final String mimName = "LTE ERBS F1101";
        final NameGenerator nameGenerator = new NameGenerator();
        Assert.assertEquals(nameGenerator.getNextNeName(mimName), "LTE01ERBS00001");
    }

    @Test
    public final void verifyLTEPicoNodeNameReturnedFromLTEMimName() {
        final String mimName = "LTE PRBS F1101";
        final NameGenerator nameGenerator = new NameGenerator();
        Assert.assertEquals(nameGenerator.getNextNeName(mimName), "LTE01PRBS00001");

        for (int i = 1; i < NameGenerator.NODES_PER_SUBNET_NETSIM_LTE; i++) {
            nameGenerator.getNextNeName(mimName);
        }

        Assert.assertEquals(nameGenerator.getNextNeName(mimName), "LTE02PRBS00001");
    }

    @Test
    public final void verifyCORE_SGSNNodeNameReturnedFromCORE_SGSNMimName() {
        final String mimName = "CORE SGSN F1101";
        final NameGenerator nameGenerator = new NameGenerator();

        Assert.assertEquals(nameGenerator.getNextNeName(mimName), "CORE01SGSN00001");

        for (int i = 1; i < NameGenerator.NODES_PER_SUBNET_NETSIM_CORE; i++) {
            nameGenerator.getNextNeName(mimName);
        }

        Assert.assertEquals(nameGenerator.getNextNeName(mimName), "CORE02SGSN00001");
    }
}
