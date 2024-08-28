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

import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.dms.generics.MoFactory;

/**
 * A rule defines how an MO will behave within a network.
 * An example of a rule would be how the alphanumerical name of a node
 * increments across a network, e.g. LTE01ERBS0001 is incremented to
 * LTE01ERBS0002.
 *
 * @author ecasjim
 */

public abstract class Rule {

    /** Name of rule */
    private final String name;

    /** MoFactory used to request the MOs required to satisfy this rule. */
    protected MoFactory moFactory;

    /**
     * Constructor takes name of rule.
     *
     * @param name
     *            rule name
     */
    public Rule(final String name) {
        this.name = name;
    }

    /**
     * Used to get the name of the rule.
     *
     * @return Rule name
     */
    public String getName() {
        return name;
    }

    /**
     * Applies a rule to a Network object. All the logic of the
     * <code>Rule</code> will start with this method.
     *
     * @param network
     *            Network object.
     * @param config
     *            object
     * @return Network object with rule applied
     */
    public abstract Network apply(Network network, RuleConfig config);

    /**
     * Gives a <code>Rule</code> access to the current <code>MoFactory</code>
     * for creating and setting MOs and attributes.
     *
     * @param moFactory
     *            current <code>MoFactory</code> object.
     */
    public void setMoFactory(final MoFactory moFactory) {
        this.moFactory = moFactory;
    };

}
