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

package com.ericsson.de.onetwork.cs.exceptions;

/**
 * Thrown when simulation zips cannot be uploaded to Nexus
 *
 * @author ecasjim
 */
public class NexusUploadException extends Exception {

    private static final long serialVersionUID = 3270552387906951996L;

    /**
     * Constructs an NexusUploadException without a detail message.
     */
    public NexusUploadException() {
        super();
    }

    /**
     * Constructs an NexusUploadException with a detail message.
     *
     * @param s
     *            Describes the reason for the exception.
     */
    public NexusUploadException(final String s) {
        super(s);
    }
}
