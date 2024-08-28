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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.MimXmlFileTestConstants;
import com.ericsson.de.onetwork.gnm.Gnm;
import com.ericsson.de.onetwork.gnm.GnmManager;
import com.ericsson.de.onetwork.gnm.GnmRequestException;

/**
 * Used to verify MimSelector methods.
 *
 * @author ecasjim
 */
public class MimSelectorTest {

    private static final String GNM_XML_FILE_REVISION = "LTE_R1";
    private static int TOTAL_NETWORK_ELEMENTS = 160;
    private static Gnm GNM;
    private static MimSelector MIM_SELECTOR;
    private static String mim = MimXmlFileTestConstants.CPP_MIM;

    @BeforeMethod
    public static void setup() throws GnmRequestException {
        GNM = GnmManager.getGnm(GNM_XML_FILE_REVISION);
        MIM_SELECTOR = new MimSelector(GNM, TOTAL_NETWORK_ELEMENTS);
    }

    @Test
    public final void verifyMimSelection() {
        Assert.assertEquals(MIM_SELECTOR.getNextMimType(), mim);
    }
}
