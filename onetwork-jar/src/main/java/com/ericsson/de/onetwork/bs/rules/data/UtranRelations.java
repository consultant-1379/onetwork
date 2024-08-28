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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleMap;
import com.ericsson.de.onetwork.gnm.GnmRequestException;
import com.ericsson.de.onetwork.gnm.GnmXmlReader;

/**
 * Used to encapsulate Utran Relation rules.
 * TODO: Move data to DB
 *
 * @author ecasjim
 */
public class UtranRelations {

    private final static Logger logger = LoggerFactory.getLogger(UtranRelations.class.getName());
    private static GnmXmlReader gnmReader = new GnmXmlReader();
    private final static String GNM_BASE = "LTE_Base";
    private final static String GNM_R1 = "LTE_R1";

    public static RuleMap getRules() {
        final Map<String, RuleConfig> ruleMap = new LinkedHashMap<String, RuleConfig>();
        String utranFreqValue = null, totalUtranCellRelationPerCell = null, cellsPerNode = null, totalUtranFreqsPerCell = null;
        Integer totalExternalUtranCellFDDPerCell = null;

        try {
            utranFreqValue = gnmReader.getGNMData("fdd_frequencies,UtranFreqValue", GNM_BASE);
            totalUtranFreqsPerCell = gnmReader.getGNMData("fdd_frequencies,totalUtranFreqsPerCell", GNM_BASE);
            totalUtranCellRelationPerCell = gnmReader.getGNMData("fdd_frequencies,totalUtranCellRelations", GNM_BASE);
            cellsPerNode = gnmReader.getGNMData("cellpattern,cellnumber", GNM_R1);
            totalExternalUtranCellFDDPerCell = Integer.parseInt(gnmReader.getGNMData("fdd_frequencies,totalExternalUtranCellFDDPerCell", GNM_BASE));
        } catch (final GnmRequestException e1) {
            logger.error("Could not open GNM {}", GNM_BASE);
        }

        // -----------Utran FreqRelations Rule Configuration------------------
        final RuleConfig utranFreqRelationsRuleConfig =
                new RuleConfig("ManagedElement,ENodeBFunction,EUtranCellFDD,UtranFreqRelation", utranFreqValue);
        utranFreqRelationsRuleConfig.setConstantValue(true);
        utranFreqRelationsRuleConfig.setNumberOfMosPerNode(Integer.parseInt(totalUtranFreqsPerCell) * Integer.parseInt(cellsPerNode));
        ruleMap.put("BasicMoCreation_" + utranFreqRelationsRuleConfig.hashCode(), utranFreqRelationsRuleConfig);

        // -----------UtranFreqRelations Mo Ref Rule Configuration------------
        final RuleConfig utranFreqRelationMoRefRuleConfig =
                new RuleConfig("ManagedElement,ENodeBFunction,EUtranCellFDD,UtranFreqRelation");
        utranFreqRelationMoRefRuleConfig.setOnlySetAttributes(true);
        utranFreqRelationMoRefRuleConfig.setNumberOfMosPerNode(Integer.parseInt(totalUtranFreqsPerCell) * Integer.parseInt(cellsPerNode));
        ruleMap.put("BasicMoCreation_" + utranFreqRelationMoRefRuleConfig.hashCode(), utranFreqRelationMoRefRuleConfig);

        final Map<String, String> freqRefAttrMap = new HashMap<String, String>();
        freqRefAttrMap.put("utranFrequencyRef", "ManagedElement,ENodeBFunction,UtraNetwork,UtranFrequency");
        utranFreqRelationMoRefRuleConfig.setRefToBeSet(true);
        utranFreqRelationMoRefRuleConfig.setRefAttrMap(freqRefAttrMap);

        // -----------Utran Cell Relation Rule Configuration------------------
        final RuleConfig utranCellRelationsRuleConfig =
                new RuleConfig("ManagedElement,ENodeBFunction,EUtranCellFDD,UtranFreqRelation,UtranCellRelation");
        utranCellRelationsRuleConfig.setIncrementingAcrossNetwork(true);
        utranCellRelationsRuleConfig.setNumberOfMosPerCell(Integer.parseInt(totalUtranCellRelationPerCell));
        utranCellRelationsRuleConfig.setNumberOfMOsAsAMultipleOfCellsOnNode(true);

        ruleMap.put("BasicMoCreation_" + utranCellRelationsRuleConfig.hashCode(), utranCellRelationsRuleConfig);

        // -----------UtranCellRelation Mo Ref Rule Configuration-------------
        final RuleConfig utranCellRelationsMoRefRuleConfig =
                new RuleConfig("ManagedElement,ENodeBFunction,EUtranCellFDD,UtranFreqRelation,UtranCellRelation");
        utranCellRelationsMoRefRuleConfig.setOnlySetAttributes(true);
        utranCellRelationsMoRefRuleConfig.setNumberOfMOsAsAMultipleOfCellsOnNode(true);
        utranCellRelationsMoRefRuleConfig.setNumberOfMosPerCell(totalExternalUtranCellFDDPerCell);
        utranCellRelationsMoRefRuleConfig.setIncrementingAcrossNetwork(true);

        final Map<String, String> refAttrMap = new HashMap<String, String>();
        refAttrMap.put("externalUtranCellFDDRef", "ManagedElement,ENodeBFunction,UtraNetwork,UtranFrequency,ExternalUtranCellFDD");
        utranCellRelationsMoRefRuleConfig.setRefToBeSet(true);
        utranCellRelationsMoRefRuleConfig.setRefAttrMap(refAttrMap);

        ruleMap.put("BasicMoCreation_" + utranCellRelationsMoRefRuleConfig.hashCode(), utranCellRelationsMoRefRuleConfig);

        // -------------------------------------------------------------------
        return new RuleMap(ruleMap);
    }
}
