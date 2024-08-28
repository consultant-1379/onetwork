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

package com.ericsson.de.onetwork.ext.ns;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.ss.util.SshClient;
import com.ericsson.de.onetwork.util.ServerUtility;

/**
 * Provides basic NETSim services not part of 1Network core services.
 * <p>
 * Assumes that NETSim server contains simdep code at following location:
 * /netsim/onetwork/enm-ni-simdep-testware.
 *
 * @author qfatonu
 */
public class Netsim {

    private static final Logger logger = LoggerFactory.getLogger(Netsim.class);

    private static final String ONENETWORK_NETSIM_BUILD_SERVER_KEY = "onenetwork_netsim_build_server";
    private static final String ONENETWORK_NETSIM_BUILD_SERVER_USER_KEY = "onenetwork_netsim_build_server_user";
    private static final String ONENETWORK_NETSIM_BUILD_SERVER_PWD_KEY = "onenetwork_netsim_build_server_pwd";
    private static final String SIMDEP_NETSIM_UPGARDE_CMD_KEY = "simdep_netsim_upgrade_cmd";

    private static final String SIMDEP_NETSIM_SCRIPTS_FOLDER =
            "/netsim/onetwork/enm-ni-simdep-testware/ERICTAFenmnisimdep_CXP9031884/src/main/resources/scripts/netsim_install/bin";

    private static final String CONFIG_FILE = "netsim_upgrade.propsXX";

    private static final boolean FAILED = false;
    private static final int SUCCESFUL = 0;

    private static final String ONENETWORK_NETSIM_BUILD_SERVER;
    private static final String ONENETWORK_NETSIM_BUILD_SERVER_USER;
    private static final String ONENETWORK_NETSIM_BUILD_SERVER_PWD;
    private static final String SIMDEP_NETSIM_UPGARDE_CMD;

    /**
     * Application configuration file.
     */
    private static Properties appProps;

    /**
     * Default application properties.
     */
    private static Properties defaultProps;

    static {
        defaultProps = new Properties();
        defaultProps.setProperty(ONENETWORK_NETSIM_BUILD_SERVER_KEY, "netsimv006-04.athtem.eei.ericsson.se");
        defaultProps.setProperty(ONENETWORK_NETSIM_BUILD_SERVER_USER_KEY, "root");
        defaultProps.setProperty(ONENETWORK_NETSIM_BUILD_SERVER_PWD_KEY, "shroot");
        defaultProps.setProperty(SIMDEP_NETSIM_UPGARDE_CMD_KEY, "master.sh -r N -p yes -d no -f no -c no");
    }

    static {
        try (final InputStream in = Netsim.class.getResourceAsStream("/ext/ns/" + CONFIG_FILE))
        {
            appProps = new Properties();
            appProps.load(in);

            logger.info("User defined properties file {} is loaded successfully.", CONFIG_FILE);
        } catch (final IOException | NullPointerException e) {
            logger.debug("Ignorable error (null is expected): {}", e.getMessage());
            logger.info("Default properties are loaded successfully.", CONFIG_FILE);
            appProps = new Properties(defaultProps);
        }

        ONENETWORK_NETSIM_BUILD_SERVER = appProps.getProperty(ONENETWORK_NETSIM_BUILD_SERVER_KEY);
        ONENETWORK_NETSIM_BUILD_SERVER_USER = appProps.getProperty(ONENETWORK_NETSIM_BUILD_SERVER_USER_KEY);
        ONENETWORK_NETSIM_BUILD_SERVER_PWD = appProps.getProperty(ONENETWORK_NETSIM_BUILD_SERVER_PWD_KEY);
        SIMDEP_NETSIM_UPGARDE_CMD = appProps.getProperty(SIMDEP_NETSIM_UPGARDE_CMD_KEY);
    }

    /**
     * Upgrades NETSim software to the latest version including patches
     * forcefully.
     *
     * @return true if successful, otherwise false.
     * @throws IOException
     *             if ssh communication fails.
     */
    public static boolean upgradeToLatestVersion() throws IOException {

        logger.info("Start upgrading NETSim server({})", ONENETWORK_NETSIM_BUILD_SERVER);

        final SshClient sshClient = new SshClient();

        try {
            authenticateAndConnectToRemoteServer(sshClient);
            logger.info("Connection established to the NETSim server({})", ONENETWORK_NETSIM_BUILD_SERVER);

            if (checkForSimdepCodeExistenceOnGivenClient(sshClient)) {
                logger.info("Simdep code has found on NETSim server({})", ONENETWORK_NETSIM_BUILD_SERVER);

                final String cmdToExecuteUpgradeNetsim =
                        "cd " + SIMDEP_NETSIM_SCRIPTS_FOLDER + "; bash " + SIMDEP_NETSIM_UPGARDE_CMD;

                logger.info("Cmd yet to be executed: {}  ({})", cmdToExecuteUpgradeNetsim, ONENETWORK_NETSIM_BUILD_SERVER);
                return sshClient.executeCommand(cmdToExecuteUpgradeNetsim) == SUCCESFUL;

            } else {
                logger.error("Simdep code deos not exist on NETSim server({})...", ONENETWORK_NETSIM_BUILD_SERVER);
                return FAILED;
            }

        } finally {
            sshClient.close();
        }
    }

    private static boolean checkForSimdepCodeExistenceOnGivenClient(final SshClient sshClient) throws IOException {
        final String cmdToCheckExitenceOfSimDepCodeOnServer = "ls -la " + SIMDEP_NETSIM_SCRIPTS_FOLDER;
        return sshClient.executeCommand(cmdToCheckExitenceOfSimDepCodeOnServer) == SUCCESFUL;
    }

    private static void authenticateAndConnectToRemoteServer(final SshClient sshClient) throws IOException {
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
}
