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
 * Represents the generic attribute data type for MOs. Possible data types are
 * as following
 * <ul>
 * <li>Struct
 * <li>Enum
 * <li>Array
 * <li>Boolean
 * <li>String
 * <li>Integer
 * <li>Ref
 * <li>Long
 * </ul>
 *
 * @author eaefhiq
 */
public class MoAttributeDataType {

    /**
     * Instantiates a new generic MO attribute data type.
     *
     * @param dataType
     *            the data type for an Managed Object attribute. For example,
     *            String, Struct, etc...
     * @param value
     *            the value of an Managed Object attribute. For example, 1,
     *            "00001", struct Foo{int x; char[] y;}, enum
     *            ActivationVals{ACTIVATED,
     *            DEACTIVATED} etc...
     */
    public MoAttributeDataType(final String dataType, final Object value) {
        super();
        this.dataType = dataType;
        this.value = value;
    }

    /**
     * The data type of a MO attribute. For example: STRUCT, ENUM, STRING, etc..
     */
    private String dataType;

    /**
     * The value. There are three possible data types for a value of Managed
     * Object attribute. They are {@code Map<String,GenericDataType},
     * {@code list<GenericDataType>}, and primitive.
     * <p>{@code Map<String,GenericDataType} presents a Struct or an Enum type
     * on the MOM file.</p>
     * <p>{@code list<GenericDataType>} presents a Sequence type on the MOM
     * File. </p>
     */
    private Object value;

    /**
     * Gets the data type.
     *
     * @return the data type
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType
     *            the new data type
     */
    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    /**
     * Returns the attribute value of an MO as a generic object. Return data
     * types are Map, List or other primitive types: for example: String.
     *
     * @return the attribute value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the attribute value of an MO as a generic object. Setting data types
     * are Map, List or other primitive types: for example: String.
     *
     * @param value
     *            the new attribute value
     */
    public void setValue(final Object value) {
        this.value = value;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value.toString();
    }

}
