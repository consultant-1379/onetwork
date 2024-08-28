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

import java.util.List;
import java.util.Set;

import com.ericsson.de.onetwork.dms.generics.NetworkElement;

/**
 * This interface represents a Network object.
 *
 * @author ecasjim
 */
public interface Network {

    /**
     * Returns network elements of this network.
     *
     * @return list of network elements
     */
    List<NetworkElement> getNetworkElements();

    /**
     * Add network elements to a network object.
     *
     * @param networkElements
     *            list of network elements.
     */
    void addNetworkElements(List<NetworkElement> networkElements);

    /**
     * Set a List containing the names of cells within the network.
     *
     * @param networkCellNames
     *            cell names list of string
     */
    void addNetworkCellNames(List<String> networkCellNames);

    /**
     * Get a List containing the names of cells within the network.
     */
    List<String> getNetworkCellNames();

    /**
     * Returns a set of node names withing network.
     *
     * @return set of node names
     */
    Set<String> getNetworkNodeNames();
}
