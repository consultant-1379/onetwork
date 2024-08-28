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

package com.ericsson.de.onetwork.cs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.cs.exceptions.NexusUploadException;
import com.ericsson.de.onetwork.cs.nodeintro.FtpFileHandler;
import com.ericsson.de.onetwork.util.OnetworkSshClient;

/**
 * The class encapsulates all operations associated with the Nexus server.
 *
 * @author ecasjim
 */
public class OnetworkServiceUtility {

    private final static Logger logger = LoggerFactory.getLogger(OnetworkServiceUtility.class);
    private final static String NEXUS_SERVER = "https://arm901-eiffel004.athtem.eei.ericsson.se:8443/";
    private final static String _1NETWORK_NEXUS_FOLDER = "nexus/content/repositories/simnet/com/ericsson/1Network/";
    private final static String _1NETWORK_NEXUS_USERNAME = "simnet";
    private final static String _1NETWORK_NEXUS_PASSWORD = "simnet01";
    private final static String NETSIM_SERVER = "netsimv006-04.athtem.eei.ericsson.se";
    private final static String _1NETWORK_NETSIM_DIRECTORY = "/netsim/1Network/";
    private final static String SIMS_PROJECT_CODE = "ERIConetworksims_CXP9038429-";
    private static final int SUCCESSFUL = 0;

    /**
     * Used to upload zipped simulations from local machine to the Nexus server.
     *
     * @param path
     *            path to files for upload
     * @param filename
     *            name of file for upload
     * @throws Exception
     *             throw when there is an issue uploading file
     */
    public static boolean uploadZip(final String path, final String filename) throws NexusUploadException {
        String commandOutput = "", line = "";
        final Path filePath = Paths.get(path);

        logger.info("Attempting to upload {} to nexus server.", filename);

        final String uploadCurlCommand = "curl -k --upload-file " + filePath + File.separator + filename
                + " -u " + _1NETWORK_NEXUS_USERNAME + ":" + _1NETWORK_NEXUS_PASSWORD + " -v " +
                NEXUS_SERVER + _1NETWORK_NEXUS_FOLDER + filename;

        logger.debug("Running command: {}", uploadCurlCommand);

        try {
            final Process proc = Runtime.getRuntime().exec(uploadCurlCommand);

            // Read the output
            final BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            while ((line = reader.readLine()) != null) {
                logger.debug(line);
                commandOutput += line;
            }
        } catch (final IOException e) {
            throw new NexusUploadException("Error reading responce from nexus upload.");
        }

        processOutput(commandOutput);
        logger.info("Simulations successfully uploaded to {}", NEXUS_SERVER + _1NETWORK_NEXUS_FOLDER + filename);

        return true;
    }

    /**
     * Used to upload zipped simulations from remote netsim server to the Nexus
     * server.
     *
     * @param filename
     *            the name of the zip file
     * @throws NexusUploadException
     *             thrown when upload to nexus fails
     */
    public static void uploadZipFromNetsimToNexus(final String filename)
            throws NexusUploadException {
        int exitCode = 0;
        final OnetworkSshClient sshClient = new OnetworkSshClient();
        final String cmdGetSimsFromNetsim =
                "scp netsim@" + NETSIM_SERVER + ":" + _1NETWORK_NETSIM_DIRECTORY + filename + " . ";

        final String uploadCommand =
                "curl --noproxy '*' -k --upload-file "
                        + filename
                        + " -u " + _1NETWORK_NEXUS_USERNAME
                        + ":" + _1NETWORK_NEXUS_PASSWORD
                        + " -v " + NEXUS_SERVER + _1NETWORK_NEXUS_FOLDER + filename;

        try {
            logger.debug("Running command: {}", cmdGetSimsFromNetsim);
            exitCode = sshClient.executeCommand(cmdGetSimsFromNetsim);
            logger.debug("Exit code:{}, for running command: {}", exitCode, cmdGetSimsFromNetsim);

            logger.debug("Running command: {}", uploadCommand);
            exitCode = sshClient.executeCommand(uploadCommand);
            logger.debug("Exit code:{}, for running command: {}", exitCode, uploadCommand);

        } catch (final IOException e) {
            logger.debug("ERROR with sshClient: " + e.getMessage());
        } finally {
            sshClient.close();
        }

        processExitCode(filename, exitCode);
    }

    private static void processExitCode(final String filename, final int exitCode) throws NexusUploadException {
        if (exitCode == SUCCESSFUL) {
            logger.info("Simulations successfully uploaded to {}", NEXUS_SERVER + _1NETWORK_NEXUS_FOLDER + filename);
        } else {
            logger.info("Unable to upload simulations to Nexus.");
            throw new NexusUploadException("Unable to upload simulations to Nexus.");
        }
    }

    private static boolean processOutput(final String commandOutput) throws NexusUploadException {

        if (commandOutput.contains("Authentication problem") || commandOutput.contains("401 Unauthorized")) {
            logger.error("Authentication issue with Nexus.");
            throw new NexusUploadException("Authentication issue with Nexus.");
        } else if (commandOutput.contains("curl: Can't open")) {
            logger.error("Could not find simulation to upload to Nexus.");
            throw new NexusUploadException("Could not find simulation to upload to Nexus.");
        } else if (commandOutput.contains("error")) {
            logger.error("Error uploading simulation zip to Nexus.");
            throw new NexusUploadException("Error uploading simulation zip to Nexus.");
        }

        return true;
    }

    /**
     * Used to query the pom.xml file of the project for the version.
     *
     * @return version of the project
     */
    public static String extractVersionFromPom() {
        final String pomfile = "pom.xml";
        String version = "";
        Model model = null;
        FileReader reader = null;

        final MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            reader = new FileReader(pomfile);
            model = mavenreader.read(reader);
            version = model.getParent().getVersion();
        } catch (final Exception ex) {
            logger.error("Could not extract version from pom.xml");
            return "NoVersion";
        }

        logger.debug("Extracted version {} from pom.xml", version);
        return version;
    }

    /**
     * Zips all simulations at predefined location on 1Netowrk netsim server.
     *
     * @throws IOException
     *             thrown when issue connecting to netsim server
     */
    public static String zipAllsimulations() throws IOException {
        final OnetworkSshClient sshClient = new OnetworkSshClient();
        final String revision = extractVersionFromPom();
        final String ZIP_FILE_NAME = SIMS_PROJECT_CODE + revision + ".zip";

        try {
            final String zip = "zip -rv ";
            final String cmdToZipSimulations = "cd /netsim/netsimdir/; " + zip + _1NETWORK_NETSIM_DIRECTORY + ZIP_FILE_NAME
                    + " $(ls *.zip | egrep -i 'CORE|LTE' | egrep '[GEN]*zip')";
            sshClient.executeCommand(cmdToZipSimulations);
        } finally {
            sshClient.close();
        }

        return ZIP_FILE_NAME;
    }

    /**
     * Cleans netsim server before a build takes place.
     *
     * @throws IOException
     *             thrown when issue connecting to netsim server
     */
    public static void cleanSimulationsAndZipFiles() throws IOException {
        final OnetworkSshClient sshClient = new OnetworkSshClient();
        try {
            logger.debug("Start to clean the simulation directory..");
            // remove compressed files
            final String cmdToRemoveExistingOnenetworkZip =
                    "cd " + _1NETWORK_NETSIM_DIRECTORY + ";" + " rm -f *.zip *.gz; rm /netsim/netsimdir/*.zip";
            sshClient.executeCommand(cmdToRemoveExistingOnenetworkZip);
            logger.debug("Finished cleaning the simulation directory.");
        } finally {
            sshClient.close();
        }
    }

    public static void downloadSimulationToServer(final String simulationName) throws IOException {

        final OnetworkSshClient sshClient = new OnetworkSshClient();
        final String ftpServer = FtpFileHandler.getFtpServer();
        final FtpFileHandler ftpHandler = new FtpFileHandler();
        final String cppDirectoryPath = ftpHandler.getLatestCppDirectoryPath(FtpFileHandler.getParentDir());
        try {
            logger.debug("Downloading Simulation to Netsim Server..");
            final String cmdToCdNetsimDir = "cd /netsim/netsimdir/";
            final String cmdToDownloadSimulationToServer = cmdToCdNetsimDir + "; wget " + "ftp://"
                    + ftpServer + cppDirectoryPath + simulationName;
            final int result = sshClient.executeCommand(cmdToDownloadSimulationToServer);

            if (result != 0) {
                throw new IOException("Failed downloading simulation " + "\"" + simulationName + "\"");
            }
            logger.debug("Finished downloading simulation to Netsim Server..");
        } finally {
            sshClient.close();
        }
    }
}
