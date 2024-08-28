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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.NetworkRequest;
import com.ericsson.de.onetwork.bs.BusinessServiceController;
import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.cs.exceptions.NexusUploadException;
import com.ericsson.de.onetwork.cs.util.OnetworkServiceUtility;
import com.ericsson.de.onetwork.gnm.GnmRequestException;
import com.ericsson.de.onetwork.ss.NetsimSimulator;
import com.ericsson.de.onetwork.ss.SimulationBuildFailedException;
import com.ericsson.de.onetwork.ss.Simulator;
import com.ericsson.de.onetwork.ss.util.InvalidMimVersionFormatException;
import com.ericsson.de.onetwork.ui.SocketHandler;

/**
 * Triggers off the 1Network service to create the network.
 *
 * @author eaefhiq
 */
@Path("/onetworkservice")
public class OnetworkService {

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(OnetworkService.class);

    /**
     * Generates the network simulations and uploads them to the Nexus server.
     *
     * @param clientDefinedNetworks
     *            the client defined networks
     * @return the response
     * @throws GnmRequestException
     *             the gnm request exception
     * @throws NexusUploadException
     * @throws SimulationBuildFailedException
     * @throws IOException
     * @throws InvalidMimVersionFormatException
     */
    @POST
    @Path("/generatenetworks")
    @Produces({ "application/json", "application/xml" })
    public Response generateNetworkSimulations(final List<ClientDefinedNetwork> clientDefinedNetworks) {
        try {
            OnetworkServiceUtility.cleanSimulationsAndZipFiles();

            logger.debug("Client defined network: " + clientDefinedNetworks);
            final List<Network> networks = createNetworks(clientDefinedNetworks);
            createSimulations(networks);
            final String zipFileName = OnetworkServiceUtility.zipAllsimulations();
            OnetworkServiceUtility.uploadZipFromNetsimToNexus(zipFileName);
            return Response.status(200).entity("1Network service started successfullly").build();
        } catch (final IOException | GnmRequestException | InvalidMimVersionFormatException | SimulationBuildFailedException
                | NexusUploadException exception) {
            // TODO: In the future, catch and return better error responses to
            // client - GnmRequestException, NexusUploadException,
            // SimulationBuildFailedException, IOException etc.
            exception.printStackTrace();
            logger.error("1Network Exception caught: {}", exception.getMessage());
            return Response.serverError().entity("1Network service failed.").build();
        } catch (final Exception exception) {
            exception.printStackTrace();
            logger.error("Generic Exception caught: {}", exception.getMessage());
            return Response.serverError().entity("1Network service failed.").build();
        } finally {
            SocketHandler.setReadLogFile(false);
        }
    }

    /**
     * Creates the networks that are defined by a customer.
     *
     * @param clientDefinedNetworks
     *            the client defined networks
     * @return the list of networks
     * @throws GnmRequestException
     *             Thrown when a requested <code>Gnm</code> cannot be returned.
     * @throws InvalidMimVersionFormatException
     */
    private List<Network> createNetworks(final List<ClientDefinedNetwork> clientDefinedNetworks) throws GnmRequestException,
            InvalidMimVersionFormatException {
        final List<Network> networks = new ArrayList<Network>();
        for (final ClientDefinedNetwork clientDefinedNetwork : clientDefinedNetworks) {
            clientDefinedNetwork.getFeatures().remove("");
            final BusinessServiceController bld = new BusinessServiceController();
            logger.debug("number of features: {}", clientDefinedNetwork.getFeatures().size());
            networks.add(bld.buildNetwork(getNetworkRequest(clientDefinedNetwork)));
        }
        return networks;
    }

    private NetworkRequest getNetworkRequest(final ClientDefinedNetwork clientDefinedNetwork) throws InvalidMimVersionFormatException {

        if (clientDefinedNetwork.getMimVersion() == null) {
            logger.debug("mimVersion set by GNM");
            return new NetworkRequest.Builder(
                    clientDefinedNetwork.getGnmName(),
                    clientDefinedNetwork.getSize())
                            .featureNames(clientDefinedNetwork.getFeatures())
                            .build();
        } else {
            logger.debug("mimVersion set by Client to={}", clientDefinedNetwork.getMimVersion());
            return new NetworkRequest.Builder(
                    clientDefinedNetwork.getGnmName(),
                    clientDefinedNetwork.getSize())
                            .featureNames(clientDefinedNetwork.getFeatures())
                            .mimVersion(clientDefinedNetwork.getMimVersion())
                            .build();
        }
    }

    private void createSimulations(final List<Network> networks) throws SimulationBuildFailedException {
        final Simulator netsim = new NetsimSimulator();
        for (final Network network : networks) {
            netsim.simulateNetwork(network);
        }
    }
}
