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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.de.onetwork.dms.generics.NetworkElement;

/**
 * Represents the NETSim simulation object.
 *
 * @author qfatonu
 */
public class Sim {

    /** Holds network elements */
    List<NetworkElement> networkElements;

    /** Name of the simulation */
    String name;

    /** Holds MML commands */
    final StringBuilder mmlCommands;

    // TODO: Replace this with SQLLite built in db after vertical slice
    // implementation.
    /** Holds MO scripts as network element key */
    final Map<String, String> networkElementToMoScript;

    /**
     * Constructs a new simulation object using initial network elements.
     *
     * @param networkElements
     *            the network elements in which contains MO, MIM data
     */
    public Sim(final List<NetworkElement> networkElements) {
        super();
        this.networkElements = networkElements;
        mmlCommands = new StringBuilder();
        networkElementToMoScript = new HashMap<String, String>();
    }

    /**
     * Returns network elements of the simulation.
     *
     * @return a list of network elements
     */
    public List<NetworkElement> getNetworkElements() {
        return networkElements;
    }

    /**
     * Appends MML commands. The collection of MML commands are used to create
     * simulation.
     *
     * @param command
     *            the NETSim MML command
     */
    public void appendMMlCmd(final String command) {
        mmlCommands.append(command);
    }

    /**
     * Returns a map of MO scripts where the NE names are a key and MO scripts
     * are the value.
     *
     * @return the networkElementToMoScript map contains MO script per NE
     */
    public Map<String, String> getNetworkElementToMoScriptMap() {
        return networkElementToMoScript;
    }

    /**
     * Returns the name of the simulation.
     *
     * @return the name of the simulation
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the simulation.
     *
     * @param name
     *            the name of the simulation
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns MML commands of this simulation in order to create this sim.
     *
     * @return a bulk of MML command
     */
    public String getMmlCmd() {
        return mmlCommands.toString();
    }

}
