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

package com.ericsson.de.onetwork.dms.generics;

/**
 * The platform on which the MIM xml is based. For example CPP, COM-ECIM.
 *
 * @author edalrey
 * @since 1Network_15.12
 */
public enum Platform {

    /** Indicates that the underlying MOM is based on the CPP platform. */
    CPP,

    /** Indicates that the underlying MOM is based on the COM-ECIM platform. */
    COMECIM;
}
