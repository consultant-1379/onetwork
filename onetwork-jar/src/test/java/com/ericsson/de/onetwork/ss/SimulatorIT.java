
package com.ericsson.de.onetwork.ss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.NetworkRequest;
import com.ericsson.de.onetwork.bs.BusinessServiceController;
import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.gnm.GnmRequestException;
import com.ericsson.de.onetwork.ss.util.SshClient;

/**
 * Verifies e-2-e simulation creation process;
 *
 * @author qfatonu
 */
@Test(enabled = false)
public class SimulatorIT {

    private final static Logger logger = LoggerFactory.getLogger(SimulatorIT.class);

    private static final String LTE_GNM_REVISION = "LTE_R1";
    private static final String CORE_GNM_REVISION = "CORE_R1";

    private static final int NUM_OF_NETWORK_ELEMENTS_LTE = 12;
    private static final int NUM_OF_NETWORK_ELEMENTS_CORE = 3;

    private Network coreNetwork;
    private Network lteNetwork;

    private static final String LTE_SIM_ZIP_FILE_NAME = "LTEF1101x" + NUM_OF_NETWORK_ELEMENTS_LTE + "-GEN-LTE01.zip";
    private static final String CORE_SIM_ZIP_FILE_NAME = "CORE15A-WPP-V4x" + NUM_OF_NETWORK_ELEMENTS_CORE + "-GEN-CORE01.zip";

    private static String USER_1 = "netsim";
    private static String USER_1_PWD = "netsim";
    private static String USER_1_HOST = "netsimv006-04.athtem.eei.ericsson.se";

    private SshClient sshClient;

    @BeforeClass
    public void setup() throws GnmRequestException, SimulationBuildFailedException, IOException {
        logger.debug("Starting SimulatorIT_bk");
        logger.debug("BEFORE_TEST START");
        removeZipFilesFromRemoteNetsimServer();

        final Simulator netsim = new NetsimSimulator();

        final List<String> lteFeatures = new ArrayList<String>();
        lteFeatures.add("Cell Pattern Assignment");

        final List<String> coreFeatures = new ArrayList<String>();
        coreFeatures.add("Hardware Item Support");

        final NetworkRequest coreNetworkRequest =
                new NetworkRequest.Builder(CORE_GNM_REVISION, NUM_OF_NETWORK_ELEMENTS_CORE).featureNames(coreFeatures).build();
        final NetworkRequest lteNetworkRequest =
                new NetworkRequest.Builder(LTE_GNM_REVISION, NUM_OF_NETWORK_ELEMENTS_LTE).featureNames(lteFeatures).build();

        final BusinessServiceController bld = new BusinessServiceController();
        coreNetwork = bld.buildNetwork(coreNetworkRequest);
        lteNetwork = bld.buildNetwork(lteNetworkRequest);

        netsim.simulateNetwork(lteNetwork);
        netsim.simulateNetwork(coreNetwork);
        logger.debug("BEFORE_TEST END");
    }

    @BeforeMethod
    public void beforeMethod() throws IOException {
        logger.debug("BEFORE_METHOD START");
        sshClient = new SshClient();
        sshClient.authUserPassword(USER_1, USER_1_PWD);
        sshClient.connectThroughDefaultTunnel(USER_1_HOST);
        logger.debug("BEFORE_METHOD END");
    }

    @AfterMethod
    public void afterMethod() {
        logger.debug("AFTER_METHOD");
        sshClient.close();
        logger.debug("AFTER_METHOD END");
    }

    @Test(enabled = false)
    public void verifyCreatedLTESimulationExists() throws IOException {
        final String cmd1 = "ls /netsim/netsimdir/" + LTE_SIM_ZIP_FILE_NAME;
        final int exitCode = sshClient.executeCommand(cmd1);
        Assert.assertEquals(exitCode, 0, "Only successful when exitCode equals to 0");
    }

    @Test(enabled = false)
    public void verifyCreatedCORESimulationExists() throws IOException {
        final String cmd1 = "ls /netsim/netsimdir/" + CORE_SIM_ZIP_FILE_NAME;
        final int exitCode = sshClient.executeCommand(cmd1);
        Assert.assertEquals(exitCode, 0, "Only successful when exitCode equals to 0");
    }

    private void removeZipFilesFromRemoteNetsimServer() throws IOException {
        final SshClient sshClient2 = new SshClient();
        try {
            sshClient2.authUserPassword(USER_1, USER_1_PWD);
            sshClient2.connectThroughDefaultTunnel(USER_1_HOST);

            final String removeSims =
                    "rm -v /netsim/netsimdir/" + LTE_SIM_ZIP_FILE_NAME + " /netsim/netsimdir/" + CORE_SIM_ZIP_FILE_NAME + "echo sims are removed= $?";
            sshClient2.executeCommand(removeSims);

        } finally {
            sshClient2.close();
        }
    }
}
