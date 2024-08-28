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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.de.onetwork.dms.exceptions.ChildNotFoundException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Attribute;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;

/**
 * A {@code CppMo} is an implementation of {@link Mo} interface. It represents
 * an {@code Mo} instance based on CPP platform managed object.
 *
 * @author edalrey
 * @since 1Network_15.12
 */
abstract class AbstractMo implements Mo {

    private final static String INVALID_CHILD_EXCEPTION_MESSAGE = "%s is not a valid child of %s";
    private final static String CHILD_NOT_FOUND_EXCEPTION_MESSAGE = "%s contains no child of type %s";

    /**
     * The Fully Distinguished Name of the {@link Mo}.
     */
    protected Fdn fdn = null;
    /**
     * The managed object representation based on the MOM XML.
     */
    protected Class momMoClass = null;
    /**
     * Indicates if the {@link Mo} must be created automatically.
     */
    private boolean systemCreated = false;

    /**
     * Used to keep track of feature populated attribute names.
     */
    private final List<String> featurePopulatedAttributeNames = new ArrayList<>();

    /**
     * A map keyed by the child {@link Mo}'s type, and containing a list of
     * child {@code Mo}'s of that type.
     */
    private final Map<String, List<Mo>> children = new HashMap<>();
    /**
     * A map keyed by each attribute's name for the {@code Mo}, and containing a
     * {@code GenericDataType}.
     */
    private final Map<String, MoAttributeDataType> attributes = new HashMap<>();

    @Override
    public String getType() {
        return fdn.getType();
    }

    @Override
    public String getName() {
        return fdn.getName();
    }

    @Override
    public Fdn getFdn() {
        return fdn;
    }

    @Override
    public Fdn getParentFdn() {
        return fdn.getParentFdn();
    }

    @Override
    public int getLevel() {
        return fdn.getLength();
    }

    @Override
    public List<String> getChildTypes() {
        return new ArrayList<>(children.keySet());
    }

    @Override
    public void setChildTypes(final List<String> childTypes) {
        for (final String childType : childTypes) {
            children.put(childType, new ArrayList<Mo>());
        }
    }

    @Override
    public List<Mo> getChildren() {
        final List<Mo> childMos = new ArrayList<>();
        for (final String key : children.keySet()) {
            for (final Mo childMo : children.get(key)) {
                if (childMo != null) {
                    childMos.add(childMo);
                }
            }
        }
        return childMos;
    }

    @Override
    public List<Mo> getChildrenByType(final String moType) throws InvalidChildException {
        final List<Mo> childrenByType = children.get(moType);
        if (null == childrenByType) {
            throw new InvalidChildException(String.format(INVALID_CHILD_EXCEPTION_MESSAGE, moType, getFdn().getType()));
        }
        return childrenByType;
    }

    @Override
    public Mo getChildByTypeByName(final String moType, final String name) throws InvalidChildException, ChildNotFoundException {
        final List<Mo> childrenFdnByType = getChildrenByType(moType);
        try {
            for (final Mo childMo : childrenFdnByType) {
                if (childMo.getName().equals(name)) {
                    return childMo;
                }
            }
        } catch (final NullPointerException es) {
            throw new InvalidChildException(String.format(INVALID_CHILD_EXCEPTION_MESSAGE, moType, getFdn().getType()));
        }
        throw new ChildNotFoundException(String.format(CHILD_NOT_FOUND_EXCEPTION_MESSAGE, getFdn(), moType));
    }

    @Override
    public void addChild(final Mo child) {
        final String childType = child.getType();
        final List<Mo> childrenByType = children.get(childType);
        childrenByType.add(child);
    }

    @Override
    public boolean isValidChildMoType(final String childMoType) {
        for (final String type : getChildTypes()) {
            if (type.equals(childMoType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getAttributeNames() {
        return new ArrayList<>(attributes.keySet());
    }

    @Override
    public void setAttributeNames(final List<String> attributeNames) {
        for (final String attributeName : attributeNames) {
            attributes.put(attributeName, null);
        }
    }

    @Override
    public MoAttributeDataType getAttributeByName(final String name) {
        final MoAttributeDataType attribute = attributes.get(name);
        return attribute;
    }

    @Override
    public void setAttributeValue(final String attributeName, final Object value, final CreationContext attributeContext) {
        attributes.put(attributeName, new MoAttributeDataType(getDataTypeFromMomMoClass(attributeName), value));

        if (attributeContext.equals(CreationContext.FEATURE_CREATED)) {
            featurePopulatedAttributeNames.add(attributeName);
        }
    }

    private String getDataTypeFromMomMoClass(final String attributeName) {
        for (final Object momMoAttribute : momMoClass.getActionOrAttribute()) {
            if (momMoAttribute instanceof Attribute) {
                final Attribute attribute = (Attribute) momMoAttribute;
                if (attribute.getName().equals(attributeName)) {
                    final String dataTypeClassName =
                            attribute
                                    .getDataType()
                                    .getIntegerOrBooleanOrOctetOrCharOrDoubleOrFloatOrLongOrLonglongOrShortOrStringOrWstringOrInt8OrInt16OrInt32OrInt64OrUint8OrUint16OrUint32OrUint64OrDecimal64OrEnumRefOrMoRefOrStructRefOrDerivedDataTypeRefOrSequence()
                                    .get(0).getClass().getSimpleName();
                    return dataTypeClassName;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isSystemCreated() {
        return systemCreated;
    }

    @Override
    public void setSystemCreated(final boolean systemCreated) {
        this.systemCreated = systemCreated;
    }

    @Override
    public List<String> getFeaturePopulatedAttributeNames() {
        return featurePopulatedAttributeNames;
    }

}
