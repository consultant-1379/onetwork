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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.gnm.Gnm;

/**
 * Determines which MIM to use by consolidating information from
 * <code>Gnm</code> and <code>NetworkRequest</code>.
 *
 * @author ecasjim
 */
public class MimSelector {
    private final static Logger logger = LoggerFactory
            .getLogger(ProceduralEngine.class.getName());

    /** Stores the number of nodes required for each MIM type. */
    private final Map<String, Integer> requiredNodesPerMim = new HashMap<String, Integer>();

    /**
     * Stores the number of nodes currently implemented in the network for each
     * MIM type.
     */
    private Map<String, Integer> currentNodesPerMim;

    /** Stores the MIM usage percentages taken from the GNM. */
    private final Map<String, Double> mimMap;

    /** The GNM which defines the network being built. */
    private final Gnm gnm;

    /**
     * Constructor which takes a <code>Gnm</code> object and the number of
     * network elements in the network.
     * It then populates the requiredNodesPerMim map with the MIM percentages
     * taken from the <code>Gnm</code>.
     *
     * @param gnm
     *            gnm object.
     * @param numberOfNEs
     *            number of network elements in the network.
     */
    public MimSelector(final Gnm gnm, final int numberOfNEs) {
        this.gnm = gnm;
        mimMap = this.gnm.getMimUsage();

        if (requiredNodesPerMim.isEmpty()) {
            populateNumberOfNodesPerMimMap(mimMap, numberOfNEs);
        }
    }

    /**
     * Returns the required MIM type to use at this point in the network as
     * defined by the <code>GNM</code>.
     *
     * @return the MIM type to use for current network element.
     */
    public String getNextMimType() {
        String mimName = "";

        for (final Map.Entry<String, Integer> entry : requiredNodesPerMim.entrySet()) {
            mimName = entry.getKey();

            if (isNeTotalForThisMimTypeNotReached(mimName)) {
                updateCurrentNodesPerMimMap(mimName);
                break;
            }
        }
        logger.debug("Mim selected: " + mimName);
        return mimName;
    }

    private boolean isNeTotalForThisMimTypeNotReached(final String mimName) {
        return currentNodesPerMim.get(mimName) < requiredNodesPerMim.get(mimName);
    }

    /**
     * Used to keep track of how many nodes are using each individual MIM type.
     *
     * @param mimName
     *            MIM type in question.
     */
    private void updateCurrentNodesPerMimMap(final String mimName) {
        int currentNumberOfNEs = currentNodesPerMim.get(mimName);
        currentNodesPerMim.put(mimName, ++currentNumberOfNEs);
    }

    /**
     * Determines how many nodes of each MIM type are required to satisfy
     * <code>GNM</code> requirements.
     *
     * @param mimMap
     *            stores the MIM usage percentages taken from the GNM
     * @param numberOfNEs
     *            number of network elements in the network.
     */
    private void populateNumberOfNodesPerMimMap(final Map<String, Double> mimMap, final int numberOfNEs) {
        for (final Map.Entry<String, Double> entry : mimMap.entrySet()) {
            final double networkElementsPerMim = numberOfNEs * (entry.getValue() / 100);
            requiredNodesPerMim.put(entry.getKey(), roundUpToNearestInt(networkElementsPerMim));
        }

        initialiseCurrentNodesPerMimMapWithKeys();
    }

    /**
     * Initialises the Map with MIM type names as keys and values set to zero.
     * As MIM types are assigned to NEs, the values for appropriate MIMs are
     * incremented.
     */
    private void initialiseCurrentNodesPerMimMapWithKeys() {
        currentNodesPerMim = new HashMap<String, Integer>(requiredNodesPerMim);

        for (final Map.Entry<String, Integer> entry : currentNodesPerMim.entrySet()) {
            currentNodesPerMim.put(entry.getKey(), 0);
        }
    }

    private static Integer roundUpToNearestInt(final Double value) {
        return (int) Math.ceil(value);
    }
}
