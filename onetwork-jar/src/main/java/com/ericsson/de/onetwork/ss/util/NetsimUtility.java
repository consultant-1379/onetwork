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

package com.ericsson.de.onetwork.ss.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.ss.Sim;

/**
 * A utility class which provides common NETSim specific methods.
 * For example; port creation, simulation creation, nodes creation etc.
 */
public class NetsimUtility {

    /** Logger for parsing events . */
    private final static Logger logger = LoggerFactory.getLogger(NetsimUtility.class);

    /** Holds network type information for each NE */
    public final static Map<String, String> networkTypes;
    static {
        networkTypes = new HashMap<String, String>();
        networkTypes.put("ERBS", "LTE");
        networkTypes.put("SGSN", "WPP");
    }

    /** Holds port information for each NE */
    public final static Map<String, String> portTypes;
    static {
        portTypes = new HashMap<String, String>();
        portTypes.put("ERBS", "IIOP_PROT");
        portTypes.put("SGSN", "NETCONF_PROT_SSH");
    }

    /** Represents the limit of network elements per NETSim simulation. */
    public static final int NODES_PER_SUBNET_NETSIM_LTE = 160;
    public static final int NODES_PER_SUBNET_NETSIM_CORE = 10;
    public static final int NODES_PER_SUBNET_NETSIM_OTHRES = 10;

    /**
     * Creates MML commands in order to create an empty(without any NEs)
     * simulation.
     *
     * @param simName
     *            the name of the simulation
     * @return a list of MML commands
     */
    public static String createSimMml(final String simName) {
        final SimServiceStringBuilder mml = new SimServiceStringBuilder();

        mml.append(".deletesimulation " + simName + " force");
        mml.append(".new simulation " + simName);

        return mml.toString();
    }

    /**
     * Creates MML commands in order to save and compress the simulation.
     *
     * @return a list of MML commands
     */
    public static String createSaveAndCompressSimMml() {
        final SimServiceStringBuilder mml = new SimServiceStringBuilder();

        mml.append(".select network");
        mml.append(".stop -parallel");
        mml.append(".saveandcompress force nopmdata");

        return mml.toString();
    }

    /**
     * Creates MML commands in order to create NEs of the simulation.
     *
     * @param simName
     *            the name of the simulation. For example
     *            LTEF1101x12-GEN-LTE01.zip.
     * @param port
     *            the name of port where the simulation connects to. For example
     *            IIOP_PROT.
     * @param prefixName
     *            an optional prefix name for the NE names. For example LTE01.
     * @param neType
     *            type of NE such as ERBS, PRBS
     * @param numOfNes
     *            the number of NEs to be created
     * @param neVersion
     *            the MIM version of an NE. For example F1101.
     * @param offset
     *            the initial offset
     * @return a list of MML commands
     */
    public static String createNesMml(final String simName, final String port, final String prefixName, final String neType, final int numOfNes,
            final String neVersion, final int offset) {

        // temporary currently set to 5 for LTE. For example LTE01ERBS00001
        final int numOfDigitsForNeCount = 5;

        final SimServiceStringBuilder mml = new SimServiceStringBuilder();

        mml.append(".open " + simName);
        mml.append(".createne checkport " + port);
        mml.append(".set preference positions " + numOfDigitsForNeCount);
        mml.append(".new simne -auto " + numOfNes + " " + prefixName + neType + " 01");
        mml.append(".set netype " + getNetworkTypeFromNetworkElement(neType) + " " + neType + " " + neVersion);
        mml.append(".set port " + port);
        mml.append(".createne subaddr " + offset + " subaddr no_value");
        mml.append(".set save");

        return mml.toString();
    }

    /**
     * TODO NOTE:Currently handles LTE nodes only. Once we have GNM and clear
     * structure for CORE nodes this will be updated. Allocates network elements
     * into sims based on predefined input.
     *
     * @param network
     *            the network object holds the network elements
     * @return a list of simulation
     * @throws InvalidMimVersionFormatException
     *             if provided MIM version doesn't follow the required naming
     *             convention format
     */
    public static List<Sim> allocateSimulations(final Network network) throws InvalidMimVersionFormatException {

        if (network.getNetworkElements().size() < 1) {
            return new ArrayList<Sim>();
        }

        final String networkType = getNetworkType(network.getNetworkElements().get(0).getMimVersion());
        logger.debug("networkType={}", networkType);

        final int numOfNodesPerSim = getNumOfNodesPerSim(networkType);
        logger.debug("numOfNodesPerSim={}", numOfNodesPerSim);

        final List<Sim> sims = new ArrayList<Sim>();

        final List<NetworkElement> nes = network.getNetworkElements();

        final int nesSize = nes.size();
        logger.debug("nesSize={}", nesSize);

        int simCount = 1;
        int fromIndex = 0;
        while (fromIndex < nesSize) {
            final int toIndex = fromIndex + numOfNodesPerSim > nesSize ? nesSize : fromIndex + numOfNodesPerSim;

            logger.debug("Sim-{}[{}-{}]", simCount, fromIndex, toIndex);

            final List<NetworkElement> simNes = nes.subList(fromIndex, toIndex);

            final Sim sim = new Sim(simNes);
            sim.setName(getSimName(simNes, simCount));
            sims.add(sim);

            logger.info("Created simulation: {}", sim.getName());

            fromIndex = toIndex;
            simCount++;
        }

        return sims;
    }

    // TODO: Compile OneNetwork generic constants for network types
    // Also move these numbers to be read from a config file
    private static int getNumOfNodesPerSim(final String networkType) {
        if ("LTE".equalsIgnoreCase(networkType)) {
            return NODES_PER_SUBNET_NETSIM_LTE;
        } else if ("CORE".equalsIgnoreCase(networkType)) {
            return NODES_PER_SUBNET_NETSIM_CORE;
        } else {
            return NODES_PER_SUBNET_NETSIM_OTHRES;
        }
    }

    private static String getSimName(final List<NetworkElement> simNes, final int simCount) throws InvalidMimVersionFormatException {

        final int numOfNes = simNes.size();
        final String mimVersion = simNes.get(0).getMimVersion();
        logger.debug("mimVersion={}", mimVersion);

        final String neVersion = getNetworkElementVersion(mimVersion);
        logger.debug("mimName={}", neVersion);

        final String networkType = getNetworkType(mimVersion);
        logger.debug("networkType={}", networkType);

        final String simName = String.format("%s%sx%d-GEN-%s%02d", networkType, neVersion, numOfNes, networkType, simCount);
        logger.debug("simName={}", simName);

        return simName;
    }

    /**
     * Returns NE version. For example F1101.
     *
     * @param mimVersion
     *            the long NE MIM version name. For example; "LTE ERBS F1101".
     * @return the NE version name. For example F1101.
     * @throws InvalidMimVersionFormatException
     */
    public static String getNetworkElementVersion(final String mimVersion) throws InvalidMimVersionFormatException {
        final String[] mimVersionComponents = getMimVersionComponentsArray(mimVersion);
        return mimVersionComponents[2];
    }

    /**
     * Returns network type of an NE. For example LTE.
     *
     * @param mimVersion
     *            the long NE version name. For example; "LTE ERBS F1101".
     * @return the network type. For example LTE.
     * @throws InvalidMimVersionFormatException
     */
    public static String getNetworkType(final String mimVersion) throws InvalidMimVersionFormatException {
        final String[] mimVersionComponents = getMimVersionComponentsArray(mimVersion);
        return mimVersionComponents[0];
    }

    /**
     * Returns network element type of an NE. For example ERBS.
     *
     * @param mimVersion
     *            the long NE version name. For example; "LTE ERBS F1101".
     * @return the network element type. For example ERBS.
     * @throws InvalidMimVersionFormatException
     */
    public static String getNetworkElementType(final String mimVersion) throws InvalidMimVersionFormatException {
        final String[] mimVersionComponents = getMimVersionComponentsArray(mimVersion);
        return mimVersionComponents[1];
    }

    private static String[] getMimVersionComponentsArray(final String mimVersion) throws InvalidMimVersionFormatException {
        final String mimVersionComponents[] = mimVersion.split(" ", 3);
        if (mimVersionComponents.length != 3) {
            final String msg =
                    "Check your mimVersion format. Example format "
                            + "<NwType><Space><NEType><Space><NEVersion>. For example \"LTE ERBS F1101\"";
            throw new InvalidMimVersionFormatException(msg);
        }
        return mimVersionComponents;
    }

    private static String getNetworkTypeFromNetworkElement(final String neType) {
        return networkTypes.get(neType);
    }

    /**
     * Returns required MML commands in order to push MO script into NE.
     *
     * @param networkElementName
     *            the name of the network element name
     * @param moScriptFileName
     *            the name of the MO script file
     * @return
     */
    public static String createKertayleMml(final String networkElementName, final String moScriptFileName) {
        final SimServiceStringBuilder sb = new SimServiceStringBuilder();

        sb.append(".selectregexp simne " + networkElementName);
        sb.append("kertayle:file=\"" + moScriptFileName + "\";");

        return sb.toString();
    }

    /**
     * Returns required MML commands in order to start network. As a default
     * action it also turn off mandatory required MO attributes input.
     *
     * @return a list of MML commands to start network
     */
    public static String createStartNetworkMml() {
        final SimServiceStringBuilder sb = new SimServiceStringBuilder();

        sb.append(".select network");
        sb.append(".start -parallel");
        sb.append("useattributecharacteristics:switch=\"off\";");

        return sb.toString();
    }

    /**
     * TODO: To be extended when new nodes support added.
     * Returns a port name for the NETSIM based on network element.
     *
     * @param neType
     *            the network element type. For example "ERBS" or "SGSN".
     * @return the name of port in order NE to be started
     */
    public static String getNetworkElementPort(final String neType) {
        return portTypes.get(neType);
    }
}
