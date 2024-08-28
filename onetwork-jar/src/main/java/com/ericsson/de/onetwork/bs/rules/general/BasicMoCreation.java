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

package com.ericsson.de.onetwork.bs.rules.general;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.bs.rules.Rule;
import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleUtility;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidPlatformRuntimeException;
import com.ericsson.de.onetwork.dms.exceptions.MaximumCardinalityExceededException;
import com.ericsson.de.onetwork.dms.generics.CreationContext;
import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.dms.mo.datatype.AttributeDataType;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;

/**
 * General rule is used to create simple MOs across all network types.
 *
 * @author ecasjim
 */
public class BasicMoCreation extends Rule {
    private final static Logger logger = LoggerFactory.getLogger(BasicMoCreation.class);

    /** Rule Config object to be used with this rule */
    private RuleConfig ruleConfig;

    /** Counter for Mos created per cell */
    private int totalMosCreatedPerCell = 1;

    /** List containing cell names of network. */
    private Set<String> nodeNames = new LinkedHashSet<String>();

    /** Number of MOs required to be created in this rule */
    private Integer numberOfMosRequiredPerNe = 0;

    /**
     * Constructor used to set name of rule.
     *
     * @param name
     *            name of rule
     */
    public BasicMoCreation(final String name) {
        super(name);
    }

    /**
     * @see com.ericsson.de.onetwork.bs.rules.Rule#getName()
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * @see com.ericsson.de.onetwork.bs.rules.Rule#applyRule(com.ericsson.de.onetwork
     *      .bs.Network, java.lang.String, com.ericsson.de.onetwork.gnm.Gnm)
     */
    @Override
    public Network apply(final Network network, final RuleConfig ruleConfig) {
        nodeNames = network.getNetworkNodeNames();
        this.ruleConfig = ruleConfig;
        final String moTypeHierarchy = ruleConfig.getMoTypeHierarchy();
        String moValue = null;
        final Collection<NetworkElement> networkElements = network.getNetworkElements();
        final String moType = RuleUtility.getMoTypeToCreate(moTypeHierarchy);
        logger.debug("Attempting to apply rule {} for Mo {} to {} node network", getName(), moType, networkElements.size());

        for (final NetworkElement networkElement : networkElements) {
            logger.debug("Applying rule {},{}", getName(), moTypeHierarchy);
            final List<Mo> parentMoList = RuleUtility.getParentsOfMoForCreation(networkElement, moTypeHierarchy);

            if (parentMoList.isEmpty()) {
                logger.error("Rule {} failed as parent {} of {} do not exist on node: {}.",
                        getName(), RuleUtility.getParentMoType(moTypeHierarchy), moType, networkElement.getName());
            } else if (ruleConfig.isOnlySetAttributes()) {
                setAttributesOnExistingMos(ruleConfig, networkElement);
            } else {
                moValue = createRequiredMos(networkElement, parentMoList, moType, moValue);
                moValue = RuleUtility.prepareMoValueForNextNE(ruleConfig, moValue);
            }
        }

        return network;
    }

    private void setAttributesOnExistingMos(final RuleConfig ruleConfig, final NetworkElement networkElement) {
        List<Mo> moList;
        try {
            moList = networkElement.getMosFromNe(ruleConfig.getMoTypeHierarchy());
            for (final Mo existingMo : moList) {
                setMoAttributes(existingMo, networkElement);
            }
        } catch (final InvalidChildException e) {
            logger.error("Could not set attributes on {}", networkElement.getName());
        }
    }

    private String createRequiredMos(final NetworkElement networkElement, final List<Mo> parentMoList, final String requiredMoType,
            final String lastMoValue) {
        String moValue = null;
        final String nodeMimVersion = networkElement.getMimVersion();
        Integer parentMoIndex = 0;
        moValue = setInitialMoValueForThisNe(lastMoValue, networkElement.getName());
        numberOfMosRequiredPerNe = determineNumOfMosPerNe(networkElement);

        for (int mosCreated = 0; mosCreated < numberOfMosRequiredPerNe; mosCreated++) {
            final Mo parentMo = parentMoList.get(parentMoIndex);

            Mo createdMo = RuleUtility.getMoIfSystemCreated(parentMo, requiredMoType, moValue.toString());

            if (createdMo == null) {
                createdMo = createMo(networkElement, requiredMoType, moValue, nodeMimVersion, parentMo, createdMo);
            }

            setMoAttributes(createdMo, networkElement);
            parentMoIndex = updateParentIndex(parentMoIndex, parentMoList.size(), ruleConfig);

            if (isNotFinishedCreatingMosForNE(mosCreated)) {
                moValue = RuleUtility.determineMoValue(ruleConfig, moValue, new LinkedHashSet<String>(nodeNames), networkElement.getName());
            }
        }

        return moValue;
    }

    private Mo createMo(final NetworkElement networkElement, final String requiredMoType, final String moValue, final String nodeMimVersion,
            final Mo parentMo, Mo createdMo) {
        try {
            createdMo = super.moFactory.getMo(nodeMimVersion, parentMo, requiredMoType, moValue.toString());
            logger.debug("Created Mo {}={} on Mo:{} {}", createdMo.getType(), createdMo.getName(), networkElement.getName(), parentMo.getFdn());
            setMoAttributeId(createdMo);
            parentMo.addChild(createdMo);
        } catch (InvalidPlatformRuntimeException | InvalidChildException | MaximumCardinalityExceededException e) {
            logger.error("Error: {}", e.getMessage());
        }

        return createdMo;
    }

    private boolean isNotFinishedCreatingMosForNE(final int mosCreated) {
        return mosCreated != numberOfMosRequiredPerNe;
    }

    private String setInitialMoValueForThisNe(final String lastMoValue, final String nodeName) {
        if (lastMoValue == null) {
            return RuleUtility.determineMoValue(ruleConfig, lastMoValue, new LinkedHashSet<String>(nodeNames), nodeName);
        } else {
            return lastMoValue;
        }
    }

    private Integer determineNumOfMosPerNe(final NetworkElement networkElement) {
        if (ruleConfig.getNumberOfMosPerNode() != 0) {
            if (ruleConfig.isNumberOfMOsAsAMultipleOfCellsOnNode()) {
                return ruleConfig.getNumberOfMosPerCell() * RuleUtility.getNumberOfCellsOnNode(networkElement);
            } else if (ruleConfig.isExtractValuesFromNetwork() && RuleUtility.isMoATypeOfCell(ruleConfig)) {
                return nodeNames.size() < ruleConfig.getNumberOfMosPerNode() ? nodeNames.size() - 1 : ruleConfig.getNumberOfMosPerNode();
            }
        }
        return ruleConfig.getNumberOfMosPerNode();
    }

    private int updateParentIndex(int parentMoIndex, final int parentMoListSize, final RuleConfig ruleConfig) {
        if (ruleConfig.getNumberOfMosPerCell() > 0 && totalMosCreatedPerCell < ruleConfig.getNumberOfMosPerCell()) {
            totalMosCreatedPerCell++;
        } else {
            parentMoIndex = RuleUtility.updateParentMoIndex(parentMoIndex, parentMoListSize);
            totalMosCreatedPerCell = 1;
        }

        return parentMoIndex;
    }

    private void setMoAttributes(final Mo mo, final NetworkElement networkElement) {
        for (final Map.Entry<String, MoAttributeDataType> entry : ruleConfig.getAttributes().entrySet()) {
            mo.setAttributeValue(entry.getKey(), entry.getValue().getValue(), CreationContext.FEATURE_CREATED);
            logger.debug("Setting {} to {} on Mo {}", entry.getKey(), entry.getValue(), mo.getFdn());
        }

        replacePlaceHoldersWithRequiredValues(mo, networkElement.getName());

        try {
            setAttributesWhichRequireMoRef(mo, networkElement);
        } catch (final InvalidChildException e) {
            logger.error("Error: {}", e.getMessage());
        }
    }

    private void setAttributesWhichRequireMoRef(final Mo mo, final NetworkElement networkElement) throws InvalidChildException {
        if (ruleConfig.isRefToBeSet()) {
            for (final Map.Entry<String, String> entry : ruleConfig.getRefAttrMap().entrySet()) {
                final List<Mo> moList = networkElement.getMosFromNe(entry.getValue());
                for (final Mo moExternal : moList) {
                    if (moExternal.getName().equalsIgnoreCase(mo.getName())) {
                        mo.setAttributeValue(entry.getKey(), moExternal.getFdn(), CreationContext.FEATURE_CREATED);
                        logger.debug("Setting {} to {} on Mo {}", entry.getKey(), moExternal.getFdn(), mo.getFdn());
                    }
                }
            }
        }
    }

    private void replacePlaceHoldersWithRequiredValues(final Mo mo, final String nodeName) {
        final List<String> attrNames = mo.getAttributeNames();

        for (final String attrName : attrNames) {
            final MoAttributeDataType attr = mo.getAttributeByName(attrName);

            if (RuleUtility.isAttributeValid(attr)) {
                findPlaceHolders(attrName, attr, nodeName, mo);
            }
        }
    }

    // TODO: Maybe when nodes are being created, any MOs/Attributes that also
    // need the node name should be created or set.
    private void findPlaceHolders(final String attrName, final MoAttributeDataType attr, final String nodeName, final Mo mo) {
        String replacement = null;

        if (attr.getValue() instanceof MoAttributeDataType) {
            findPlaceHolders(attrName, (MoAttributeDataType) attr.getValue(), nodeName, mo);
        } else if (attr.getDataType().equalsIgnoreCase("StructRef")) {
            final HashMap<?, MoAttributeDataType> structMap = (HashMap<?, MoAttributeDataType>) attr.getValue();

            for (final Entry<?, MoAttributeDataType> entry : structMap.entrySet()) {
                final String value = entry.getValue().toString();
                final String dataType = entry.getValue().getDataType().toString();

                replacement = getPlaceholderReplacementValue(value, attrName, nodeName, mo);

                if (replacement != null) {
                    entry.setValue(new MoAttributeDataType(dataType, replacement));
                    logger.debug("Replaced with {}", replacement);
                }
            }
        } else if (attr.getDataType().equalsIgnoreCase("String")) {
            replacement = getPlaceholderReplacementValue(attr.getValue().toString(), attrName, nodeName, mo);

            if (replacement != null) {
                mo.setAttributeValue(attrName, replacement, CreationContext.FEATURE_CREATED);
                logger.debug("Replaced with {}", replacement);
            }
        }
    }

    private String getPlaceholderReplacementValue(final String attr, final String attrName, final String nodeName, final Mo mo) {
        final String nodeNamePlaceholder = "<NodeName>";
        final String moValuePlaceholder = "<MoValue>";
        String placeholder = null;

        if (attr.contains(nodeNamePlaceholder)) {
            logger.debug("Found {}", attr);
            placeholder = nodeNamePlaceholder;
            return attr.replace(placeholder, nodeName);
        } else if (attr.contains(moValuePlaceholder)) {
            logger.debug("Found {}", attr);
            placeholder = moValuePlaceholder;
            return attr.replace(placeholder, mo.getName());
        }

        return null;
    }

    private void setMoAttributeId(final Mo mo) {
        if (ruleConfig.isSettingAttributeId()) {
            final List<String> attrNames = mo.getAttributeNames();

            for (final String attrName : attrNames) {
                if (attrName.equalsIgnoreCase(mo.getType() + "id")) {
                    final MoAttributeDataType moAttributeDataType = new MoAttributeDataType(AttributeDataType.String.toString(), mo.getName());
                    mo.setAttributeValue(attrName, moAttributeDataType, CreationContext.FEATURE_CREATED);
                    logger.debug("Setting {} to {}", attrName, mo.getName());
                    break;
                }
            }
        }
    }
}
