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

package com.ericsson.de.onetwork.bs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.NetworkRequest;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.gnm.GnmRequestException;

/**
 * Tests used to verify a network object was created with mandatory features
 * applied.
 *
 * @author ecasjim
 */
public class BusinessServiceControllerIT {

    private final static Logger logger = LoggerFactory.getLogger(BusinessServiceControllerIT.class);

    /** Network object. */
    private Network network;

    /** Number of nodes within a network */
    private static final int NUM_OF_NODES = 12;

    /** Cell pattern average across network */
    private static final int CELL_PATTERN_AVERAGE = 4;

    @BeforeClass
    public void setup() throws GnmRequestException {
        logger.debug("Starting BusinessServiceControllerIT");
        final List<String> features = new ArrayList<String>();
        features.add("Cell Pattern Assignment");
        final String gnmRevisionTest = "LTE_R1";
        final NetworkRequest networkRequest = new NetworkRequest.Builder(gnmRevisionTest, NUM_OF_NODES).featureNames(features).build();
        final BusinessServiceController bld = new BusinessServiceController();

        network = bld.buildNetwork(networkRequest);
    }

    @Test
    public final void verifyNetworkSizeAsDefinedInGnmByCountingNEsCreated() throws GnmRequestException {
        Assert.assertEquals(network.getNetworkElements().size(), NUM_OF_NODES);
    }

    @Test
    public final void verifyLteNetwork_WithNoFetaures_And_ClientEnteredMimVersion() throws GnmRequestException, InvalidChildException {
        final List<String> features = new ArrayList<String>();
        final String gnmRevisionTest = "LTE_R1";
        final String mimVersion = "LTE ERBS F1101";
        final NetworkRequest networkRequest = new NetworkRequest.Builder(gnmRevisionTest, NUM_OF_NODES)
                .featureNames(features)
                .mimVersion(mimVersion)
                .build();
        final BusinessServiceController bld = new BusinessServiceController();
        final Network network = bld.buildNetwork(networkRequest);

        Assert.assertEquals(getMoCountFromNetwork(network, "ManagedElement,ENodeBFunction,EUtranCellFDD"), NUM_OF_NODES * CELL_PATTERN_AVERAGE);
        Assert.assertEquals(network.getNetworkElements().get(0).getMimVersion(), mimVersion);
    }

    @Test(expectedExceptions = GnmRequestException.class)
    public final void verifyThrownExceptionForNotSupportedGnmNetworkType() throws GnmRequestException {
        final String gnmRevisionTest = "WRAN_R1";
        final List<String> features = new ArrayList<String>();
        final NetworkRequest networkRequest = new NetworkRequest.Builder(gnmRevisionTest, NUM_OF_NODES).featureNames(features).build();
        final BusinessServiceController bld = new BusinessServiceController();
        bld.buildNetwork(networkRequest);
    }

    @Test(expectedExceptions = GnmRequestException.class)
    public final void verifyThrownExceptionForInvalidGnmNetworkType() throws GnmRequestException {
        final String gnmRevisionTest = "ERROR_R1";
        final List<String> features = new ArrayList<String>();
        final NetworkRequest networkRequest = new NetworkRequest.Builder(gnmRevisionTest, NUM_OF_NODES).featureNames(features).build();
        final BusinessServiceController bld = new BusinessServiceController();
        bld.buildNetwork(networkRequest);
    }

    private int getMoCountFromNetwork(final Network network, final String moTypeHeirarchy) throws InvalidChildException {
        final List<NetworkElement> networkElements = network.getNetworkElements();
        int totalMos = 0;

        for (final NetworkElement networkElement : networkElements) {
            totalMos += networkElement.getMosFromNe(moTypeHeirarchy).size();
        }

        return totalMos;
    }
}
