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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.ss.util.InvalidMimVersionFormatException;
import com.ericsson.de.onetwork.ss.util.NetsimUtility;

/**
 * Represents the NETSim tool in terms of simulation building functionalities.
 * <p> Main purpose of this class is to build the sims.
 *
 * @author qfatonu
 */
public class NetsimSimulator implements Simulator {

    private final static Logger logger = LoggerFactory.getLogger(NetsimSimulator.class);

    @Override
    public void simulateNetwork(final Network network) throws SimulationBuildFailedException {

        try {
            final List<Sim> sims = NetsimUtility.allocateSimulations(network);

            populateMoScripts(sims);

            populateMmlScripts(sims);

            buildSims(sims);

        } catch (final IOException | InvalidMimVersionFormatException e) {
            throw new SimulationBuildFailedException(e.fillInStackTrace() + ", " + e.getMessage());
        }
    }

    private void populateMoScripts(final List<Sim> sims) {

        final SimMoDataGenerator simMoDataGen = new SimMoDataGenerator();
        for (final Sim sim : sims) {
            simMoDataGen.loadMoScriptsOnto(sim);
        }

        final Sim sim = sims.get(0);
        for (final Map.Entry<String, String> entry : sim.getNetworkElementToMoScriptMap().entrySet()) {
            logger.debug("\n{}={}", entry.getKey(), entry.getValue());
        }
    }

    private void populateMmlScripts(final List<Sim> sims) throws InvalidMimVersionFormatException {

        final SimMmlDataGenerator simMmlDataGen = new SimMmlDataGenerator();
        int ipOffset = 0; // allows setting next free ip address in NETSim
        for (final Sim sim : sims) {
            simMmlDataGen.loadMmlCmdsOntoSim(sim, ipOffset);
            ipOffset += sim.getNetworkElements().size();
        }
    }

    private void buildSims(final List<Sim> sims) throws IOException, SimulationBuildFailedException {
        final SimBuildManager sbm = new SimBuildManager();
        sbm.setSims(sims);
        logger.debug("Start of NETSim scripts creation locally!");
        sbm.createNetsimScripts();
        logger.debug("End of NETSim scripts creation locally!");

        logger.debug("Start of building sims on NETSim!");
        sbm.buildSimsInNetsim();
    }
}
