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
 * Used to distinguish between system created and feature created MO/attributes.
 *
 * @author ecasjim
 */
public enum CreationContext {
    /** Indicates that the MO/attribute is system created. */
    SYSTEM_CREATED,

    /** Indicates that the MO/attribute is feature created. */
    FEATURE_CREATED
}
