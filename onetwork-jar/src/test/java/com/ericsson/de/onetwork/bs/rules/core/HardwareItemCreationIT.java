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

package com.ericsson.de.onetwork.bs.rules.core;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.bs._1Network;
import com.ericsson.de.onetwork.bs.rules.Rule;
import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleManager;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.generics.MoFactory;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.gnm.GnmRequestException;

/**
 * Used to verify Hardware Item feature.
 *
 * @author ecasjim
 */
public class HardwareItemCreationIT {

    private Rule hardwareItemCreation;
    private final static Network NETWORK = new _1Network();
    private final static String ruleName = "BasicMoCreation";
    private final static String MO_TYPE_HIERARCHY = "ManagedElement,SystemFunctions,HwInventory,HwItem";
    private final static RuleManager RULE_MANAGER = new RuleManager(new MoFactory(true));
    private final static int TOTAL_NODES = 3;
    private final static int TOTAL_MOS_PER_NODE = 4;
    private final static int SYSTEM_CREATED_HWITEM_PER_NODE = 1;

    @Test
    public final void verifyMosCreatedByFeature() throws InvalidChildException, GnmRequestException {
        addNodes(NETWORK);

        hardwareItemCreation = RULE_MANAGER.getRule(ruleName);
        final RuleConfig ruleConfig = new RuleConfig(MO_TYPE_HIERARCHY, TOTAL_MOS_PER_NODE);
        ruleConfig.setStartingValue(2);
        hardwareItemCreation.apply(NETWORK, ruleConfig);

        Assert.assertEquals(getHwItemMoCountFromNetwork(NETWORK), TOTAL_NODES * TOTAL_MOS_PER_NODE + SYSTEM_CREATED_HWITEM_PER_NODE * TOTAL_NODES);
    }

    private void addNodes(final Network network) {
        final List<NetworkElement> networkElements = new ArrayList<NetworkElement>();

        for (int i = 0; i < TOTAL_NODES; i++) {
            networkElements.add(new NetworkElement("CORE SGSN 15B-WPP-V5", "CORE01SGSN00001"));
        }

        network.addNetworkElements(networkElements);
    }

    private int getHwItemMoCountFromNetwork(final Network network) throws InvalidChildException {
        final List<NetworkElement> networkElements = network.getNetworkElements();
        int totalHwItemMos = 0;

        for (final NetworkElement networkElement : networkElements) {
            totalHwItemMos += networkElement.getMosFromNe("ManagedElement,SystemFunctions,HwInventory,HwItem").size();
        }

        return totalHwItemMos;
    }
}
