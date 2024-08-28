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
import com.ericsson.de.onetwork.dms.generics.MoFactory;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.gnm.Gnm;
import com.ericsson.de.onetwork.gnm.GnmManager;
import com.ericsson.de.onetwork.gnm.GnmRequestException;
import com.ericsson.de.onetwork.gnm.GnmXmlReader;

/**
 * Used to verify Utran External Network.
 *
 * @author ecasjim
 */
public class UtranExtNetworkIT {

    private final static Logger logger = LoggerFactory.getLogger(UtranExtNetworkIT.class.getName());
    private final static Network NETWORK = new _1Network();
    private static GnmXmlReader gnmReader = new GnmXmlReader();;
    private final static String GNM_REVISION = "LTE_R1";
    private final static String BASE_GNM_REVISION = "LTE_Base";
    private final static RuleManager RULE_MANAGER = new RuleManager(new MoFactory(true));
    private final static int TOTAL_NODES = 10;
    private final static FeatureManager FEATURE_MANAGER = new FeatureManager();
    final static FeatureModule cellPatternAssignmentFeature = FEATURE_MANAGER.getFeatureModule("Cell Pattern Assignment");
    final static FeatureModule UtranFeature = FEATURE_MANAGER.getFeatureModule("Utra Support");
    private static Gnm lteGnm = null;

    @BeforeClass
    public static void setup() throws GnmRequestException {
        logger.debug("Starting test UtranExtNetworkIT");
        addNodes(NETWORK);
        final RuleMap ruleMapUtranFeature = UtranFeature.getRules();
        final RuleMap ruleMapEUtranCells = cellPatternAssignmentFeature.getRules();

        lteGnm = GnmManager.getGnm(GNM_REVISION);

        final Map<RuleConfig, Rule> ruleObjectMapCells = RuleUtility.getRuleObjectsFromRuleNames(RULE_MANAGER, ruleMapEUtranCells.getMap());
        final Map<RuleConfig, Rule> ruleObjectMapUtranFeature = RuleUtility.getRuleObjectsFromRuleNames(RULE_MANAGER, ruleMapUtranFeature.getMap());
        ruleObjectMapCells.putAll(ruleObjectMapUtranFeature);

        for (final Map.Entry<RuleConfig, Rule> entry : ruleObjectMapCells.entrySet()) {
            final Rule rule = entry.getValue();
            final RuleConfig ruleConfig = entry.getKey();
            ruleConfig.setGnm(lteGnm);
            rule.apply(NETWORK, ruleConfig);
        }
    }

    @Test(enabled = true)
    public final void verifyUtraNetworkCreatedByFeature() throws GnmRequestException, InvalidChildException {
        final int totalExpectedMos = TOTAL_NODES;
        final String searchedMos = "ManagedElement,ENodeBFunction,UtraNetwork";
        final int totalFound = getMoCountFromNetwork(NETWORK, searchedMos);

        logger.warn("Mo Count: {}, Expected {} : Found {}", searchedMos, totalExpectedMos, totalFound);
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, searchedMos), totalExpectedMos);
    }

    @Test(enabled = true)
    public final void verifyExternalUtranCellsCreatedByFeature() throws GnmRequestException, InvalidChildException {
        final String totalExternalUtranCellFDDPerCell =
                gnmReader.getGNMData("fdd_frequencies,totalExternalUtranCellFDDPerCell", BASE_GNM_REVISION);
        final String searchedMos = "ManagedElement,ENodeBFunction,UtraNetwork,UtranFrequency,ExternalUtranCellFDD";
        final Double totalExpectedMos = TOTAL_NODES * Integer.parseInt(totalExternalUtranCellFDDPerCell) * lteGnm.getCellPatternAverage();
        final int totalFound = getMoCountFromNetwork(NETWORK, searchedMos);

        logger.warn("Mo Count: {}, Expected {} : Found {}", searchedMos, totalExpectedMos.intValue(), totalFound);
        Assert.assertEquals(totalFound, totalExpectedMos.intValue());
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
