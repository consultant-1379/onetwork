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

package com.ericsson.de.onetwork.cs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.ericsson.de.onetwork.NetworkRequest;
import com.ericsson.de.onetwork.NetworkType;
import com.ericsson.de.onetwork.bs.BusinessServiceController;
import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.bs.features.FeatureManager;
import com.ericsson.de.onetwork.bs.features.FeatureModule;
import com.ericsson.de.onetwork.cs.exceptions.NexusUploadException;
import com.ericsson.de.onetwork.cs.nodeintro.LatestErbsMimsHandler;
import com.ericsson.de.onetwork.cs.util.OnetworkServiceUtility;
import com.ericsson.de.onetwork.gnm.GnmRequestException;
import com.ericsson.de.onetwork.ss.NetsimSimulator;
import com.ericsson.de.onetwork.ss.SimulationBuildFailedException;
import com.ericsson.de.onetwork.util.ServerUtility;

/**
 * TODO: Manage multiple test classes using ssh tunnel.
 * Integration test for the 1Network.
 *
 * @author ecasjim
 */
@Test(enabled = true)
public class OnetworkServiceIT {

    private final static String SIMS_PROJECT_CODE = "ERIConetworksims_CXP9038429-";
    private final static String GNM_REVISION = "LTE_R1";
    private final static int TOTAL_NODES = 2;

    /**
     * Test method for
     * {@link com.ericsson.de.onetwork.cs.OnetworkService#generateNetworkSimulations(java.util.List)}
     * This is an integration test. For this test to be picked up by maven
     * plugin, it needs "IT" at the end of the test name. These integration
     * tests (ITs) will be run at acceptance and release jobs.
     *
     * @throws GnmRequestException
     * @throws SimulationBuildFailedException
     * @throws IOException
     * @throws NexusUploadException
     */
    @Test(enabled = false)
    public final void _1NetworkFullSliceAllLteFeatures() throws GnmRequestException, SimulationBuildFailedException, IOException,
            NexusUploadException {
        final String revision = OnetworkServiceUtility.extractVersionFromPom();
        final String zipFileName = SIMS_PROJECT_CODE + revision + ".zip";

        final NetworkRequest networkRequest = new NetworkRequest.Builder(
                GNM_REVISION, TOTAL_NODES)
                        .featureNames(getAllSupportedFeatureNamesOfNetworkType(NetworkType.LTE))
                        .build();

        final Network network = new BusinessServiceController().buildNetwork(networkRequest);

        new NetsimSimulator().simulateNetwork(network);
        OnetworkServiceUtility.zipAllsimulations();

        if (ServerUtility.isRunningOnJenkinsServer()) {
            OnetworkServiceUtility.uploadZipFromNetsimToNexus(zipFileName);
        }
    }

    private List<String> getAllSupportedFeatureNamesOfNetworkType(final NetworkType networkType) {
        final FeatureManager featureManager = new FeatureManager();
        final List<String> allFeatureNames = new ArrayList<String>();

        for (final FeatureModule feature : featureManager.getAllFeatures()) {
            if (feature.getNetworkType().equals(networkType)) {
                allFeatureNames.add(feature.getName());
            }
        }

        return allFeatureNames;
    }

    @Test(enabled = false)
    public void testDownload() throws IOException {
        final String FTP_SERVER = "ftp.lmera.ericsson.se";
        final String PARENT_DIR = "/project/netsim-ftp/simulations/NEtypes/";
        final int PORT = 21;
        final LatestErbsMimsHandler mimHandler = new LatestErbsMimsHandler(FTP_SERVER, PORT, PARENT_DIR);
        mimHandler.setNodeMap("ERBSG1220-lim", "R27E");
        final String mimName = "LTE ERBS G1220-lim";
        final String simulationName = mimHandler.getSimulationName(mimName);
        OnetworkServiceUtility.downloadSimulationToServer(simulationName);
    }
}
