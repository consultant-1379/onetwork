/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.ss;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.dms.generics.Fdn;
import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;

/**
 * Verifies SimMoDataGenerator methods.
 *
 * @author qfatonu
 */
public class SimMoDataGeneratorTest {

    private static final Logger logger = LoggerFactory.getLogger(SimMoDataGeneratorTest.class);

    private Properties simMoDataProps;

    private @Mock NetworkElement mockedNetworkElement;
    private @Mock Mo mockedMo;
    private @Mock Mo childMockedMo;

    private List<NetworkElement> networkElements;
    private Sim sim;
    private SimMoDataGenerator simMoDataGen;

    private String testingMethodName;

    @BeforeMethod
    public void beforeMethod(final Method method) {
        MockitoAnnotations.initMocks(this);
        testingMethodName = method.getName();

        networkElements = getPopulatedNetworkElements(1);
        sim = new Sim(networkElements);
        simMoDataGen = new SimMoDataGenerator();
    }

    @BeforeClass
    public void beforeClass() throws IOException {
        logger.debug("BEFORE_CLASS starts..");

        simMoDataProps = new Properties();
        try (final InputStream in = this.getClass().getResourceAsStream("/ss/sim_mo_data_generator_test_data.props")) {
            simMoDataProps.load(in);
            logger.info("sim_mo_data_generator_test_data.props file is loaded successfully.");
        }
        logger.debug("BEFORE_CLASS ends..");

    }

    @Test
    public void loadMoScriptOntoSim_WithSingleSystemCreatedMo_AndNoChildren_EmptyMoScript() {

        when(mockedNetworkElement.getName()).thenReturn("LTE01ERBS0001");
        logger.debug("neName={}", mockedNetworkElement.getName());

        when(mockedMo.isSystemCreated()).thenReturn(true);
        when(mockedNetworkElement.getRootMo()).thenReturn(mockedMo);
        when(mockedMo.getChildren()).thenReturn(new ArrayList<Mo>());

        logger.debug("neSize={}", sim.getNetworkElements().size());
        logger.debug("sim.getNetworkElements().get(0).getName()={}", sim.getNetworkElements().get(0).getName());

        simMoDataGen.loadMoScriptsOnto(sim);
        final String actual = removeExtraSpaces(sim.getNetworkElementToMoScriptMap().get("LTE01ERBS0001"));
        logger.debug("actual-{}=\n{}", testingMethodName, actual);
        final String expected = "";
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void loadMoScriptOntoSim_WithSingleNonSystemCreatedMo_SingleCreateMoScriptWithoutAttr() {

        when(mockedNetworkElement.getName()).thenReturn("LTE01ERBS0001");
        logger.debug("neName={}", mockedNetworkElement.getName());

        when(mockedNetworkElement.getRootMo()).thenReturn(mockedMo);
        when(mockedMo.isSystemCreated()).thenReturn(false);

        logger.debug("neSize={}", sim.getNetworkElements().size());
        logger.debug("sim.getNetworkElements().get(0).getName()={}", sim.getNetworkElements().get(0).getName());

        simMoDataGen.loadMoScriptsOnto(sim);
        final String actual = removeExtraSpaces(sim.getNetworkElementToMoScriptMap().get("LTE01ERBS0001"));
        logger.debug("actual-{}=\n{}", testingMethodName, actual);
        final String expected = simMoDataProps.getProperty("SingleCreateMoScriptWithoutAttr");
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void loadMoScriptOntoSim_WithSingleSystemCreated_AndSingleChildMo_AndFakeValues_SingleCreateMoScriptWithFakeValuesWithoutAttr() {

        when(mockedNetworkElement.getName()).thenReturn("LTE01ERBS0001");
        logger.debug("neName={}", mockedNetworkElement.getName());

        when(mockedMo.isSystemCreated()).thenReturn(true);
        when(mockedNetworkElement.getRootMo()).thenReturn(mockedMo);

        when(childMockedMo.getParentFdn()).thenReturn(new Fdn(null, "fake-parent", "1"));
        when(childMockedMo.getName()).thenReturn("fake-name");
        when(childMockedMo.getType()).thenReturn("fake-type");
        final List<Mo> children = new ArrayList<>();
        children.add(childMockedMo);
        when(mockedMo.getChildren()).thenReturn(children);

        logger.debug("neSize={}", sim.getNetworkElements().size());
        logger.debug("sim.getNetworkElements().get(0).getName()={}", sim.getNetworkElements().get(0).getName());

        simMoDataGen.loadMoScriptsOnto(sim);
        final String actual = removeExtraSpaces(sim.getNetworkElementToMoScriptMap().get("LTE01ERBS0001"));
        logger.debug("actual-{}=\n{}", testingMethodName, actual);

        final String expected = simMoDataProps.getProperty("SingleCreateMoScriptWithFakeValuesWithoutAttr");
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void loadMoScriptOntoSim_WithSingleNonSystemCreatedMo_AndSingleNullFeaturePopulatedAttr_SingleCreateMoScriptWithoutAttr() {

        when(mockedNetworkElement.getName()).thenReturn("LTE01ERBS0001");
        logger.debug("neName={}", mockedNetworkElement.getName());

        when(mockedMo.isSystemCreated()).thenReturn(false);
        when(mockedNetworkElement.getRootMo()).thenReturn(mockedMo);
        final List<String> listOfAttrs = new ArrayList<String>();
        listOfAttrs.add("userLabel");
        when(mockedMo.getFeaturePopulatedAttributeNames()).thenReturn(listOfAttrs);

        logger.debug("neSize={}", sim.getNetworkElements().size());
        logger.debug("sim.getNetworkElements().get(0).getName()={}", sim.getNetworkElements().get(0).getName());

        simMoDataGen.loadMoScriptsOnto(sim);
        final String actual = removeExtraSpaces(sim.getNetworkElementToMoScriptMap().get("LTE01ERBS0001"));
        logger.debug("actual-{}=\n{}", testingMethodName, actual);

        final String expected = simMoDataProps.getProperty("SingleCreateMoScriptWithoutAttr");
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void loadMoScriptOntoSim_WithSingleNonSystemCreatedMo_AndSingleUserLabelFeaturePopulatedAttr_SingleCreateMoScriptWithUserLabelAttr() {

        when(mockedNetworkElement.getName()).thenReturn("LTE01ERBS0001");
        logger.debug("neName={}", mockedNetworkElement.getName());

        when(mockedMo.isSystemCreated()).thenReturn(false);
        when(mockedNetworkElement.getRootMo()).thenReturn(mockedMo);
        final List<String> listOfAttrs = new ArrayList<String>();
        listOfAttrs.add("userLabel");
        when(mockedMo.getFeaturePopulatedAttributeNames()).thenReturn(listOfAttrs);
        when(mockedMo.getAttributeNames()).thenReturn(listOfAttrs);
        // string attribute
        final MoAttributeDataType moAttrDataType = new MoAttributeDataType("string", "fake-user-label");
        when(mockedMo.getAttributeByName("userLabel")).thenReturn(moAttrDataType);

        logger.debug("neSize={}", sim.getNetworkElements().size());
        logger.debug("sim.getNetworkElements().get(0).getName()={}", sim.getNetworkElements().get(0).getName());

        simMoDataGen.loadMoScriptsOnto(sim);
        final String actual = removeExtraSpaces(sim.getNetworkElementToMoScriptMap().get("LTE01ERBS0001"));
        logger.debug("actual-{}=\n{}", testingMethodName, actual);

        final String expected = simMoDataProps.getProperty("SingleCreateMoScriptWithUserLabelAttr");
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void loadMoScriptOntoSim_WithSingleNonSystemCreatedMo_AndStructArrayFeaturePopulatedAttr_SingleCreateMoScriptWithStructArrayAttr() {

        when(mockedNetworkElement.getName()).thenReturn("LTE01ERBS0001");
        logger.debug("neName={}", mockedNetworkElement.getName());

        when(mockedMo.isSystemCreated()).thenReturn(false);
        when(mockedNetworkElement.getRootMo()).thenReturn(mockedMo);
        final List<String> listOfAttrs = new ArrayList<String>();
        listOfAttrs.add("userLabel");
        listOfAttrs.add("healthCheckSchedule");
        when(mockedMo.getFeaturePopulatedAttributeNames()).thenReturn(listOfAttrs);
        when(mockedMo.getAttributeNames()).thenReturn(listOfAttrs);

        // empty string attribute
        final MoAttributeDataType moAttrDataTypeInString = new MoAttributeDataType("string", "");
        // array of struct attribute
        final List<MoAttributeDataType> healthCheckScheduleArray = new ArrayList<MoAttributeDataType>();
        final Map<String, MoAttributeDataType> scheduleEntryStruct1 = new TreeMap<String, MoAttributeDataType>();
        scheduleEntryStruct1.put("time", new MoAttributeDataType("String", "tttime1"));
        scheduleEntryStruct1.put("weekday", new MoAttributeDataType("String", "wwwekday1"));
        healthCheckScheduleArray.add(new MoAttributeDataType("Struct", scheduleEntryStruct1));
        final MoAttributeDataType moAttrDataTypeInSequence = new MoAttributeDataType("Sequence", healthCheckScheduleArray);

        when(mockedMo.getAttributeByName("userLabel")).thenReturn(moAttrDataTypeInString);
        when(mockedMo.getAttributeByName("healthCheckSchedule")).thenReturn(moAttrDataTypeInSequence);

        logger.debug("neSize={}", sim.getNetworkElements().size());
        logger.debug("sim.getNetworkElements().get(0).getName()={}", sim.getNetworkElements().get(0).getName());

        simMoDataGen.loadMoScriptsOnto(sim);
        final String actual = removeExtraSpaces(sim.getNetworkElementToMoScriptMap().get("LTE01ERBS0001"));
        logger.debug("actual-{}=\n{}", testingMethodName, actual);

        final String expected = simMoDataProps.getProperty("SingleCreateMoScriptWithStructArrayAttr");
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void loadMoScriptOntoSim_WithSingleNonSystemCreatedMo_AndStructFeaturePopulatedAttr_SingleCreateMoScriptWithStructAttr() {

        when(mockedNetworkElement.getName()).thenReturn("LTE01ERBS0001");
        logger.debug("neName={}", mockedNetworkElement.getName());

        when(mockedMo.isSystemCreated()).thenReturn(false);
        when(mockedNetworkElement.getRootMo()).thenReturn(mockedMo);
        final List<String> listOfAttrs = new ArrayList<String>();
        listOfAttrs.add("healthCheckStruct");
        when(mockedMo.getFeaturePopulatedAttributeNames()).thenReturn(listOfAttrs);
        when(mockedMo.getAttributeNames()).thenReturn(listOfAttrs);

        // struct attribute
        final Map<String, MoAttributeDataType> healthCheckStructMap = new TreeMap<String, MoAttributeDataType>();
        healthCheckStructMap.put("message", new MoAttributeDataType("String", "mmmessage"));
        healthCheckStructMap.put("startTime", new MoAttributeDataType("String", "ssstartTime"));
        healthCheckStructMap.put("healthCheckResultCode", new MoAttributeDataType("Enum", "2"));
        final MoAttributeDataType moAttrDataTypeInStruct = new MoAttributeDataType("Struct", healthCheckStructMap);
        when(mockedMo.getAttributeByName("healthCheckStruct")).thenReturn(moAttrDataTypeInStruct);

        logger.debug("neSize={}", sim.getNetworkElements().size());
        logger.debug("sim.getNetworkElements().get(0).getName()={}", sim.getNetworkElements().get(0).getName());

        simMoDataGen.loadMoScriptsOnto(sim);
        final String actual = removeExtraSpaces(sim.getNetworkElementToMoScriptMap().get("LTE01ERBS0001"));
        logger.debug("actual-{}=\n{}", testingMethodName, actual);

        final String expected = simMoDataProps.getProperty("SingleCreateMoScriptWithStructAttr");
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void loadMoScriptOntoSim_WithSingleNonSystemCreatedMo_AndArrayFeaturePopulatedAttr_SingleCreateMoScriptWithArrayAttr() {

        when(mockedNetworkElement.getName()).thenReturn("LTE01ERBS0001");
        logger.debug("neName={}", mockedNetworkElement.getName());

        when(mockedMo.isSystemCreated()).thenReturn(false);
        when(mockedNetworkElement.getRootMo()).thenReturn(mockedMo);
        final List<String> listOfAttrs = new ArrayList<String>();
        listOfAttrs.add("acBarringForSpecialAC");
        when(mockedMo.getFeaturePopulatedAttributeNames()).thenReturn(listOfAttrs);
        when(mockedMo.getAttributeNames()).thenReturn(listOfAttrs);

        // array attribute
        final List<MoAttributeDataType> acBarringForSpecialACArray = new ArrayList<MoAttributeDataType>();
        acBarringForSpecialACArray.add(new MoAttributeDataType("Boolean", false));
        acBarringForSpecialACArray.add(new MoAttributeDataType("Boolean", false));
        acBarringForSpecialACArray.add(new MoAttributeDataType("Boolean", false));
        acBarringForSpecialACArray.add(new MoAttributeDataType("Boolean", false));
        final MoAttributeDataType moAttrDataTypeInArray = new MoAttributeDataType("Sequence", acBarringForSpecialACArray);

        when(mockedMo.getAttributeByName("acBarringForSpecialAC")).thenReturn(moAttrDataTypeInArray);

        logger.debug("neSize={}", sim.getNetworkElements().size());
        logger.debug("sim.getNetworkElements().get(0).getName()={}", sim.getNetworkElements().get(0).getName());

        simMoDataGen.loadMoScriptsOnto(sim);
        final String actual = removeExtraSpaces(sim.getNetworkElementToMoScriptMap().get("LTE01ERBS0001"));
        logger.debug("actual-{}=\n{}", testingMethodName, actual);

        final String expected = simMoDataProps.getProperty("SingleCreateMoScriptWithArrayAttr");
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
