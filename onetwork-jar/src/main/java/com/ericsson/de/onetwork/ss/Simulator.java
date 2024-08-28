/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.ss;

import com.ericsson.de.onetwork.bs.Network;

/**
 * The entry point interface of the Simulation Service of 1Network which allows
 * the end users to simulate the populated network data.
 *
 * @author qfatonu
 */
public interface Simulator {

    /**
     * Simulates the network based on predefined network distribution.
     * <p>
     * For example, 30K sized network data can be distributed as 160 nodes
     * simulation for the System Test environment.
     *
     * @param network
     *            the managed objects populated network data
     * @throws SimulationBuildFailedException
     *             if simulation build operation fails
     */
    void simulateNetwork(Network network) throws SimulationBuildFailedException;

}
