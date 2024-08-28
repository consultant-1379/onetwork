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

import java.util.List;

import com.ericsson.de.onetwork.dms.exceptions.ChildNotFoundException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;

/**
 * Interface for representing Managed Objects. {@code Mo} provides a set of
 * methods to interact with the Managed Object's children, and the Managed
 * Objects's attributes.
 *
 * @author edalrey
 * @since 1Network_15.11
 */
public interface Mo {

    /**
     * Returns the type of Managed Object for this {@link Mo} as a string. For
     * example "ManagedElement".
     *
     * @return the ManagedObject type of this {@code Mo}
     */
    String getType();

    /**
     * Returns the name of the ManagedObject for this {@link Mo}. For example
     * "LTE01ERBS00001".
     *
     * @return the name of this {@code Mo}
     */
    String getName();

    /**
     * Returns the full distinguished name (FDN) for this {@link Mo}. For
     * example in CPP "ManagedElement=LTE01ERBS00001,ENodeBFunction=1".
     *
     * @return the {@link Fdn} of this {@code Mo}
     */
    Fdn getFdn();

    /**
     * Returns the full distinguished name (FDN) for the parent {@link Mo} of
     * this {@code Mo}. For example an {@code Mo} that is of type
     * ENodeBFunction can have a parent Mo "ManagedElement=LTE01ERBS00001".
     *
     * @return the {@link Fdn} of the parent {@code Mo} to this {@code Mo}
     *         instance
     */
    Fdn getParentFdn();

    /**
     * Returns the level at which this {@link Mo} sits. This is the number of
     * {@code Mo}'s below the root {@code Mo} (inclusive) where this {@code Mo}
     * is located. For example an {@code Mo} with {@link Fdn}
     * "ManagedElement=LTE01ERBS00001,ENodeBFunction=1" has level 2.
     *
     * @return the level at which this {@code Mo} sits
     */
    int getLevel();

    /**
     * Returns a list containing all possible child {@link Mo} types that sit
     * under this {@code Mo}.
     *
     * @return a list containing all possible child {@code Mo} types that sit
     *         under this {@code Mo}.
     */
    List<String> getChildTypes();

    /**
     * Sets the current list of child types to a newly specified list of child
     * types.
     *
     * @param childTypes
     *            a list defining the child types for this {@link Mo}
     */
    void setChildTypes(List<String> childTypes);

    /**
     * Returns a list of children {@link Mo}s.
     *
     * @return a list of children {@code Mo}s
     */
    List<Mo> getChildren();

    /**
     * Returns a list of children {@link Mo}s that are of a specified type.
     *
     * @param moType
     *            the type of the requested {@code Mo}'s
     * @return a list of children {@link Mo}s that are of a specified type.
     * @throws InvalidChildException
     *             when moType does not match any available children {@code Mo}
     *             's.
     */
    List<Mo> getChildrenByType(String moType) throws InvalidChildException;

    /**
     * Returns a child {@link Mo} with a specified type and name. For example
     * "ManagedElement=LTE01ERBS00001,ENodeBFunction=1".
     *
     * @param moType
     *            the type of the requested {@code Mo}
     * @param name
     *            the name of the requested {@code Mo}
     * @return a child {@code Mo} with a specified type and name
     * @throws ChildNotFoundException
     *             when {@code Mo} has not instnaces of the valid child type,
     *             named moType
     * @throws InvalidChildException
     *             when it is not possible for {@code Mo} to contain children of
     *             type moType
     */
    Mo getChildByTypeByName(String moType, String name) throws InvalidChildException, ChildNotFoundException;

    /**
     * Checks whether an {@code Mo} specified by a given moType is a valid child
     * of this {@code Mo}.
     *
     * @param childMoType
     *            the {@code Mo} type of the possible child
     * @return true if child exists
     */
    boolean isValidChildMoType(String childMoType);

    /**
     * Adds a child {@link Mo} to the list of children {@code Mo}'s of this
     * {@code Mo} instance.
     *
     * @param child
     *            {@code Mo} to be added to the list of children {@code Mo}'s
     */
    void addChild(Mo child);

    /**
     * Returns a list of all possible attribute names of this {@link Mo}.
     *
     * @return a list of all possible attribute names of this {@code Mo}
     */
    List<String> getAttributeNames();

    /**
     * Sets the attribute names for this {@link Mo}.
     *
     * @param attributeNames
     *            a list defining the attribute names for this {@code Mo}
     */
    void setAttributeNames(List<String> attributeNames);

    /**
     * Returns the attribute specified by a given name.
     *
     * @param name
     *            the name of the attribute
     * @return the attribute
     */
    MoAttributeDataType getAttributeByName(String name);

    /**
     * Sets the value of an attribute specified by a given name to a newly
     * assigned value.
     *
     * @param name
     *            the attribute being set
     * @param value
     *            the value to set this attribute to
     * @param attributeContext
     *            defines if an attribute is system or feature created.
     */
    void setAttributeValue(String name, Object value, CreationContext attributeContext);

    /**
     * Returns the system created status of the {@link Mo}.
     *
     * @return the system created status of the {@code Mo}
     */
    boolean isSystemCreated();

    /**
     * Sets the system created status of the {@link Mo}.
     *
     * @param systemCreated
     *            the system created status of the {@code Mo}
     */
    void setSystemCreated(final boolean systemCreated);

    /**
     * Returns a list of attribute names which have been set by features.
     *
     * @return list of attribute names.
     */
    List<String> getFeaturePopulatedAttributeNames();

}
