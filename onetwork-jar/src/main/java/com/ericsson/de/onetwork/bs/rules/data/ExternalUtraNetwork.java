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

package com.ericsson.de.onetwork.bs.rules.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleMap;
import com.ericsson.de.onetwork.gnm.GnmRequestException;
import com.ericsson.de.onetwork.gnm.GnmXmlReader;

/**
 * Rules associated with External Utran Network
 *
 * @author ecasjim
 */
public class ExternalUtraNetwork {

    private final static Logger logger = LoggerFactory.getLogger(EUtranRelations.class.getName());
    private static GnmXmlReader gnmReader = new GnmXmlReader();
    private final static String GNM_BASE = "LTE_Base";

    public static RuleMap getRules() {
        final Map<String, RuleConfig> ruleMap = new LinkedHashMap<String, RuleConfig>();
        Integer totalExternalUtranCellFDDPerCell = null;
        String utranFreqValue = null;

        try {
            utranFreqValue = gnmReader.getGNMData("fdd_frequencies,UtranFreqValue", GNM_BASE);
            totalExternalUtranCellFDDPerCell = Integer.parseInt(gnmReader.getGNMData("fdd_frequencies,totalExternalUtranCellFDDPerCell", GNM_BASE));
        } catch (final GnmRequestException e1) {
            logger.error("Could not open GNM {}", GNM_BASE);
        }

        // -----------Utra Network Rule Configuration-------------------------
        final RuleConfig UtraNetworkRuleConfig = new RuleConfig("ManagedElement,ENodeBFunction,UtraNetwork");
        ruleMap.put("BasicMoCreation_" + UtraNetworkRuleConfig.hashCode(), UtraNetworkRuleConfig);

        // -----------Utran Frequency Rule Configuration----------------------

        final RuleConfig UtranFrequencyRuleConfig = new RuleConfig("ManagedElement,ENodeBFunction,UtraNetwork,UtranFrequency", utranFreqValue);
        UtranFrequencyRuleConfig.setConstantValue(true);
        ruleMap.put("BasicMoCreation_" + UtranFrequencyRuleConfig.hashCode(), UtranFrequencyRuleConfig);

        // -----------External UtranCell FDD Rule Configuration---------------
        final RuleConfig ExternalUtranCellFDDRuleConfig =
                new RuleConfig("ManagedElement,ENodeBFunction,UtraNetwork,UtranFrequency,ExternalUtranCellFDD");
        ExternalUtranCellFDDRuleConfig.setNumberOfMOsAsAMultipleOfCellsOnNode(true);
        ExternalUtranCellFDDRuleConfig.setNumberOfMosPerCell(totalExternalUtranCellFDDPerCell);
        ExternalUtranCellFDDRuleConfig.setIncrementingAcrossNetwork(true);

        ruleMap.put("BasicMoCreation_" + ExternalUtranCellFDDRuleConfig.hashCode(), ExternalUtranCellFDDRuleConfig);

        // ----------------------------------------------------------
        return new RuleMap(ruleMap);
    }
}
