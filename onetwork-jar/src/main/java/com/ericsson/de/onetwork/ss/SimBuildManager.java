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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.ss.util.FileUtils;
import com.ericsson.de.onetwork.ss.util.SshClient;
import com.ericsson.de.onetwork.util.ServerUtility;

/**
 * Manages the building of simulations.
 *
 * @author qfatonu
 */
public class SimBuildManager {

    private static final Logger logger = LoggerFactory.getLogger(SimBuildManager.class);

    // TODO: Move them to a properties file
    // Folder and server details for building onenetwork sims
    private static final String ONENETWORK_NETSIM_SCRIPTS_FOLDER = "/tmp/prod/onenetwork/";

    private static String ONENETWORK_SS_SCRIPTS_FOLDER;

    private static final String ONENETWORK_SIMULATION_BUILD_SCRIPT = "create_onenetwork_sims.sh";

    private static final String ONENETWORK_NETSIM_BUILD_SERVER = "netsimv006-04.athtem.eei.ericsson.se";
    private static final String ONENETWORK_NETSIM_BUILD_SERVER_USER = "netsim";
    private static final String ONENETWORK_NETSIM_BUILD_SERVER_PWD = "netsim";

    private static int SUCCESFULLY_EXECUTED = 0;

    /** Holds simulation list */
    private List<Sim> sims;

    /**
     * Builds sims in NETSsim.
     *
     * @param sims
     *            the list of simulations.
     * @return true if build is successful, otherwise false.
     * @throws IOException
     *             if build fails to execute.
     * @throws SimulationBuildFailedException
     */
    public boolean buildSimsInNetsim() throws SimulationBuildFailedException, IOException {

        cleanNetsimScriptsFolderOnProductionNetsimServer();

        createNetsimScripts();

        copyLocalNetsimScriptsToProductionNetsimServer();

        return executeScriptsOnProductionNetsimServer() == SUCCESFULLY_EXECUTED;
    }

    private int cleanNetsimScriptsFolderOnProductionNetsimServer() throws IOException {
        logger.debug("Following folder is going to be deleted:" + ONENETWORK_NETSIM_SCRIPTS_FOLDER);

        final SshClient sshClient = new SshClient();

        try {
            authenticateAndConnectToRemoteServer(sshClient);

            final String cmdToRemoveContentsOfOneNetworkNetsimScriptsFolder = "rm -rfv " + ONENETWORK_NETSIM_SCRIPTS_FOLDER + "/*";
            return sshClient.executeCommand(cmdToRemoveContentsOfOneNetworkNetsimScriptsFolder);

        } finally {
            sshClient.close();
        }
    }

    private static void setFileLocationsBasedOnServer() {
        ONENETWORK_SS_SCRIPTS_FOLDER = getPlatformIndependentPathString(SimBuildManager.class.getResource("/ss/scripts").getFile());
    }

    // If the beginning of the string is a slash, then a character, then a colon
    // and another slash, replace it with the character, the colon, and the
    // slash (leaving the leading slash off)
    private static String getPlatformIndependentPathString(final String path) {
        return path.replaceFirst("^/(.:/)", "$1");
    }

    private void copyLocalNetsimScriptsToProductionNetsimServer() throws IOException {
        if (!ServerUtility.isRunningOnProductionNetsimServer()) {
            copyFilesToRemoteNetsimServer();
        }
    }

    private int executeScriptsOnProductionNetsimServer() throws IOException {
        logger.info("Building simulations on netsim server...");

        final SshClient sshClient = new SshClient();

        final Path remoteDestPath = Paths.get(ONENETWORK_NETSIM_SCRIPTS_FOLDER);

        try {
            authenticateAndConnectToRemoteServer(sshClient);

            final String script = getScriptWithFullPath(remoteDestPath);

            final String cmdToExecuteScriptAfterMakeScriptsExecutableAndConvertThemToUnixFormat = "cd " + ONENETWORK_NETSIM_SCRIPTS_FOLDER
                    + "; chmod +x `find . -print | egrep -i 'sh|pl'`"
                    + "; dos2unix `find . -print | egrep -i 'sh|pl'`"
                    + ";" + script;

            final int commandResult = sshClient.executeCommand(cmdToExecuteScriptAfterMakeScriptsExecutableAndConvertThemToUnixFormat);

            if (commandResult == 0) {
                logger.info("Successfully built simulations on netsim server.");
            } else {
                logger.info("Failed to build simulations on netsim server.");
            }

            return commandResult;

        } finally {
            sshClient.close();
        }
    }

    private String getScriptWithFullPath(final Path remoteDestPath) {
        final String script = remoteDestPath.toString().replace("\\", "/") + "/" + ONENETWORK_SIMULATION_BUILD_SCRIPT;
        logger.debug("script:{}", script);
        return script;
    }

    private void authenticateAndConnectToRemoteServer(final SshClient sshClient) throws IOException {
        sshClient.authUserPassword(ONENETWORK_NETSIM_BUILD_SERVER_USER, ONENETWORK_NETSIM_BUILD_SERVER_PWD);

        if (ServerUtility.isRunningOnWindowsServer()
                || ServerUtility.isRunningOnJenkinsServer()) {
            sshClient.connectThroughDefaultTunnel(ONENETWORK_NETSIM_BUILD_SERVER);
        } else if (ServerUtility.isRunningOnProductionNetsimServer()
                || ServerUtility.isRunningOnNetsimServerApartFromProductionServer()) {
            sshClient.connect(ONENETWORK_NETSIM_BUILD_SERVER);
        } else {
            sshClient.connect(ONENETWORK_NETSIM_BUILD_SERVER);
        }
    }

    /**
     * Creates simulation build scripts for NETSim.
     *
     * @param sims
     *            the list of simulations
     * @throws IOException
     *             if files creation fails
     * @throws SimulationBuildFailedException
     */
    public void createNetsimScripts() throws IOException, SimulationBuildFailedException {

        createNetsimScriptsBaseDir();

        createMoFiles(getSims());

        createMmlFiles(getSims());

        copyBuildScriptsToDefaultNetsimScriptsFolderLocally();
    }

    private void createNetsimScriptsBaseDir() throws IOException {
        final Path netsimFilesFolderPath = Paths.get(ONENETWORK_NETSIM_SCRIPTS_FOLDER);

        if (Files.notExists(netsimFilesFolderPath)) {
            Files.createDirectories(netsimFilesFolderPath);
        }
        FileUtils.deleFolderContents(netsimFilesFolderPath);
    }

    private void createMoFiles(final List<Sim> sims) throws IOException {
        for (final Sim sim : sims) {
            createMoFilesPerSim(sim);
        }
    }

    private void createMoFilesPerSim(final Sim sim) {
        for (final Map.Entry<String, String> entry : sim.getNetworkElementToMoScriptMap().entrySet()) {
            final String moScriptFileName = entry.getKey() + ".mo";
            final String neMoScript = entry.getValue();
            logger.debug("\n{}=\n{}", moScriptFileName, neMoScript);

            FileUtils.writeToFile(ONENETWORK_NETSIM_SCRIPTS_FOLDER, moScriptFileName, neMoScript);
        }
    }

    private void createMmlFiles(final List<Sim> sims) throws IOException {
        for (final Sim sim : sims) {
            createMmlFilesPerSim(sim);
        }
    }

    private void createMmlFilesPerSim(final Sim sim) {
        final String mmlScriptFileName = sim.getName() + ".mml";
        final String mmlScript = sim.getMmlCmd();
        logger.debug("\n{}=\n{}", mmlScriptFileName, mmlScript);

        FileUtils.writeToFile(ONENETWORK_NETSIM_SCRIPTS_FOLDER, mmlScriptFileName, mmlScript);
    }

    private void copyBuildScriptsToDefaultNetsimScriptsFolderLocally() throws IOException {
        setFileLocationsBasedOnServer();
        final Path scriptsFolderPath = Paths.get(ONENETWORK_SS_SCRIPTS_FOLDER);
        final Path netsimFilesFolderPath = Paths.get(ONENETWORK_NETSIM_SCRIPTS_FOLDER);
        FileUtils.copyFolderFiles(scriptsFolderPath, netsimFilesFolderPath);
    }

    private boolean copyFilesToRemoteNetsimServer() throws IOException {

        logger.info("Copying simulation scripts to netsim server...");
        final SshClient sshClient = new SshClient();

        final Path sourceFolderPath = Paths.get(ONENETWORK_NETSIM_SCRIPTS_FOLDER);
        // for now both source and dest. folder the same
        final Path remoteDestPath = Paths.get(ONENETWORK_NETSIM_SCRIPTS_FOLDER);

        final List<Path> paths = FileUtils.getSourceFiles(sourceFolderPath);

        try {
            authenticateAndConnectToRemoteServer(sshClient);

            if (sshClient.copyFiles(paths, remoteDestPath)) {
                logger.info("Successfully copied simulation scripts to netsim server.");
                return true;
            } else {
                logger.info("Failed to copy simulation scripts to netsim server.");
                return false;
            }

        } finally {
            sshClient.close();
        }
    }

    /**
     * Returns the sims for the processing.
     *
     * @return the sims
     * @throws SimulationBuildFailedException
     *             if sims are not set.
     */
    private List<Sim> getSims() throws SimulationBuildFailedException {
        if (sims == null) {
            throw new SimulationBuildFailedException("Simulations are not set!");
        }
        return sims;
    }

    /**
     * Sets the sims for the simulation build processing on a simulator.
     *
     * @param sims
     *            the sims to be created on a simulator
     */
    public void setSims(final List<Sim> sims) {
        this.sims = sims;
    }
}
