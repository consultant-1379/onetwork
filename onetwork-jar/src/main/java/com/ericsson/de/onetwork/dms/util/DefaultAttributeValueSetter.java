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

package com.ericsson.de.onetwork.dms.util;

import java.util.Collection;

import com.ericsson.de.onetwork.dms.MimDataStorage;
import com.ericsson.de.onetwork.dms.generics.CreationContext;
import com.ericsson.de.onetwork.dms.generics.Fdn;
import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Attribute;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Char;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Decimal64;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DefaultValue;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataTypeRef;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.EnumRef;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Int16;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Int32;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Int64;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Int8;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Longlong;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Octet;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Sequence;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Uint16;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Uint32;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Uint64;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Uint8;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Wstring;

/**
 * A class which sets the default values for each attribute of an {@link Mo}.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class DefaultAttributeValueSetter {

    /**
     * Sets the default values for attributes on a {@link Mo} based on the data
     * contained in the corresponding instance of {@link Class}. This data is
     * ultimately derived from the underlying MOM XML.
     *
     * @param mimVersion
     *            the version of the underlying node model
     * @param managedObject
     *            the {@code Mo} that default attribute values are set on
     */
    public static void setAllDefaultAttributes(final String mimVersion, final Mo managedObject) {

        final Fdn moFdn = managedObject.getFdn();
        final String moType = moFdn.getType();
        final Class moClass = MimDataStorage.getClassByMoType(mimVersion, moType);

        final Collection<String> attributes = managedObject.getAttributeNames();
        for (final String attribute : attributes) {
            final DefaultValue defaultValue = getDefaultValue(attribute, moClass);
            final String defaultAttriuteValue = getMimDefinedDefaultAttributeValuesIfTheyExist(defaultValue);
            managedObject.setAttributeValue(attribute, defaultAttriuteValue, CreationContext.SYSTEM_CREATED);
        }
    }

    private static String getMimDefinedDefaultAttributeValuesIfTheyExist(final DefaultValue defaultAttribute) {
        if (null != defaultAttribute) {
            return defaultAttribute.getvalue();
        }
        return null;
    }

    private static DefaultValue getDefaultValue(final String attributeName, final Class moClass) {
        DefaultValue defaultValue = null;
        for (final Object possibleAttribute : moClass.getActionOrAttribute()) {
            if (possibleAttribute instanceof Attribute) {

                final Attribute attribute = (Attribute) possibleAttribute;
                if (attribute.getName().equals(attributeName)) {
                    final Object topLevelDataType =
                            attribute
                                    .getDataType()
                                    .getIntegerOrBooleanOrOctetOrCharOrDoubleOrFloatOrLongOrLonglongOrShortOrStringOrWstringOrInt8OrInt16OrInt32OrInt64OrUint8OrUint16OrUint32OrUint64OrDecimal64OrEnumRefOrMoRefOrStructRefOrDerivedDataTypeRefOrSequence()
                                    .get(0);

                    if (topLevelDataType instanceof Sequence) {
                        defaultValue = ifTopLevelIsSequence(topLevelDataType);
                    } else {
                        defaultValue = selectDefault(topLevelDataType);
                    }
                    if (null == defaultValue) {
                        defaultValue = null;
                        // TODO: Use the MomDefaultValue Enum here.
                        // defaultValue = MomDefaultValue.valueOf(topLevelName);
                    }
                    break;
                }
            }
        }
        return defaultValue;
    }

    private static DefaultValue ifTopLevelIsSequence(final Object topLevelDataType) {
        final Object secondLevelDataType =
                ((Sequence) topLevelDataType)
                        .getIntegerOrBooleanOrOctetOrCharOrDoubleOrFloatOrLongOrLonglongOrShortOrStringOrWstringOrInt8OrInt16OrInt32OrInt64OrUint8OrUint16OrUint32OrUint64OrDecimal64OrEnumRefOrMoRefOrStructRefOrDerivedDataTypeRef()
                        .get(0);
        final DefaultValue defaultValue = selectDefault(secondLevelDataType);
        return defaultValue;

    }

    private static DefaultValue selectDefault(final Object defaultValueContainer) {

        final String nameOfDataTypeOfDefault = defaultValueContainer.getClass().getSimpleName().toLowerCase();
        DefaultValue defaultValue = null;

        switch (nameOfDataTypeOfDefault) {
            case "boolean":
                defaultValue = ((com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Boolean) defaultValueContainer).getDefaultValue();
                break;
            case "octet":
                defaultValue = ((Octet) defaultValueContainer).getDefaultValue();
                break;
            case "char":
                defaultValue = ((Char) defaultValueContainer).getDefaultValue();
                break;
            case "double":
                defaultValue = ((com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Double) defaultValueContainer).getDefaultValue();
                break;
            case "float":
                defaultValue = ((com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Float) defaultValueContainer).getDefaultValue();
                break;
            case "long":
                defaultValue = ((com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Long) defaultValueContainer).getDefaultValue();
                break;
            case "longlong":
                defaultValue = ((Longlong) defaultValueContainer).getDefaultValue();
                break;
            case "short":
                defaultValue = ((com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Short) defaultValueContainer).getDefaultValue();
                break;
            case "string":
                defaultValue = ((com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.String) defaultValueContainer).getDefaultValue();
                break;
            case "wstring":
                defaultValue = ((Wstring) defaultValueContainer).getDefaultValue();
                break;
            case "int8":
                defaultValue = ((Int8) defaultValueContainer).getDefaultValue();
                break;
            case "int16":
                defaultValue = ((Int16) defaultValueContainer).getDefaultValue();
                break;
            case "int32":
                defaultValue = ((Int32) defaultValueContainer).getDefaultValue();
                break;
            case "int64":
                defaultValue = ((Int64) defaultValueContainer).getDefaultValue();
                break;
            case "uint8":
                defaultValue = ((Uint8) defaultValueContainer).getDefaultValue();
                break;
            case "uint16":
                defaultValue = ((Uint16) defaultValueContainer).getDefaultValue();
                break;
            case "uint32":
                defaultValue = ((Uint32) defaultValueContainer).getDefaultValue();
                break;
            case "uint64":
                defaultValue = ((Uint64) defaultValueContainer).getDefaultValue();
                break;
            case "decimal64":
                defaultValue = ((Decimal64) defaultValueContainer).getDefaultValue();
                break;
            case "enumref":
                defaultValue = ((EnumRef) defaultValueContainer).getDefaultValue();
                break;
            case "deriveddatatypeRef":
                defaultValue = ((DerivedDataTypeRef) defaultValueContainer).getDefaultValue();
                break;
            // TODO: Needs to be implemented.
            // case "moRef":
            // def = ((MoRef) obj).getDefaultValue();
            // break;
            // case "structRef":
            // def = ((StructRef) obj).getDefaultValue();
            // break;
            // case "sequence":
            // def = ((Sequence) obj).getDefaultValue();
            // break;
            default:
                break;
        }
        return defaultValue;
    }
}
