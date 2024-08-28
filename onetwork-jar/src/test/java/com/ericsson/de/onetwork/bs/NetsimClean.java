
package com.ericsson.de.onetwork.bs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.ss.util.SshClient;
import com.ericsson.de.onetwork.util.ServerUtility;

@Test(enabled = false)
public class NetsimClean {

    private static final Logger logger = LoggerFactory.getLogger(NetsimClean.class);
    private static final String ONENETWORK_NETSIM_SCRIPTS_FOLDER = "/tmp/prod/onenetwork/";
    private static final String ONENETWORK_NETSIM_SIMS_FOLDER = "/netsim/netsimdir/";
    private static final String ONENETWORK_NETSIM_BUILD_SERVER = "netsimv006-04.athtem.eei.ericsson.se";
    private static final String ONENETWORK_NETSIM_BUILD_SERVER_USER = "netsim";
    private static final String ONENETWORK_NETSIM_BUILD_SERVER_PWD = "netsim";

    @BeforeSuite(enabled = false)
    public void beforeSuite() {
        cleanNetsim();
        logger.debug("Cleaned netsim before tests.");
    }

    @Test(enabled = false)
    public void clean() {

    }

    @AfterSuite(enabled = false)
    public void afterSuite() {
        cleanNetsim();
        logger.debug("Cleaned netsim afters tests.");
    }

    private void cleanNetsim() {
        final SshClient sshClient = new SshClient();

        try {
            authenticateAndConnectToRemoteServer(sshClient);
            final String cmdToCloseNetsimGui = "/netsim/inst/./restart_gui";
            final String cmdToRemoveContentsOfOneNetworkNetsimScriptsFolder = "rm -rfv " + ONENETWORK_NETSIM_SCRIPTS_FOLDER + "/*";
            final String cmdToRemoveLteSims = "rm -rfv " + ONENETWORK_NETSIM_SIMS_FOLDER + "/LTE*";
            final String cmdToRemoveCoreSims = "rm -rfv " + ONENETWORK_NETSIM_SIMS_FOLDER + "/CORE*";

            sshClient.executeCommand(cmdToCloseNetsimGui);
            sshClient.executeCommand(cmdToRemoveContentsOfOneNetworkNetsimScriptsFolder);
            sshClient.executeCommand(cmdToRemoveLteSims);
            sshClient.executeCommand(cmdToRemoveCoreSims);
        } catch (final IOException e) {
            logger.debug("Could not clean netsim: {}", e.getMessage());
        } finally {
            sshClient.close();
        }
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

}
