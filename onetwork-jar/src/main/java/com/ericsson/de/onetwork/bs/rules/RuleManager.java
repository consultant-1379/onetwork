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

import com.ericsson.de.onetwork.bs.rules.general.BasicMoCreation;
import com.ericsson.de.onetwork.bs.rules.lte.EUtranCellCreation;
import com.ericsson.de.onetwork.dms.generics.MoFactory;

/**
 * This class manages all the rules and allows 1Network developers to access
 * any of the previously developed rules when creating features.
 *
 * @author ecasjim
 */
public class RuleManager {

    /** Used to stores all available rules. */
    private final Map<String, Rule> rules = new HashMap<String, Rule>();

    /** Used by rules to get MOs. */
    private final MoFactory moFactory;

    /**
     * Constructor instantiates the MO factory and populates the rules map.
     *
     * @param moFactory
     *            MO Factory object
     */
    public RuleManager(final MoFactory moFactory) {
        this.moFactory = moFactory;
        populateRuleCollection();
    }

    /**
     * Retrieve a rule from the rule set.
     *
     * @param ruleName
     *            Name of the rule required
     * @return The Rule object
     */
    public Rule getRule(final String ruleName) {
        Rule rule = null;
        if (ruleName.contains("_")) {
            rule = rules.get(ruleName.split("_")[0]);
        }
        else {
            rule = rules.get(ruleName);
        }
        rule.setMoFactory(moFactory);
        return rule;
    }

    /**
     * TODO: Possibly move rule collection to database.
     * Populates map with currently implemented rules.
     */
    private void populateRuleCollection() {
        final String ruleName = "CellCreation";
        final Rule eUtranCellCreation = new EUtranCellCreation(ruleName);
        rules.put(ruleName, eUtranCellCreation);

        final String ruleName2 = "BasicMoCreation";
        final Rule MoCreation = new BasicMoCreation(ruleName2);
        rules.put(ruleName2, MoCreation);
    }
}
