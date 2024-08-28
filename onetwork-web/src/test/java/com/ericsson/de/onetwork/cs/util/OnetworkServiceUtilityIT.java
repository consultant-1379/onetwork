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

package com.ericsson.de.onetwork.cs.util;

import static org.junit.Assert.assertFalse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.util.ServerUtility;

/**
 * Tests for OnetworkServiceUtilityIT class.
 *
 * @author ecasjim
 */
public class OnetworkServiceUtilityIT {

    private final static Logger logger = LoggerFactory.getLogger(OnetworkServiceUtilityIT.class);

    @BeforeClass
    public void setUp() {
        if (ServerUtility.isRunningOnWindowsServer()) {
            logger.debug("Skipping tests that use curl as on Windows machine.");
            throw new SkipException("Using curl will fail these tests on Windows. Aborting!");
        }
    }

    @Test
    public final void verifyPomVersionObtained() {
        assertFalse(OnetworkServiceUtility.extractVersionFromPom().contains("NoVersion"));
    }
}
