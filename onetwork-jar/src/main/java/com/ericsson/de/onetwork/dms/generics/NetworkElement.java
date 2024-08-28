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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.bs.rules.RuleUtility;
import com.ericsson.de.onetwork.dms.MimDataStorage;
import com.ericsson.de.onetwork.dms.exceptions.DataModellerServiceException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidPlatformRuntimeException;
import com.ericsson.de.onetwork.dms.exceptions.MaximumCardinalityExceededException;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;
import com.ericsson.de.onetwork.dms.util.CardinalityAssessor;
import com.ericsson.de.onetwork.dms.util.MimToPlatformMapper;
import com.ericsson.de.onetwork.dms.util.MoFormatter;

/**
 * The {@code NetworkElement} object is a collection of {@code Mo} instances
 * based on a given MIM version. The {@code Mo}s are stored in a map that is
 * keyed by the {@code Fdn} of the {@code Mo}.
 *
 * @author edalrey
 * @since 1Network_15.12
 */
public class NetworkElement {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The version of the {@code NetworkElement}/ MIM used to build node,
     * e.g.LTE ERBS F1100-V4.
     */
    private final String mimVersion;
    /**
     * The unique name of the node e.g ERBS10000.
     */
    private final String name;
    /**
     * The lower level {@code Mo} on top of which all other {@code Mo}s are
     * added.
     */
    private Mo rootMo;
    /**
     * A utility used to generated an {@code Mo} instance based on an input MOM.
     * This is used for the autogeneration of SystemCreated {@code Mo}s.
     */
    private final MoFactory moFactory;

    /**
     * Initialises a newly created {@code NetworkElement} object that represents
     * the node on which the {@code Mo} objects.
     * <p>
     * Note: The mimVersion parameter should be used to configure the
     * {@code MoGenerator}, which will map to a MOM XML.
     *
     * @param mimVersion
     *            the type MOM xml that is used to create {@code Mo} objects on
     *            this {@code NetworkElement}
     * @param name
     *            the name of the @ NetworkElement}
     * @param moFactory
     *            a utility used to generate {@code Mo} objects
     */
    public NetworkElement(final String mimVersion, final String name) {
        this.mimVersion = mimVersion;
        this.name = name;
        moFactory = new MoFactory(true);
        try {
            initialiseRootMo();
            populateSystemCreatedMos(rootMo);
        } catch (final DataModellerServiceException customException) {
            if (customException instanceof InvalidChildException || customException instanceof MaximumCardinalityExceededException) {
                logger.error("Node ({}) is invalid. {}", name, customException.getMessage());
            } else {
                logger.error(customException.getMessage());
            }
        }
    }

    private void initialiseRootMo() throws DataModellerServiceException {
        MimDataStorage.loadMimVersionToMemory(mimVersion);
        final Class rootClass = MimDataStorage.getRootMoClass(mimVersion);
        final String moType = MoFormatter.removeComEcimNamespaceFrom(rootClass.getName());
        rootMo = moFactory.getMo(mimVersion, null, moType, getRootName(name));
        rootMo.setSystemCreated(true);
    }

    private String getRootName(final String name) {
        String rootName = name;
        final Platform currentPlatform = MimToPlatformMapper.getPlatformByMimVersion(mimVersion);
        if (Platform.CPP.equals(currentPlatform)) {
            rootName = "1";
        }
        if (Platform.COMECIM.equals(currentPlatform)) {
            rootName = name;
        }
        return rootName;
    }

    private void populateSystemCreatedMos(final Mo parentMo) throws InvalidPlatformRuntimeException, InvalidChildException,
            MaximumCardinalityExceededException {
        for (final String childType : parentMo.getChildTypes()) {
            if (MimDataStorage.isMoSystemCreated(mimVersion, childType)) {
                final long minCardinality = getMinCardinality(parentMo, childType);
                for (int moCounter = 1; moCounter <= minCardinality; moCounter++) {
                    final Mo managedObject = moFactory.getMo(mimVersion, parentMo, childType, Integer.toString(moCounter));
                    managedObject.setSystemCreated(true);
                    parentMo.addChild(managedObject);
                    populateSystemCreatedMos(managedObject);
                }
            }
        }
    }

    private long getMinCardinality(final Mo parentMo, final String childType) {
        final Relationship relationship = MimDataStorage.getRelatonshipByParentAndChildMoTypes(parentMo.getType(), childType);
        final long minCardinality = CardinalityAssessor.getMinCardinality(relationship);
        // This is a unique case where the Mo is both systemCreated and has a 0
        // minimum cardinality.
        // System creating here that at least 1 instance must be created.
        return Math.max(minCardinality, 1);
    }

    /**
     * Returns the root level {@link Mo} within the {@link NetworkElement}. All
     * other {@code Mo}'s in the node sit below the root level.
     *
     * @return the root level {@code Mo} within the {@code NetworkElement}.
     */
    public Mo getRootMo() {
        return rootMo;
    }

    /**
     * Returns the version of the underlying MIM.
     *
     * @return the version of the underlying MIM
     */
    public String getMimVersion() {
        return mimVersion;
    }

    /**
     * Returns the name of the {@link NetworkElement}.
     *
     * @return the name of the {@link NetworkElement}
     */
    public String getName() {
        return name;
    }

    /**
     * Used to get mos of a certain type which are within a network element.
     *
     * @param moTypeHierarchy
     *            moTypeHierarchy representing the mos which need to be obtained
     * @return list of mos
     * @throws InvalidChildException
     *             thrown when the moTypeHierarchy represents a parent-child
     *             relationship which doesn't exist
     */
    public List<Mo> getMosFromNe(final String moTypeHierarchy) throws InvalidChildException {
        final String[] parentMoTypes = RuleUtility.getMoTypeHierarchyAsArray(moTypeHierarchy);
        return getRequiredMosRecursively(getRootMo().getChildren(), parentMoTypes, 1);
    }

    private List<Mo> getRequiredMosRecursively(final List<Mo> parentList, final String[] parentMoTypes, int level)
            throws InvalidChildException {
        List<Mo> childList = new ArrayList<Mo>();

        for (final Mo mo : parentList) {
            if (mo.getType().equalsIgnoreCase(parentMoTypes[level])) {
                childList.addAll(mo.getChildrenByType(parentMoTypes[level + 1]));
            }
        }

        if (level < parentMoTypes.length - 2) {
            childList = getRequiredMosRecursively(childList, parentMoTypes, ++level);
        }

        return childList;
    }

}
