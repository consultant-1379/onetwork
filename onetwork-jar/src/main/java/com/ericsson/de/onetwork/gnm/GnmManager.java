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

package com.ericsson.de.onetwork.gnm;

/**
 * Stores and manages access to all the <code>Gnm</code> revisions available for
 * Network creation.
 *
 * @author ecasjim
 */
public class GnmManager {

    private static final GnmReader gnmXmlReader = new GnmXmlReader();

    /**
     * Returns the requested Gnm object.
     *
     * @param gnmRevision
     *            the revision number of the required Gnm
     * @return requested Gnm object
     * @throws GnmRequestException
     *             thrown due to issue retrieving Gnm.
     */
    public static Gnm getGnm(final String gnmRevision) throws GnmRequestException {
        return gnmXmlReader.getGnm(gnmRevision);
    }
}
