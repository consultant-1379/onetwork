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

import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataType;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Enum;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Struct;

/**
 * A factory for creating instances of {@code Trimmer} objects.
 *
 * @author eaefhiq
 */
public class TrimmerFactory {

    /**
     * Creates an instance of a Trimmer object.
     *
     * @param objectToBeTrimmed
     *            the object to be trimmed
     * @return the trimmer
     */
    public static Trimmer getTrimmer(final Object objectToBeTrimmed) {
        switch (objectToBeTrimmed.getClass().getName()) {
            case "com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class":
                return new ClassTrimmer((Class) objectToBeTrimmed);
            case "com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Enum":
                return new EnumTrimmer((Enum) objectToBeTrimmed);
            case "com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Struct":
                return new StructTrimmer((Struct) objectToBeTrimmed);
            case "com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataType;":
                return new DerivedDataTypeTrimmer((DerivedDataType) objectToBeTrimmed);
            default:
                break;
        }
        return null;
    }
}
