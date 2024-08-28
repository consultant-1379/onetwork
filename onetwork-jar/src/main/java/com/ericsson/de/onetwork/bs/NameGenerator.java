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

package com.ericsson.de.onetwork.bs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages naming of network elements.
 *
 * @author ecasjim
 */
public class NameGenerator {
    private final static Logger logger = LoggerFactory.getLogger(NameGenerator.class.getName());

    /** Represents a sub network number. */
    private Integer currentSubnetNumber = 1;

    /** Represents a network element number. */
    private Integer currentNodeNumber = 1;

    /** Represents a network element prefix. */
    private String nodeNamePrefix = "";

    /** Represents a network element postfix. */
    private String nodeNamePostfix = "";

    /** Represents the limit of network elements per NETSim simulation. */
    public static final int NODES_PER_SUBNET_NETSIM_LTE = 160;
    public static final int NODES_PER_SUBNET_NETSIM_CORE = 10;
    public static final int NODES_PER_SUBNET_NETSIM_OTHRES = 10;

    /** Represents general LTE node types. */
    private static final List<String> SIMPLE_LTE_NODE_TYPES = new ArrayList<String>(Arrays.asList("ERBS", "PRBS", "MSRBS", "MRBS"));

    /** Represents general LTE node types. */
    private static final List<String> SIMPLE_CORE_NODE_TYPES = new ArrayList<String>(Arrays.asList("SGSN", "MME"));

    /**
     * Returns the network element name by using the MIM name as input.
     *
     * @param mimName
     *            MIM name of NE to be named
     * @return NE name
     */
    public String getNextNeName(final String mimName) {
        if (nodeNamePrefix.isEmpty()) {
            determineNodeNameStringsByMimName(mimName);
        }

        final int numOfNodesPerSubNetwork = getNumOfNodesPerSubNetwork(nodeNamePrefix);

        if (currentNodeNumber > numOfNodesPerSubNetwork) {
            currentSubnetNumber++;
            currentNodeNumber = 1;
        }

        final String simPrefix = nodeNamePrefix + String.format("%02d", currentSubnetNumber);
        final String nodeName = simPrefix + nodeNamePostfix + String.format("%05d", currentNodeNumber++);
        if ((currentNodeNumber - 1) % numOfNodesPerSubNetwork == 1) {
            logger.debug("Creating NE name:{} ", nodeName);
        }
        return nodeName;
    }

    /**
     * Using the MIM name, the naming constructs of the nodes are determined.
     * [Not complete: Support for other networks will be added as required.]
     *
     * @param mimName
     *            MIM name.
     */
    private void determineNodeNameStringsByMimName(final String mimName) {

        if (isMimNodeTypeInNodeList(SIMPLE_LTE_NODE_TYPES, mimName)) {
            nodeNamePrefix = "LTE";
            nodeNamePostfix = extractNodePostFixFromMimName(SIMPLE_LTE_NODE_TYPES, mimName);
        }
        else if (isMimNodeTypeInNodeList(SIMPLE_CORE_NODE_TYPES, mimName)) {
            nodeNamePrefix = "CORE";
            nodeNamePostfix = extractNodePostFixFromMimName(SIMPLE_CORE_NODE_TYPES, mimName);
        }
        else if (mimName.contains("")) {
            nodeNamePrefix = "WRAN";
        }
        else if (mimName.contains("")) {
            nodeNamePrefix = "GRAN";
        }
    }

    private static boolean isMimNodeTypeInNodeList(final List<String> nodeTypes, final String mimName) {
        for (final String nodeType : nodeTypes) {
            if (mimName.contains(nodeType)) {
                return true;
            }
        }
        return false;
    }

    private static String extractNodePostFixFromMimName(final List<String> nodeTypes, final String mimName) {
        for (final String nodeType : nodeTypes) {
            if (mimName.contains(nodeType)) {
                return nodeType;
            }
        }
        return "GEN";
    }

    // TODO: Compile OneNetwork generic constants for all packages.
    // Also, if it is possible move all these constants to a configurable
    // properties file.
    /**
     * Returns number of nodes per subnetwork|simulation.
     *
     * @param networkType
     *            Queried network type. For example, LTE, CORE etc.
     * @return number of nodes per subnetwork|simulation
     */
    public static int getNumOfNodesPerSubNetwork(final String networkType) {
        if ("LTE".equalsIgnoreCase(networkType)) {
            return NODES_PER_SUBNET_NETSIM_LTE;
        } else if ("CORE".equalsIgnoreCase(networkType)) {
            return NODES_PER_SUBNET_NETSIM_CORE;
        } else {
            return NODES_PER_SUBNET_NETSIM_OTHRES;
        }
    }
}
