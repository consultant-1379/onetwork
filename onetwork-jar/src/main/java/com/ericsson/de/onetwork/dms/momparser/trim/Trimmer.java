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

package com.ericsson.de.onetwork.dms.momparser.trim;

/**
 * Trims the unused elements in an XML file. For example, removes unnecessary
 * elements
 * from the AccessControlList.xml file before using it.
 *
 * @author eaefhiq
 */
public interface Trimmer {

    /**
     * Trims unused elements from the MOM file.
     */
    void trim();
}
