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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.de.onetwork.dms.momparser.predicate.ArtifactPredicate;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataType;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Enum;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.InterMim;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Mim;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Models;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Struct;
import com.ericsson.de.onetwork.dms.util.MoFormatter;

/**
 * The {@code ArtifactMerger} contains static methods to combine artifacts
 * (Class, Struct, Enum, Relationship) across multiple {@link Mim} and
 * {@link InterMim} instances from a given {@link Models}.
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class ArtifactMerger {

    /**
     * Merges each {@link Class} artifact to the mimClasses collection.
     *
     * @param mims
     *            the collection of MIM elements from the MOM model.
     */
    public static Map<String, Class> mergeAllClassArtifacts(final Collection<Mim> mims) {
        final HashMap<String, Class> mimClasses = new HashMap<>();
        for (final Mim mim : mims) {
            mimClasses.putAll(getMergedClassesFromMim(mim));
        }
        return mimClasses;
    }

    private static Map<String, Class> getMergedClassesFromMim(final Mim mim) {
        final HashMap<String, Class> returnedClasses = new HashMap<>();

        final Collection<Class> classArtifacts = getClassArtifactsFromMim(mim);
        for (final Class classArtifact : classArtifacts) {
            final String fullClassName = classArtifact.getName();
            final String classNameWithoutNamespace = MoFormatter.removeComEcimNamespaceFrom(fullClassName);
            returnedClasses.put(classNameWithoutNamespace, classArtifact);
        }

        return returnedClasses;
    }

    private static Collection<Class> getClassArtifactsFromMim(final Mim mim) {
        return MomParserUtil.<Class>ruleFilter(mim.getClazz(), new ArtifactPredicate<Class>(Class.class));
    }

    /**
     * Merges each {@link Struct}, {@link Enum} and {@link DerivedDataType}
     * artifact to the mergedMimAttributeSpecifications collection. These
     * three artifacts can come under the name of AttributeSpecifications as
     * they specify the nature of an attribute.
     *
     * @param mims
     *            the collection of MIM elements from the MOM model.
     */
    public static Collection<Object> mergeAllAttributeSpecificationArtifacts(final Collection<Mim> mims) {
        final Collection<Object> mergedAttributeSpecifications = new ArrayList<Object>();
        for (final Mim mim : mims) {
            mergedAttributeSpecifications.addAll(getMergedAttributeSpecificationsFromMim(mim));

        }
        return mergedAttributeSpecifications;

    }

    private static Collection<Object> getMergedAttributeSpecificationsFromMim(final Mim mim) {
        final Collection<Object> returnedAttributeSpecifications = new ArrayList<>();

        final List<Object> possibleAttributeSpecifications = mim.getStructOrEnumOrExceptionOrDerivedDataType();
        for (final Object possibleAttributeSpecification : possibleAttributeSpecifications) {
            if (isAttributeSpecification(possibleAttributeSpecification)) {
                returnedAttributeSpecifications.add(possibleAttributeSpecification);
            }
        }

        return returnedAttributeSpecifications;

    }

    private static boolean isAttributeSpecification(final Object possibleAttributeSpecification) {
        return possibleAttributeSpecification instanceof Struct
                || possibleAttributeSpecification instanceof Enum
                || possibleAttributeSpecification instanceof DerivedDataType;

    }

    /**
     * Merges each {@link Relationship} artifact to the allMimRelationsips
     * collection.
     *
     * @param mims
     *            the collection of MIM elements from the MOM model.
     * @param interMims
     *            the collection of {@link InterMim} elements from the MOM
     *            model.
     */
    public static Map<String, Relationship> mergeAllRelationshipArtifacts(final Collection<Mim> mims, final Collection<InterMim> interMims) {
        final HashMap<String, Relationship> allMimRelationsips = new HashMap<>();
        allMimRelationsips.putAll(mergeMimRelationships(mims));
        allMimRelationsips.putAll(mergeInterMimRelationships(interMims));
        return allMimRelationsips;
    }

    private static Map<String, Relationship> mergeMimRelationships(final Collection<Mim> mims) {
        final HashMap<String, Relationship> mimRelationships = new HashMap<>();
        for (final Mim mim : mims) {
            mimRelationships.putAll(getValidRelationshipsFromMim(mim));
        }
        return mimRelationships;
    }

    private static Map<String, Relationship> mergeInterMimRelationships(final Collection<InterMim> interMims) {
        final HashMap<String, Relationship> interMimRelationships = new HashMap<>();
        for (final InterMim interMim : interMims) {
            interMimRelationships.putAll(getValidRelationshipsFromInterMim(interMim));
        }
        return interMimRelationships;
    }

    private static Map<String, Relationship> getValidRelationshipsFromMim(final Mim mim) {
        return extractValidRelationships(mim.getRelationship());
    }

    private static Map<String, Relationship> getValidRelationshipsFromInterMim(final InterMim interMim) {
        return extractValidRelationships(interMim.getRelationship());
    }

    private static Map<String, Relationship> extractValidRelationships(final Collection<Relationship> relationships) {
        final HashMap<String, Relationship> mimRelationships = new HashMap<>();
        for (final Relationship relationship : relationships) {
            if (isRelationshipValid(relationship)) {
                final String relationshipKey = MoFormatter.removeComEcimNamespaceFrom(relationship.getName());
                mimRelationships.put(relationshipKey, relationship);
            }
        }
        return mimRelationships;
    }

    private static boolean isRelationshipValid(final Relationship mimRelationship) {
        for (final Object possibleInvalidation : mimRelationship.getPreliminaryOrDeprecatedOrObsolete()) {
            // TODO: Check if Obsolete use case is invalid, similar to
            // Deprecated.
            if (possibleInvalidation instanceof Deprecated) {
                return false;
            }
        }
        return true;
    }

}
