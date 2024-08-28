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

package com.ericsson.de.onetwork.dms.mo.datatype;

/**
 * The attribute data types are used by the MO attributes within NETSim.
 *
 * @author eaefhiq
 */
public enum AttributeDataType {

    /** The Struct attribute data type of an MO. */
    Struct,
    /** The Enum attribute data type of an MO. */
    Enum,
    /** The Array attribute data type of an MO. */
    Array,
    /** The Boolean attribute data type of an MO. */
    Boolean,
    /** The String attribute data type of an MO. */
    String,
    /** The Integer attribute data type of an MO. */
    Integer,
    /** The Ref attribute data type of an MO. */
    Ref,
    /** The Long attribute data type of an MO. */
    Long
}
