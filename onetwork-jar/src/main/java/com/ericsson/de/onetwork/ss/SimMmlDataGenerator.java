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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.ss.util.InvalidMimVersionFormatException;
import com.ericsson.de.onetwork.ss.util.NetsimUtility;
import com.ericsson.de.onetwork.ss.util.SimServiceStringBuilder;

/**
 * Generates MML commands according to NETSim tool standard.
 * <p>
 * MML commands are passed to NETSim simulator in order to create a simulation.
 */
public class SimMmlDataGenerator {

    private static final String ONENETWORK_NETSIM_SCRIPTS_FOLDER = "/tmp/prod/onenetwork/";

    private final static Logger logger = LoggerFactory.getLogger(SimMmlDataGenerator.class);

    /**
     * Holds reference to simulation object in order to load the MML commands
     */
    private Sim sim;

    /**
     * TODO: Re-factoring: Try to move private methods into the
     * NetsimUtility class.
     * Processes the simulation object to load MML commands onto it.
     *
     * @param sim
     *            the simulation where MML commands load to
     * @param ipOffset
     *            the offset which NEs IP set to. For example 1 to 160 for the
     *            first simulation, and 161 to 320 for the next simulation.
     * @throws InvalidMimVersionFormatException
     *             if provided MIM version doesn't follow the required naming
     *             convention format
     */
    public void loadMmlCmdsOntoSim(final Sim sim, final int ipOffset) throws InvalidMimVersionFormatException {

        this.sim = sim;

        final SimServiceStringBuilder sb = new SimServiceStringBuilder();

        sb.append(getCreateSimMml());

        sb.append(getCreateNesMml(ipOffset));

        sb.append(NetsimUtility.createStartNetworkMml());

        sb.appendWithoutNewLine(getKertayleForAllNetworkElementsAsMml());

        sb.appendWithoutNewLine(NetsimUtility.createSaveAndCompressSimMml());

        sim.appendMMlCmd(sb.toString().trim());

        logger.debug("\nsimMmlCmd=\n{}", sim.getMmlCmd());
    }

    private String getCreateSimMml() {
        final SimServiceStringBuilder mmlCommand = new SimServiceStringBuilder();

        final String simName = sim.getName();
        logger.debug("simName={}", simName);

        final String createSimMmml = NetsimUtility.createSimMml(simName);
        mmlCommand.appendWithoutNewLine(createSimMmml);
        logger.debug("\ncreateSimMml=\n{}", createSimMmml);

        return mmlCommand.toString();
    }

    private String getCreateNesMml(final int offset) throws InvalidMimVersionFormatException {
        final SimServiceStringBuilder mmlCommand = new SimServiceStringBuilder();

        final String simName = sim.getName();

        final NetworkElement ne = sim.getNetworkElements().get(0);
        final String neName = ne.getName();
        logger.debug("neName={}", neName);

        final String mimVersion = ne.getMimVersion();
        logger.debug("mimVersion={}", ne.getMimVersion());

        final String neType = NetsimUtility.getNetworkElementType(mimVersion);
        final String port = NetsimUtility.getNetworkElementPort(neType);

        final String nePrefixName = getNetworkElementPrefixName(neName);
        logger.debug("prefixName={}", nePrefixName);

        final int numOfNes = sim.getNetworkElements().size();

        final String neVersion = NetsimUtility.getNetworkElementVersion(mimVersion);
        logger.debug("neVersion={}", neVersion);

        final String createNesMml = NetsimUtility.createNesMml(simName, port, nePrefixName, neType, numOfNes, neVersion, offset);
        logger.debug("\ncreateNesMml=\n{}", createNesMml);
        mmlCommand.appendWithoutNewLine(createNesMml);

        return mmlCommand.toString();
    }

    // TODO: Use single location for SS constants.
    private String getKertayleForAllNetworkElementsAsMml() {
        final SimServiceStringBuilder mmlCommand = new SimServiceStringBuilder();

        for (final String neName : sim.getNetworkElementToMoScriptMap().keySet()) {
            final String networkElementName = neName;
            final String moScriptFileName = ONENETWORK_NETSIM_SCRIPTS_FOLDER + neName + ".mo";
            mmlCommand.appendWithoutNewLine(NetsimUtility.createKertayleMml(networkElementName, moScriptFileName));
        }
        // Introduce an new line for nice looking MML file
        if (!mmlCommand.toString().isEmpty()) {
            mmlCommand.append("");
        }
        logger.debug("\ngetKertayleForAllNetworkElementsAsMml=\n{}", mmlCommand.toString());

        return mmlCommand.toString();
    }

    private String getNetworkElementPrefixName(final String neName) {
        for (final String neType : NetsimUtility.networkTypes.keySet()) {
            if (neName.contains(neType)) {
                return neName.split(neType)[0];
            }
        }
        return "";
    }
}
