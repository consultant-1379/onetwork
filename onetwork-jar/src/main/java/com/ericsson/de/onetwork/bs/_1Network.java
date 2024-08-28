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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ericsson.de.onetwork.dms.generics.NetworkElement;

/**
 * Concrete implementation of <code>Network</code> object.
 *
 * @author ecasjim
 */
public class _1Network implements Network {

    /** List of network elements */
    private final List<NetworkElement> networkElements = new ArrayList<NetworkElement>();

    /** List of network cell names */
    private final List<String> networkCellNames = new ArrayList<String>();

    /*
     * @see com.ericsson.de.onetwork.bs.Network#getNetworkElements()
     */
    @Override
    public List<NetworkElement> getNetworkElements() {
        return networkElements;
    }

    /*
     * @see
     * com.ericsson.de.onetwork.bs.Network#addNetworkElements(java.util.List)
     */
    @Override
    public void addNetworkElements(final List<NetworkElement> networkElements) {
        this.networkElements.addAll(networkElements);
    }

    /* (non-Javadoc)
     * @see com.ericsson.de.onetwork.bs.Network#setNetworkCellList(java.util.List)
     */
    @Override
    public void addNetworkCellNames(final List<String> networkCellNames) {
        this.networkCellNames.addAll(networkCellNames);
    }

    /* (non-Javadoc)
     * @see com.ericsson.de.onetwork.bs.Network#getNetworkCellNames()
     */
    @Override
    public List<String> getNetworkCellNames() {
        return networkCellNames;
    }

    /* (non-Javadoc)
     * @see com.ericsson.de.onetwork.bs.Network#getNetworkNodeNames()
     */
    @Override
    public Set<String> getNetworkNodeNames() {
        final Set<String> nodeNamesSet = new LinkedHashSet<String>();

        for (Integer i = 0; i < networkCellNames.size(); i++) {
            nodeNamesSet.add(networkCellNames.get(i).split("-")[0]);
        }

        return nodeNamesSet;
    }

}
