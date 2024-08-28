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
import java.util.List;

import com.ericsson.de.onetwork.dms.MimDataStorage;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Child;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Containment;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Max;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Min;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;

/**
 * A class which can be used to check the minimum or maximum cardinality of an
 * MO.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class CardinalityAssessor {

    /**
     * If the MOM does not contain a max/min relationship cardinality, then a
     * default value is returned and later processed.
     */
    private static final long NO_CARDINALITY_DEFINED_INDICATOR = 0;

    /**
     * Returns the minimum number of {@link Mo} objects of a specified moType
     * that can be created underneath the relevant parent {@code Mo}.
     *
     * @param relationship
     *            an mp.dtd {@link relationship} defined by a MOM.xml
     * @return the minimum number of children of type {@code moType} that can be
     *         added underneath a given parent
     */
    public static long getMinCardinality(final Relationship relationship) {
        final List<Object> minOrMaxObjects = getMinAndMaxObjects(relationship);
        for (final Object minOrMaxObject : minOrMaxObjects) {
            if (minOrMaxObject instanceof Min) {
                final Min min = (Min) minOrMaxObject;
                return Long.parseLong(min.getvalue());
            }
        }
        return NO_CARDINALITY_DEFINED_INDICATOR;
    }

    /**
     * Returns the maximum number of {@link Mo} objects of a specified moType
     * that can be created underneath the relevant parent {@code Mo}.
     *
     * @param relationship
     *            an mp.dtd {@link relationship} defined by a MOM.xml
     * @return the maximum number of children of type {@code moType} that can be
     *         added underneath a given parent
     */
    public static long getMaxCardinality(final Relationship relationship) {
        final List<Object> minOrMaxObjects = getMinAndMaxObjects(relationship);
        for (final Object minOrMaxObject : minOrMaxObjects) {
            if (minOrMaxObject instanceof Max) {
                final Max max = (Max) minOrMaxObject;
                return Long.parseLong(max.getvalue());
            }
        }
        return NO_CARDINALITY_DEFINED_INDICATOR;
    }

    private static List<Object> getMinAndMaxObjects(final Relationship relationship) {
        final Containment containment = getRelationshipContainmentForMo(relationship);
        final Child onlyChildInRelationshipContainment = containment.getChild().get(0);
        final List<Object> minAndMaxObjects = onlyChildInRelationshipContainment.getCardinality().getMinOrMax();
        return minAndMaxObjects;
    }

    private static Containment getRelationshipContainmentForMo(final Relationship relationship) {
        final List<Object> possibleContainments = relationship.getBiDirectionalAssociationOrUniDirectionalAssociationOrContainmentOrInheritance();
        for (final Object possibleContainment : possibleContainments) {
            if (possibleContainment instanceof Containment) {
                return (Containment) possibleContainment;
            }
        }
        throw new IllegalStateException("Could not find containment for relationship " + relationship.toString());
    }

    /**
     * Returns a boolean value based on whether the maximum cardinality has been
     * exceeded for specific {@link Mo} type on a given parent {@code Mo}.
     *
     * @param mimVersion
     *            the version of the underlying node model
     * @param moType
     *            the type of managed object
     * @param parentMo
     *            the parent {@link Mo}
     * @return a boolean that indicates if the maximum cardinality has been
     *         exceeded
     * @throws InvalidChildException
     *             thrown when the requested {@code Mo} type is not a child of
     *             the parent {@code Mo}
     */
    public static boolean isMaxCardinalityExceeded(final String moType, final Mo parentMo) throws InvalidChildException {
        final Relationship relationship = MimDataStorage.getRelatonshipByParentAndChildMoTypes(parentMo.getType(), moType);
        final long maxCardinality = getMaxCardinality(relationship);
        if (NO_CARDINALITY_DEFINED_INDICATOR == maxCardinality) {
            return false;
        }
        final Collection<Mo> childrenByType = parentMo.getChildrenByType(moType);
        final long currentNumberOfChildrenByType = childrenByType.size();
        return currentNumberOfChildrenByType >= maxCardinality;
    }
}
