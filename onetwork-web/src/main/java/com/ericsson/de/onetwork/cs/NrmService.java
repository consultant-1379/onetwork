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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.ericsson.de.onetwork.gnm.Gnm;
import com.ericsson.de.onetwork.gnm.GnmManager;
import com.ericsson.de.onetwork.gnm.GnmRequestException;
import com.ericsson.de.onetwork.nrm.Nrm;
import com.ericsson.de.onetwork.nrm.NrmManager;

/**
 * Provides a service to access NRM
 *
 * @author eaefhiq
 */
@Path("/nrm")
public class NrmService {

    /**
     * Gets names of all NRMs.
     *
     * @return names of all NRMs.
     */
    @GET
    @Path("/getall")
    public String getAll() {

        final NrmManager nrmManager = new NrmManager();

        return nrmManager.getNrmList().toString();

    }

    /**
     * Gets a list of GNMs by a given NRM name.
     *
     * @param name
     *            the name of a NRM
     * @return a list of GNMs
     * @throws GnmRequestException
     *             Thrown when a requested <code>Gnm</code> cannot be returned.
     */
    @GET
    @Path("{nrmName}")
    @Produces({ "application/json", "application/xml" })
    public List<Gnm> getGnmsByNrmName(@PathParam("nrmName") final String nrmName) throws GnmRequestException {
        final NrmManager nrmManager = new NrmManager();
        final Nrm nrm = nrmManager.getNrm(nrmName);
        final List<String> gnmNames = nrm.getGnmNames();
        final List<Gnm> gnms = new ArrayList<Gnm>();
        for (final String gnmName : gnmNames) {
            gnms.add(GnmManager.getGnm(gnmName));
        }
        return gnms;
    }

}
