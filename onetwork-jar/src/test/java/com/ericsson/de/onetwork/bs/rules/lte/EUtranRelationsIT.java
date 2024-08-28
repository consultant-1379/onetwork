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

package com.ericsson.de.onetwork.bs.rules.lte;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.bs.NameGenerator;
import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.bs._1Network;
import com.ericsson.de.onetwork.bs.features.FeatureManager;
import com.ericsson.de.onetwork.bs.features.FeatureModule;
import com.ericsson.de.onetwork.bs.rules.Rule;
import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleManager;
import com.ericsson.de.onetwork.bs.rules.RuleMap;
import com.ericsson.de.onetwork.bs.rules.RuleUtility;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.generics.MoFactory;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;
import com.ericsson.de.onetwork.gnm.Gnm;
import com.ericsson.de.onetwork.gnm.GnmManager;
import com.ericsson.de.onetwork.gnm.GnmRequestException;
import com.ericsson.de.onetwork.gnm.GnmXmlReader;

/**
 * Used to verify the functionality of EUtranCellCreation.
 *
 * @author ecasjim
 */
public class EUtranRelationsIT {

    private final static Logger logger = LoggerFactory.getLogger(EUtranRelationsIT.class.getName());
    private final static Network NETWORK = new _1Network();
    private static GnmXmlReader gnmReader = new GnmXmlReader();;
    private final static String GNM_REVISION = "LTE_R1";
    private final static String BASE_GNM_REVISION = "LTE_Base";
    private final static RuleManager RULE_MANAGER = new RuleManager(new MoFactory(true));
    private final static int TOTAL_NODES = 10;
    private final static FeatureManager FEATURE_MANAGER = new FeatureManager();
    final static FeatureModule CellPatternAssignmentFeature = FEATURE_MANAGER.getFeatureModule("Cell Pattern Assignment");
    final static FeatureModule EUtranFeature = FEATURE_MANAGER.getFeatureModule("EUtran Support");
    static Gnm lteGnm = null;

    @BeforeClass
    public static void setup() throws GnmRequestException {
        logger.debug("Starting test EUtranRelationsIT");
        lteGnm = GnmManager.getGnm(GNM_REVISION);
        addNodes(NETWORK);

        final RuleMap ruleMapCellCreation = CellPatternAssignmentFeature.getRules();
        final RuleMap ruleMapEUtranRelations = EUtranFeature.getRules();
        lteGnm = GnmManager.getGnm(GNM_REVISION);

        final Map<RuleConfig, Rule> ruleObjectMap = RuleUtility.getRuleObjectsFromRuleNames(RULE_MANAGER, ruleMapCellCreation.getMap());
        final Map<RuleConfig, Rule> ruleObjectMap2 = RuleUtility.getRuleObjectsFromRuleNames(RULE_MANAGER, ruleMapEUtranRelations.getMap());

        ruleObjectMap.putAll(ruleObjectMap2);

        for (final Map.Entry<RuleConfig, Rule> entry : ruleObjectMap.entrySet()) {
            final Rule rule = entry.getValue();
            final RuleConfig ruleConfig = entry.getKey();
            ruleConfig.setGnm(lteGnm);
            rule.apply(NETWORK, ruleConfig);
        }
    }

    @Test
    public final void verifyCellsCreatedByFeature() throws GnmRequestException, InvalidChildException {
        final int totalExpectedCells = (int) Math.floor(lteGnm.getCellPatternAverage() * TOTAL_NODES);
        final String searchedMos = "ManagedElement,ENodeBFunction,EUtranCellFDD";
        final int totalFound = getMoCountFromNetwork(NETWORK, searchedMos);

        logger.warn("Mo Count: {}, Expected {} : Found {}", searchedMos, totalExpectedCells, totalFound);
        Assert.assertEquals(totalFound, totalExpectedCells);
    }

    @Test
    public final void verifyEUtranFreqRelationsCreatedByFeature() throws GnmRequestException, InvalidChildException {
        final String totalEutranFreqRelationsPerCell = gnmReader.getGNMData("fdd_frequencies,totalEUtranfreqsPerCell", BASE_GNM_REVISION);
        final int totalMosInNetwork = (int) (Integer.parseInt(totalEutranFreqRelationsPerCell) * TOTAL_NODES * lteGnm.getCellPatternAverage());
        final String searchedMos = "ManagedElement,ENodeBFunction,EUtranCellFDD,EUtranFreqRelation";
        final int totalFound = getMoCountFromNetwork(NETWORK,
                searchedMos);

        logger.warn("Mo Count: {}, Expected {} : Found {}", searchedMos, totalMosInNetwork, totalFound);
        Assert.assertEquals(totalFound, totalMosInNetwork);
    }

    @Test
    public final void verifyEUtranCellRelationsCreatedByFeature() throws GnmRequestException, InvalidChildException {
        final String totalEutranFreqRelationsPerNode = gnmReader.getGNMData("fdd_frequencies,totalEUtranCellRelations", BASE_GNM_REVISION);
        final int totalMosInNetwork = Integer.parseInt(totalEutranFreqRelationsPerNode) * TOTAL_NODES;

        final String searchedMos = "ManagedElement,ENodeBFunction,EUtranCellFDD,EUtranFreqRelation,EUtranCellRelation";
        final int totalFound = getMoCountFromNetwork(NETWORK, searchedMos);

        logger.warn("Mo Count: {}, Expected {} : Found {}", searchedMos, totalMosInNetwork, totalFound);
        Assert.assertEquals(totalFound, totalMosInNetwork);
    }

    // TODO: To be implemented in future sprint.
    @Test(enabled = false)
    public final void verifyMoRefAttributesSetByFeature() throws InvalidChildException {
        for (final NetworkElement node : NETWORK.getNetworkElements()) {
            final List<Mo> moList = node.getMosFromNe("ManagedElement,ENodeBFunction,EUtranCellFDD,EUtranFreqRelation");
            for (final Mo mo : moList) {
                final MoAttributeDataType moAttr = mo.getAttributeByName("eutranFrequencyRef");

                if (moAttr == null || moAttr.getValue() == null) {
                    Assert.fail("eutranFrequencyRef not set on " + mo.getFdn());
                }

                final String attrValue = moAttr.getValue().toString();
                if (attrValue == null || attrValue.equalsIgnoreCase("")) {
                    Assert.fail("eutranFrequencyRef not set on " + mo.getFdn());
                }
            }
        }

        Assert.assertTrue(true);
    }

    private int getMoCountFromNetwork(final Network network, final String moTypeHeirarchy) throws InvalidChildException {
        final List<NetworkElement> networkElements = network.getNetworkElements();
        int totalMos = 0;

        for (final NetworkElement networkElement : networkElements) {
            totalMos += networkElement.getMosFromNe(moTypeHeirarchy).size();
        }

        return totalMos;
    }

    private static void addNodes(final Network network) {
        final List<NetworkElement> networkElements = new ArrayList<NetworkElement>();
        final NameGenerator nameGenerator = new NameGenerator();

        for (int i = 0; i < TOTAL_NODES; i++) {
            networkElements.add(new NetworkElement("LTE ERBS F1101", nameGenerator.getNextNeName("ERBS")));
        }

        network.addNetworkElements(networkElements);
    }
}
