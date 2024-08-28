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

package com.ericsson.de.onetwork.gnm;

import static org.testng.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.NetworkType;

/**
 * Verifies that the XML Gnm can be read correctly.
 *
 * @author ecasjim
 */
public class GnmXmlReaderTest {

    private final static String testGnmXmlfile = "TEST";
    private static GnmXmlReader xmlReader;
    private static Gnm gnm;

    @BeforeClass
    public static void setUp() throws GnmRequestException {
        xmlReader = new GnmXmlReader();
        gnm = xmlReader.getGnm(testGnmXmlfile);
    }

    @Test
    public final void readRevision() {
        assertEquals(gnm.getGnmRevision(), testGnmXmlfile);
    }

    @Test
    public final void readNetworkType() {
        assertEquals(gnm.getNetworkType(), NetworkType.LTE);
    }

    @Test
    public final void confirmAllMimPercentagesAreRead() {
        assertEquals(gnm.getMimUsage().size(), 1);
    }

    @Test
    public final void confirmMimPercentagesTotalOneHundred() {
        final Map<String, Double> mimUsageMap = gnm.getMimUsage();
        final Collection<Double> mimUsageMapValues = mimUsageMap.values();
        Double totalPercentage = 0.0;

        for (final Double mimPercent : mimUsageMapValues) {
            totalPercentage += mimPercent;
        }

        assertEquals(totalPercentage.intValue(), 100);
    }

    @Test
    public final void getBaseGNMValue() throws GnmRequestException {
        assertEquals(xmlReader.getGNMData("outertest,test,innertest", testGnmXmlfile), "37");
    }
}
