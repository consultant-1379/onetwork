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
 * The GnmReader interface provides the retrieval of a <code>Gnm</code> object
 * from its location
 *
 * @author ecasjim
 */
public interface GnmReader {

    /**
     * Used to retrieve a Gnm object from its storage location.
     *
     * @param gnmRevision
     *            the Gnm revision required.
     * @return requested Gnm object.
     */
    Gnm getGnm(String gnmRevision) throws GnmRequestException;

}
