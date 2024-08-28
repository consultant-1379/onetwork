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

package com.ericsson.de.onetwork.dms.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.cs.nodeintro.FtpFileHandler;
import com.ericsson.de.onetwork.cs.nodeintro.LatestErbsMimsHandler;
import com.ericsson.de.onetwork.cs.util.OnetworkServiceUtility;
import com.ericsson.de.onetwork.ss.util.FileUtils;
import com.ericsson.de.onetwork.ss.util.InvalidMimVersionFormatException;
import com.ericsson.de.onetwork.ss.util.NetsimUtility;
import com.ericsson.de.onetwork.util.OnetworkNetsimHandler;
import com.ericsson.de.onetwork.util.OnetworkSshClient;

/**
 * Provides utility methods in order to ease MIM file management.
 *
 * @author qfatonu
 */
public class MimFileUtility {

    private final static Logger logger = LoggerFactory.getLogger(MimFileUtility.class);

    private static int SUCCESSFULL = 0;

    public static final String MIM_FILE_DIR = "/dms/mims/";

    public static final String MIM_FILE_RESOURCE_DIR = MimFileUtility.class.getResource(MIM_FILE_DIR).getPath();

    /**
     * Returns true if MIM file exist on MIM_FILE_RESOURCE_DIR.
     *
     * @param neVersion
     *            network element version
     * @return true if MIM file exist on MIM_FILE_RESOURCE_DIR.
     */
    public static boolean isMimFileAvailable(final String neVersion) {
        return null != getMimFileNameLocally(neVersion);
    }

    /**
     * Gets MIM file name if exist on MIM_FILE_RESOURCE_DIR, otherwise null.
     *
     * @param neVersion
     *            network element version. For example "LTE ERBS F1101"
     * @return MIM file name if exist on MIM_FILE_RESOURCE_DIR, otherwise null.
     *         For example "Netsim_ERBS_NODE_MODEL_vF_1_101.xml"
     */
    public static String getMimFileNameLocally(final String neVersion) {
        try {
            final InputStream in = MimFileUtility.class.getResourceAsStream(MIM_FILE_DIR);

            if (in != null) {
                final List<String> files = IOUtils.readLines(in, Charsets.UTF_8);
                final String simplifiedNeVersion = neVersion.replaceAll("[-]?lim", "");
                for (final String file : files) {
                    final String simplifiedFileName = file.replaceAll("_" + "", "");
                    logger.debug("simplifiedFileName:{} vs simplifiedNeVersion:{}", simplifiedFileName, simplifiedNeVersion);

                    if (simplifiedFileName.contains(simplifiedNeVersion)) {
                        logger.debug("Found mimFileName:{}", file);
                        return file;
                    }
                }
            }
        } catch (final IOException e) {
            logger.debug("Ignoreable error: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Copies MIM file from remote NETSim server to local
     * MMIM_FILE_RESOURCE_DIR.
     *
     * @param simName
     *            simulation name
     * @return true if copies successfully
     * @throws IOException
     *             if fails to copy file
     */
    public static boolean copyMimFile(final String simName) throws IOException {

        final boolean result;

        final String simNamePlain = simName.substring(0, simName.indexOf("."));
        final String netsimNEsDir = "/tmp/nes/";
        final String netsimMimFileDir = netsimNEsDir + simNamePlain + "/netsimuserinstallation/mim_files/";
        String mimFileName = "";

        final OnetworkSshClient sshClient = new OnetworkSshClient();
        try {
            logger.debug("Start to copy file by unzipping the simulation named {}", simName);

            final String cmdToUnzipSim = "unzip -u /netsim/netsimdir/" + simName + " -d " + netsimNEsDir;
            final int exitCode = sshClient.executeCommand(cmdToUnzipSim);
            if (exitCode != SUCCESSFULL) {
                logger.debug("Unable to unzip the sim named {}. Make sure the sim is located under /netsim/netsimdir");
                return false;
            }

            final String cmdToGetMimFileName = "find " + netsimMimFileDir + " -name '*.xml' -printf '%f\n'";
            mimFileName = sshClient.executeCommandv2(cmdToGetMimFileName).trim();
            logger.debug("mimFileName:{}", mimFileName);

            final String remoteFilePath = netsimMimFileDir + mimFileName;
            final String localDestFolderPath = FileUtils.getPlatformIndependentPathString(MIM_FILE_RESOURCE_DIR);

            logger.debug("Copying sim from {} to {}", remoteFilePath, localDestFolderPath);

            result = sshClient.copyFrom(remoteFilePath, localDestFolderPath);

            logger.debug("Finished copying mimFile named {}...", mimFileName);

        } finally {
            sshClient.close();
        }

        return result;
    }

    /**
     * Gets MIM file name from remote NETSim server to MIM_FILE_RESOURCE_DIR.
     * Also upgrade NETSim based on MIM file compatibility with exiting NETSim
     * server.
     *
     * @param mimVersion
     *            mim version. For example "LTE ERBS F1101"
     * @return name of MIM file name.
     * @throws InvalidMimVersionFormatException
     *             if MIM version doesn't follow naming convention
     * @throws IOException
     *             if get operation fails due to server communication
     */
    public static String getMimFileName(final String mimVersion) throws InvalidMimVersionFormatException, IOException {

        final String neVersion = NetsimUtility.getNetworkElementVersion(mimVersion);
        logger.debug("To be searched neVersion:{}", neVersion);

        final String mimFileName = getMimFileNameLocally(neVersion);
        logger.debug("Identified mimFileName:{}", mimFileName);

        if (mimFileName != null) {
            logger.debug("MimFile named {} has alread exist. Locally returned.", mimFileName);
            return mimFileName;
        }

        // If mim file doesn't exist
        // --> TODO Download it onto NETSim /netsim/netsimdir (Euphraim)

        logger.debug("Connecting ftp server in order to get simulation name via mimversion named {}", mimVersion);
        final LatestErbsMimsHandler mimHandler =
                new LatestErbsMimsHandler(FtpFileHandler.FTP_SERVER, FtpFileHandler.PORT, FtpFileHandler.PARENT_DIR, 50);
        final String simName = mimHandler.getSimulationName(mimVersion);
        logger.debug("About to download simulation named {} onto netsim server", simName);
        OnetworkServiceUtility.downloadSimulationToServer(simName);

        // --> TODO Open simulation file
        logger.debug("About to open simulation named on netsim server", simName);
        if (OnetworkNetsimHandler.openSimulation(simName)) {
            logger.debug("About to copy mim file by extracting simulation name");
            MimFileUtility.copyMimFile(simName);
        }

        return getMimFileNameLocally(neVersion);
    }

}
