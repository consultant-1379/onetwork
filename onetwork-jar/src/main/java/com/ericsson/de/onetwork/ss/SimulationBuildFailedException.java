/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.ss;

/**
 * Thrown when simulation build operation fails.
 *
 * @author qfatonu
 */
public class SimulationBuildFailedException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an SimulationBuildFailedException without a detail message.
     */
    public SimulationBuildFailedException() {
        super();
    }

    /**
     * Constructs an SimulationBuildFailedException with a detail message.
     *
     * @param s
     *            Describes the reason for the exception.
     */
    public SimulationBuildFailedException(final String s) {
        super(s);
    }
}
