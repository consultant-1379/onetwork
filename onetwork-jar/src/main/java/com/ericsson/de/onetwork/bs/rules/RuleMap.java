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

package com.ericsson.de.onetwork.bs.rules;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines how an MO will behave within a feature.
 * Using the MO path, the MOs position within a node is defined.
 * Example of an MO path: "ManagedElement -> ENodeBFunction -> EUtranCellFDD"
 * <p>
 * Actual implementation of MO path string is comma separated MO names,
 * Example: "ManagedElement,ENodeBFunction,EUtranCellFDD"
 *
 * @author ecasjim
 */
public class RuleMap {

    /**
     * Used to store the <code>Rule</code> names (Key) and
     * the associated Rule Configuration which determines how the rule is used.
     */
    private final Map<String, RuleConfig> map;

    /**
     * Default constructor
     */
    public RuleMap() {
        map = new HashMap<String, RuleConfig>();
    }

    /**
     * Constructor consumes the Map of rule data.
     *
     * @param map
     *            map of rule names and associated rule configuration
     */
    public RuleMap(final Map<String, RuleConfig> map) {
        this.map = map;
    }

    /**
     * Returns the map containing rule data.
     *
     * @return rule data map
     */
    public Map<String, RuleConfig> getMap() {
        return map;
    }

    /**
     * Used to combine rule maps.
     *
     * @param map
     *            rule map
     */
    public void addAdditionalRules(final Map<String, RuleConfig> map) {
        this.map.putAll(map);
    }
}
