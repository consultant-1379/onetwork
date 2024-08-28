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

import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleMap;

/**
 * TODO: Move data to DB
 * Used to encapsulate LTE cell creation rules.
 *
 * @author ecasjim
 */
public class CellPattern {

    public static RuleMap getRules() {
        // As new rules are required for this feature, they are added here.
        final Map<String, RuleConfig> ruleMap = new LinkedHashMap<String, RuleConfig>();
        final RuleConfig ruleConfig = new RuleConfig("ManagedElement,ENodeBFunction,EUtranCellFDD");
        ruleConfig.setGnmRequired(true);
        ruleMap.put("CellCreation", ruleConfig);
        return new RuleMap(ruleMap);
    }
}
