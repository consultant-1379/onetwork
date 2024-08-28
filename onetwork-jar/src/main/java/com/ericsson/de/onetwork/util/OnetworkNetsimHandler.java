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

package com.ericsson.de.onetwork.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.ext.ns.Netsim;

/**
 * Provides remote NETSim commands specially for ONETWORK server and helpful
 * utilitu commands.
 *
 * @author qfatonu
 */
public class OnetworkNetsimHandler {

    private static final Logger logger = LoggerFactory.getLogger(OnetworkNetsimHandler.class);

    private static int SUCCESSFULL = 0;

    /**
     * Opens simulation remotely on NETSim server.
     *
     * @param simName
     *            simulation name
     * @return true if simulation opens successfully
     * @throws IOException
     *             if server communication fails
     */
    public static boolean openSimulation(final String simName) throws IOException {

        upgradeNetsimIfNecessary(simName);

        final int exitCode;

        final OnetworkSshClient sshClient = new OnetworkSshClient();
        try {
            logger.debug("Start to open the simulation named {}", simName);

            final String cmdToOpenSimulation = "echo .uncompressandopen " + simName + " force | /netsim/inst/netsim_pipe";
            final String cmdToSaveAndDisplayOutput = "| tee /tmp/cmdToOpenSimulation.txt; cat /tmp/cmdToOpenSimulation.txt";
            final String cmdToGetResult = cmdToOpenSimulation + cmdToSaveAndDisplayOutput + " | tail -1 | ( read status; [[ $status = \"OK\" ]] )";
            exitCode = sshClient.executeCommand(cmdToGetResult);

            logger.debug("Finished opening simulation named {}...", simName);

        } catch (final IOException ex) {
            throw new IOException(String.format("Unable to open simulation named:%s, error msg:%s ", simName, ex.getMessage()));

        } finally {
            sshClient.close();
        }

        return SUCCESSFULL == exitCode;
    }

    private static void upgradeNetsimIfNecessary(final String simName) throws IOException {
        // if sim name contains NETSim version information, upgrade NETSim if
        // necessary.
        if (simName.contains("_R")) {
            final String minimumRequriedNetsimVersion = getNetsimVersionFromSimName(simName);
            logger.debug("minimumRequriedNetsimVersion:{}", minimumRequriedNetsimVersion);

            final String currentNetsimVersion = getNetsimVersion();

            if (isNetsimVersionHigherThanCurrentNetsimVersion(minimumRequriedNetsimVersion, currentNetsimVersion)) {
                logger.debug("Netsim version is being upgraded into latest version...");
                Netsim.upgradeToLatestVersion();
            }
        }
    }

    /**
     * Returns NETSim version from simulation name.
     *
     * @param simName
     *            simulation name
     * @return NETSim version
     */
    public static String getNetsimVersionFromSimName(final String simName) {
        final String netsimVersion = simName.split("_")[1].replaceFirst(".zip", "").trim();
        logger.debug("getNetsimVersionFromSimName()-netsimVersion:{}", netsimVersion);
        return netsimVersion;
    }

    /**
     * Compares two NETSim version and returns true if first NETSim version is
     * lower than second one.
     *
     * @param minimumRequriedNetsimVersion
     *            first input to be compared to
     * @param currentNetsimVersion
     *            second input to be compared against
     * @return
     */
    public static boolean
            isNetsimVersionHigherThanCurrentNetsimVersion(final String minimumRequriedNetsimVersion, final String currentNetsimVersion) {

        // First check major release. For example 28 for R28G, 29 for R29A
        final int majorReleaseQuerried = Integer.valueOf(minimumRequriedNetsimVersion.substring(1, 3));
        final int majorReleaseExisting = Integer.valueOf(currentNetsimVersion.substring(1, 3));
        logger.debug("majorReleaseQuerried:{}, majorReleaseExisting:{}", majorReleaseQuerried, majorReleaseExisting);

        if (majorReleaseQuerried > majorReleaseExisting) {
            return true;
        } else if (majorReleaseQuerried < majorReleaseExisting) {
            return false;
        }

        // Then check minor release. For example G for R28G and H for R28H
        final String minorReleaseQuerried = minimumRequriedNetsimVersion.substring(3, 4);
        final String minorReleaseExisting = currentNetsimVersion.substring(3, 4);
        logger.debug("minorReleaseQuerried:{}, minorReleaseExisting:{}", minorReleaseQuerried, minorReleaseExisting);

        if (minorReleaseQuerried.compareToIgnoreCase(minorReleaseExisting) == 1) {
            return true;
        }

        return false;
    }

    /**
     * Returns current NETSim version.
     *
     * @return current NETSim version
     * @throws IOException
     *             if server communication fails
     */
    public static String getNetsimVersion() throws IOException {

        final OnetworkSshClient sshClient = new OnetworkSshClient();
        try {

            final String cmdToGetNetsimVersion = "ls -ltrh /netsim/inst | awk -F/ '{print $5}'";
            final String netsimVersion = sshClient.executeCommandv2(cmdToGetNetsimVersion).trim();
            logger.debug("Current netsimVersion:{}", netsimVersion);

            return netsimVersion;

        } catch (final IOException ex) {
            throw new IOException("Unable to get the netsim version: " + ex.getMessage());

        } finally {
            sshClient.close();
        }
    }
}
