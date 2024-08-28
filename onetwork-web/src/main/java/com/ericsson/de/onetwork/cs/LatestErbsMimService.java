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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.cs.nodeintro.LatestErbsMimsHandler;

/**
 * Provides a service to access Top 10 latest ERBS MIMs from the FTP site.
 *
 * @author eephmar
 */
@Path("/latestmims")
public class LatestErbsMimService {

    private final static Logger logger = LoggerFactory.getLogger(LatestErbsMimService.class);
    private static final String FTP_SERVER = "ftp.lmera.ericsson.se";
    private static final String PARENT_DIR = "/project/netsim-ftp/simulations/NEtypes/";
    private static final int PORT = 21;

    /**
     * Gets the top 10 latest ERBS mims.
     *
     * @return Names of all ERBS Mims in JSON string format: { "lteNodes":[{"name":"ERBS1"},{"name":"ERBS2"}]}.
     */
    @GET
    @Path("/top10mims")
    public Response getTop10ErbsMims() {
        try {
            final LatestErbsMimsHandler mimHandler = new LatestErbsMimsHandler(FTP_SERVER, PORT, PARENT_DIR);
            final String top10Mims = mimHandler.getLatestMimNames();
            return Response.ok().entity(top10Mims).build();
        } catch (final IOException ioException) {
            logger.error(ioException.getMessage());
            return Response.serverError().entity("Unable to retrieve MIMs from the server.").build();
        }
    }

}
