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
 * Used to verify UtranRelations.
 *
 * @author ecasjim
 */
public class UtranRelationsIT {

    private final static Logger logger = LoggerFactory.getLogger(UtranRelationsIT.class.getName());
    private final static Network NETWORK = new _1Network();
    private static GnmXmlReader gnmReader = new GnmXmlReader();;
    private final static String GNM_REVISION = "LTE_R1";
    private final static String BASE_GNM_REVISION = "LTE_Base";
    private final static RuleManager RULE_MANAGER = new RuleManager(new MoFactory(true));
    private final static int TOTAL_NODES = 10;
    private final static FeatureManager FEATURE_MANAGER = new FeatureManager();
    final static FeatureModule CellPatternAssignmentFeature = FEATURE_MANAGER.getFeatureModule("Cell Pattern Assignment");
    final static FeatureModule UtranFeature = FEATURE_MANAGER.getFeatureModule("Utra Support");
    static Gnm lteGnm = null;

    @BeforeClass
    public static void setup() throws GnmRequestException {
        logger.debug("Starting test UtranRelationsIT");
        addNodes(NETWORK);

        final RuleMap ruleMapCellCreation = CellPatternAssignmentFeature.getRules();
        final RuleMap ruleMapExtUtran = UtranFeature.getRules();

        lteGnm = GnmManager.getGnm(GNM_REVISION);

        final Map<RuleConfig, Rule> ruleObjectMap = RuleUtility.getRuleObjectsFromRuleNames(RULE_MANAGER, ruleMapCellCreation.getMap());
        final Map<RuleConfig, Rule> ruleObjectMapUtranFeature = RuleUtility.getRuleObjectsFromRuleNames(RULE_MANAGER, ruleMapExtUtran.getMap());

        ruleObjectMap.putAll(ruleObjectMapUtranFeature);

        for (final Map.Entry<RuleConfig, Rule> entry : ruleObjectMap.entrySet()) {
            final Rule rule = entry.getValue();
            final RuleConfig ruleConfig = entry.getKey();
            ruleConfig.setGnm(lteGnm);
            rule.apply(NETWORK, ruleConfig);
        }
    }

    @Test(enabled = true)
    public final void verifyUtranFreqRelationsCreatedByFeature() throws GnmRequestException, InvalidChildException {
        final String totalUtranFreqRelationsPerCell = gnmReader.getGNMData("fdd_frequencies,totalUtranFreqsPerCell", BASE_GNM_REVISION);
        final int totalExpected = (int) (Integer.parseInt(totalUtranFreqRelationsPerCell) * TOTAL_NODES * lteGnm.getCellPatternAverage());
        final String searchedMos = "ManagedElement,ENodeBFunction,EUtranCellFDD,UtranFreqRelation";
        final int totalFound = getMoCountFromNetwork(NETWORK, searchedMos);

        logger.warn("Mo Count: {}, Expected {} : Found {}", searchedMos, totalExpected, totalFound);
        Assert.assertEquals(totalFound, totalExpected);
    }

    @Test(enabled = true)
    public final void verifyUtranCellRelationsCreatedByFeature() throws GnmRequestException, InvalidChildException {
        final String totalUtranCellRelationsPerNode = gnmReader.getGNMData("fdd_frequencies,totalUtranCellRelations", BASE_GNM_REVISION);
        final int totalExpected = Integer.parseInt(totalUtranCellRelationsPerNode) * TOTAL_NODES * lteGnm.getCellPatternAverage().intValue();
        final String searchedMos = "ManagedElement,ENodeBFunction,EUtranCellFDD,UtranFreqRelation,UtranCellRelation";
        final int totalFound = getMoCountFromNetwork(NETWORK, searchedMos);

        logger.warn("Mo Count: {}, Expected {} : Found {}", searchedMos, totalExpected, totalFound);
        Assert.assertEquals(totalFound, totalExpected);
    }

    @Test(enabled = true)
    public final void verifyCellFDDMoRefAttributesSetByFeature() throws InvalidChildException {
        for (final NetworkElement node : NETWORK.getNetworkElements()) {
            final List<Mo> moList = node.getMosFromNe("ManagedElement,ENodeBFunction,EUtranCellFDD,UtranFreqRelation,UtranCellRelation");
            for (final Mo mo : moList) {
                final MoAttributeDataType moAttr = mo.getAttributeByName("externalUtranCellFDDRef");

                if (moAttr == null || moAttr.getValue() == null) {
                    Assert.fail("externalUtranCellFDDRef not set on " + mo.getFdn());
                }

                final String attrValue = moAttr.getValue().toString();
                if (attrValue == null || attrValue.equalsIgnoreCase("")) {
                    Assert.fail("externalUtranCellFDDRef not set on " + mo.getFdn());
                }
            }
        }

        Assert.assertTrue(true);
    }

    @Test(enabled = true)
    public final void verifyFreqMoRefAttributesSetByFeature() throws InvalidChildException {
        for (final NetworkElement node : NETWORK.getNetworkElements()) {
            final List<Mo> moList = node.getMosFromNe("ManagedElement,ENodeBFunction,EUtranCellFDD,UtranFreqRelation");
            for (final Mo mo : moList) {
                final MoAttributeDataType moAttr = mo.getAttributeByName("utranFrequencyRef");

                if (moAttr == null || moAttr.getValue() == null) {
                    Assert.fail("utranFrequencyRef not set on " + mo.getFdn());
                }

                final String attrValue = moAttr.getValue().toString();
                if (attrValue == null || attrValue.equalsIgnoreCase("")) {
                    Assert.fail("utranFrequencyRef not set on " + mo.getFdn());
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
