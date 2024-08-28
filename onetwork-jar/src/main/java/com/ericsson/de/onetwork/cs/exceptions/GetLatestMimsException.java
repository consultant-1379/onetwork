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

import java.io.IOException;

/**
 * Thrown when there are no valid MIMs retrieved from the FTP server.
 *
 * @author eephmar
 */
public class GetLatestMimsException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an GetLatestMimsException without a detailed message.
     */
    public GetLatestMimsException() {
        super();
    }

    /**
     * Constructs an GetLatestMimsException with a detailed message.
     *
     * @param s
     *            Describes the reason for the exception.
     */
    public GetLatestMimsException(final String s) {
        super(s);
    }
}
