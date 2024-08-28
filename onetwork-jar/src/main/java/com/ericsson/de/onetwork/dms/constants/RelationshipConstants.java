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

package com.ericsson.de.onetwork.dms.constants;

import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;

/**
 * Stores constant values relating to relationships for the Data Modeler
 * Service.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class RelationshipConstants {

    /**
     * {@link Relationships} from the MOM are stored as Parent_Child. This
     * separator is used to extract the parent or the child.
     */
    public static final String RELATIONSHIP_SEPARATOR = "_to_";
    /**
     * {@link Relationship}s from a COM-ECIM MOM can have its parent or child
     * prefixed with a namespace, e.g. namespace:parent or namespace:child.
     */
    public static final String RELATIONSHIP_COM_ECIM_SEPARATOR = ":";
    /**
     * The index of the 'parent' within the list of relationship components.
     */
    public static final int RELATIONSHIP_PARENT_TYPE_INDEX = 0;
    /**
     * The index of the 'child' within the list of relationship components.
     */
    public static final int RELATIONSHIP_CHILD_TYPE_INDEX = 1;
    /**
     * When an {@link Mo} has no parent then an empty String is returned.
     */
    public static final String NO_PARENT_FOUND_INDICATOR = "";
    /**
     * The MOM contains a class called 'ManagedObject'. This is not a valid
     * {@link Mo} instantiation and should not be considered as part of a
     * {@link Relationship}.
     */
    public static final String INVALID_ROOT_MO_TYPE = "ManagedObject";
}
