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

package com.ericsson.de.onetwork.dms.momparser;

import java.util.Collection;
import java.util.Map;

import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataType;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Enum;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Struct;

/**
 * Common interface to extract artifacts from a MOM file.
 * Four artifacts (ENUM, ClASS, RELATIONSHIP and STRUCT) are used by
 * 1Network. The MomParser interface stores these artifacts into four
 * different collections. The MomParser interface exports these artifacts.
 *
 * @author eaefhiq
 */
public interface MomParser {

    /**
     * Exports all the artifacts (ENUM, ClASS, RELATIONSHIP and STRUCT) to XML
     * files.
     */
    void exportToXml();

    /**
     * Gets the MIM classes from a MOM file.
     *
     * @return a map of {@Code Mim} classes.
     */
    Map<String, Class> getMimClasses();

    /**
     * Gets the MIM enums from a MOM file.
     *
     * @return a collection of {@Code Mim} enums
     */
    Collection<Enum> getMimEnums();

    /**
     * Gets the MIM structs from a MOM file.
     *
     * @return a collection of {@Code Mim} structs
     */
    Collection<Struct> getMimStructs();

    /**
     * Gets the MIM derivedDataTypes from a MOM file.
     *
     * @return a collection of {@Code Mim} derivedDataTypes
     */
    Collection<DerivedDataType> getMimDerivedDataTypes();

    /**
     * Gets the MIM relationships from a MOM file.
     *
     * @return a map of {@Code Mim} relationships
     */
    Map<String, Relationship> getMimRelationships();
}
