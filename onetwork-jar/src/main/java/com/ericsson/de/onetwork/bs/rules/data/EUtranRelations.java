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
 * TODO: Move data to DB
 * Used to encapsulate EUtran Relations rules.
 *
 * @author ecasjim
 */
public class EUtranRelations {

    private final static Logger logger = LoggerFactory.getLogger(EUtranRelations.class.getName());
    private static GnmXmlReader gnmReader = new GnmXmlReader();
    private final static String GNM_BASE = "LTE_Base";
    private final static String GNM_R1 = "LTE_R1";

    public static RuleMap getRules() {
        final Map<String, RuleConfig> ruleMap = new LinkedHashMap<String, RuleConfig>();
        String totalEUtranFreqsPerCell = null, totalEUtranCellRelations = null;
        String cellsPerNode = null, maxEUtranFreqRealtionsValuePerCell = null;

        try {
            totalEUtranFreqsPerCell = gnmReader.getGNMData("fdd_frequencies,totalEUtranfreqsPerCell", GNM_BASE);
            maxEUtranFreqRealtionsValuePerCell = gnmReader.getGNMData("fdd_frequencies,maxEUtranFreqRealtionsValuePerCell", GNM_BASE);
            totalEUtranCellRelations = gnmReader.getGNMData("fdd_frequencies,totalEUtranCellRelations", GNM_BASE);
            cellsPerNode = gnmReader.getGNMData("cellpattern,cellnumber", GNM_R1);
        } catch (final GnmRequestException e1) {
            logger.error("Could not open GNM {}", GNM_BASE);
        }

        // -----------EUtranFreqRelations Rule Configuration-------------------
        final RuleConfig EUtranFreqRelationsRuleConfig = new RuleConfig("ManagedElement,ENodeBFunction,EUtranCellFDD,EUtranFreqRelation");
        EUtranFreqRelationsRuleConfig.setNumberOfMosPerNode(Integer.parseInt(totalEUtranFreqsPerCell) * Integer.parseInt(cellsPerNode));
        EUtranFreqRelationsRuleConfig.setNumberOfMosPerCell(Integer.parseInt(maxEUtranFreqRealtionsValuePerCell));
        EUtranFreqRelationsRuleConfig.setMaxValuePerCell(Integer.parseInt(maxEUtranFreqRealtionsValuePerCell));
        EUtranFreqRelationsRuleConfig.setIncrementingAcrossNode(true);

        final Map<String, String> refAttrMap = new HashMap<String, String>();
        refAttrMap.put("eutranFrequencyRef", "ManagedElement,ENodeBFunction,EUtraNetwork,EUtranFrequency");
        EUtranFreqRelationsRuleConfig.setRefToBeSet(true);
        EUtranFreqRelationsRuleConfig.setRefAttrMap(refAttrMap);

        ruleMap.put("BasicMoCreation_" + EUtranFreqRelationsRuleConfig.hashCode(), EUtranFreqRelationsRuleConfig);

        // ----------EUtran Cell Relations Rule Configuration------------------
        final RuleConfig cellRelationRuleConfig = new RuleConfig("ManagedElement,ENodeBFunction,EUtranCellFDD,EUtranFreqRelation,EUtranCellRelation");
        cellRelationRuleConfig.setNumberOfMosPerNode(Integer.parseInt(totalEUtranCellRelations));
        cellRelationRuleConfig.setIncrementingAcrossNode(true);
        ruleMap.put("BasicMoCreation_" + cellRelationRuleConfig.hashCode(), cellRelationRuleConfig);

        // --------------------------------------------------------------------
        return new RuleMap(ruleMap);
    }
}
