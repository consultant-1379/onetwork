
package com.ericsson.de.onetwork.ss.util;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.gnm.GnmRequestException;

public class NetsimUtilityTest {

    private static final Logger logger = LoggerFactory.getLogger(NetsimUtilityTest.class);

    private static final String LTE_SIM_FILE_NAME = "LTEF1101x12-GEN-LTE01";
    private static final String CORE_SIM_FILE_NAME = "CORE15A-WPP-V4x10-GEN-CORE01";

    private static final String LTE_SIM_MIM_VERSION = "LTE ERBS F1101";
    private static final String CORE_SIM_MIM_VERSION = "CORE SGSN 15A-WPP-V4";

    private Properties netsimDataProps;

    private @Mock NetworkElement mockedNetworkElement;
    private @Mock Network mockedNetwork;
    private List<NetworkElement> networkElements;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeClass
    public void beforeClass() throws IOException {
        logger.debug("Starting NetsimUtilityTest");
        logger.debug("BEFORE_CLASS starts..");

        netsimDataProps = new Properties();
        try (final InputStream in = this.getClass().getResourceAsStream("/ss/util/netsim_utility_test_data.props")) {
            netsimDataProps.load(in);
            logger.info("netsim_utility_test_data.props file is loaded successfully.");
        }
        logger.debug("BEFORE_CLASS ends..");
    }

    @Test
    public void createSimMml_ForLTE() {
        final String actual = removeExtraSpaces(NetsimUtility.createSimMml(LTE_SIM_FILE_NAME));
        final String expected = netsimDataProps.getProperty("createSimMml_DataLTE");
        logger.debug("actual={}", actual);
        Assert.assertEquals(actual, expected, "Simulation MML was not created correctly for LTE");
    }

    @Test
    public void createSimMml_ForCORE() {
        final String actual = removeExtraSpaces(NetsimUtility.createSimMml(CORE_SIM_FILE_NAME));
        final String expected = netsimDataProps.getProperty("createSimMml_DataCORE");
        logger.debug("actual={}", actual);
        Assert.assertEquals(actual, expected, "Simulation MML was not created correctly for CORE");
    }

    @Test
    public void createSaveAndCompressSimMml() {
        final String actual = removeExtraSpaces(NetsimUtility.createSaveAndCompressSimMml());
        final String expected = netsimDataProps.getProperty("createSaveAndCompressSimMml_Data");
        logger.debug("actual={}", actual);
        Assert.assertEquals(actual, expected, "Simulation MML was not created correctly");
    }

    @Test
    public void createNesMmlForLTE() {
        final String simName = LTE_SIM_FILE_NAME;
        final String port = "IIOP_PROT";
        final String prefixName = "LTE01";
        final String neType = "ERBS";
        final int numOfNes = 12;
        final String neVersion = "";
        final int offset = 1;
        final String actual = removeExtraSpaces(NetsimUtility.createNesMml(simName, port, prefixName, neType, numOfNes, neVersion, offset));
        final String expected = netsimDataProps.getProperty("createNesMml_DataLTE");
        logger.debug("actual={}", actual);
        Assert.assertEquals(actual, expected, "Simulation MML was not created correctly");
    }

    @Test
    public void createNesMmlForCORE() {
        final String simName = CORE_SIM_FILE_NAME;
        final String port = "NETCONF_PROT_SSH";
        final String prefixName = "CORE01";
        final String neType = "SGSN";
        final int numOfNes = 10;
        final String neVersion = "";
        final int offset = 1;
        final String actual = removeExtraSpaces(NetsimUtility.createNesMml(simName, port, prefixName, neType, numOfNes, neVersion, offset));
        final String expected = netsimDataProps.getProperty("createNesMml_DataCORE");
        logger.debug("actual={}", actual);
        Assert.assertEquals(actual, expected, "Simulation MML was not created correctly");
    }

    @Test
    public void allocateSimulations_forZeroSims_inLTE() throws GnmRequestException, InvalidMimVersionFormatException {

        final int num = 0;
        networkElements = getPopulatedNetworkElements(num);

        final int actualSimSize = NetsimUtility.allocateSimulations(mockedNetwork).size();
        final int expectedSimSize = 0;

        Assert.assertEquals(actualSimSize, expectedSimSize);
    }

    @Test
    public void allocateSimulations_forOneSims_inLTE() throws GnmRequestException, InvalidMimVersionFormatException {

        final int num = NetsimUtility.NODES_PER_SUBNET_NETSIM_LTE;
        networkElements = getPopulatedNetworkElements(num);

        when(mockedNetworkElement.getMimVersion()).thenReturn(LTE_SIM_MIM_VERSION);
        when(mockedNetwork.getNetworkElements()).thenReturn(networkElements);

        final int actualSimSize = NetsimUtility.allocateSimulations(mockedNetwork).size();
        final int expectedSimSize = 1;

        Assert.assertEquals(actualSimSize, expectedSimSize);
    }

    @Test
    public void allocateSimulations_forTwoSims_inLTE() throws GnmRequestException, InvalidMimVersionFormatException {

        final int num = NetsimUtility.NODES_PER_SUBNET_NETSIM_LTE + 1;
        networkElements = getPopulatedNetworkElements(num);

        when(mockedNetworkElement.getMimVersion()).thenReturn(LTE_SIM_MIM_VERSION);
        when(mockedNetwork.getNetworkElements()).thenReturn(networkElements);

        final int actualSimSize = NetsimUtility.allocateSimulations(mockedNetwork).size();
        final int expectedSimSize = 2;

        Assert.assertEquals(actualSimSize, expectedSimSize);
    }

    @Test
    public void allocateSimulations_forOneSims_inCORE() throws GnmRequestException, InvalidMimVersionFormatException {

        final int num = NetsimUtility.NODES_PER_SUBNET_NETSIM_CORE;
        networkElements = getPopulatedNetworkElements(num);

        when(mockedNetworkElement.getMimVersion()).thenReturn(CORE_SIM_MIM_VERSION);
        when(mockedNetwork.getNetworkElements()).thenReturn(networkElements);

        final int actualSimSize = NetsimUtility.allocateSimulations(mockedNetwork).size();
        final int expectedSimSize = 1;

        Assert.assertEquals(actualSimSize, expectedSimSize);
    }

    @Test
    public void allocateSimulations_forThreeSims_inCORE() throws GnmRequestException, InvalidMimVersionFormatException {

        final int num = NetsimUtility.NODES_PER_SUBNET_NETSIM_CORE * 2 + 1;
        networkElements = getPopulatedNetworkElements(num);

        when(mockedNetworkElement.getMimVersion()).thenReturn(CORE_SIM_MIM_VERSION);
        when(mockedNetwork.getNetworkElements()).thenReturn(networkElements);

        final int actualSimSize = NetsimUtility.allocateSimulations(mockedNetwork).size();
        final int expectedSimSize = 3;

        Assert.assertEquals(actualSimSize, expectedSimSize);
    }

    @Test
    public void getBasicNetworkElementVersion() throws InvalidMimVersionFormatException {
        final String expectedBasicNEVersionForLTE = "F1101";
        final String expectedBasicNEVersionForCORE = "15A-WPP-V4";
        Assert.assertEquals(NetsimUtility.getNetworkElementVersion(LTE_SIM_MIM_VERSION), expectedBasicNEVersionForLTE);
        Assert.assertEquals(NetsimUtility.getNetworkElementVersion(CORE_SIM_MIM_VERSION), expectedBasicNEVersionForCORE);
    }

    @Test
    public void getNetworkType() throws InvalidMimVersionFormatException {
        final String expectedNwTypeForLTE = "LTE";
        final String expectedNwTypeForCORE = "CORE";
        Assert.assertEquals(NetsimUtility.getNetworkType(LTE_SIM_MIM_VERSION), expectedNwTypeForLTE);
        Assert.assertEquals(NetsimUtility.getNetworkType(CORE_SIM_MIM_VERSION), expectedNwTypeForCORE);
    }

    @Test(expectedExceptions = InvalidMimVersionFormatException.class)
    public void getNetworkType_with_incorrect_mimversion_inCORE() throws InvalidMimVersionFormatException {
        NetsimUtility.getNetworkType("");
    }

    @Test
    public void getNetworkElementType() throws InvalidMimVersionFormatException {
        final String expectedNwTypeForLTE = "ERBS";
        final String expectedNwTypeForCORE = "SGSN";
        Assert.assertEquals(NetsimUtility.getNetworkElementType(LTE_SIM_MIM_VERSION), expectedNwTypeForLTE);
        Assert.assertEquals(NetsimUtility.getNetworkElementType(CORE_SIM_MIM_VERSION), expectedNwTypeForCORE);
    }

    @Test(expectedExceptions = InvalidMimVersionFormatException.class)
    public void getNetworkElementType_with_incorrect_mimversion_inLTE() throws InvalidMimVersionFormatException {
        NetsimUtility.getNetworkElementType("LTE ERBS_XX");
    }

    @Test
    public void createKertayleMml() {
        final String networkElementName = "LTE01ERBS00001";
        final String moScriptFileName = "LTE01ERBS0001.mo";
        final String actual = removeExtraSpaces(NetsimUtility.createKertayleMml(networkElementName, moScriptFileName));
        final String expected = netsimDataProps.getProperty("createKertayleMml_Data");
        logger.debug("actual={}", actual);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void createStartNetworkMml() {
        final String actual = removeExtraSpaces(NetsimUtility.createStartNetworkMml());
        final String expected = netsimDataProps.getProperty("createStartNetworkMml_Data");
        logger.debug("actual={}", actual);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void getNetworkElementPort() {
        final String neType = "ERBS";
        final String actual = NetsimUtility.getNetworkElementPort(neType);
        final String expected = "IIOP_PROT";
        logger.debug("actual={}", actual);
        Assert.assertEquals(actual, expected);
    }

    private String removeExtraSpaces(final String str) {
        return str.replaceAll("\\s+", " ").trim();
    }

    private List<NetworkElement> getPopulatedNetworkElements(final int num) {
        final List<NetworkElement> networkElementsss = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            networkElementsss.add(mockedNetworkElement);
        }
        return networkElementsss;
    }
}
