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

package com.ericsson.de.onetwork.bs.rules.lte;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.ericsson.de.onetwork.gnm.Gnm;

/**
 * This <code>Rule</code> creates EUtranCellFDD/TDD/Pico/Micro cells in the LTE
 * network.
 *
 * @author ecasjim
 */
public class EUtranCellCreation extends Rule {

    private final static Logger logger = LoggerFactory.getLogger(EUtranCellCreation.class);
    private final static String DEFAULT_ATTRIBUTE_VALUE = "1";

    /** Cell pattern extracted from the GNM. */
    private List<Integer> cellPattern;

    /** Used to navigate through cell pattern array. */
    private int cellPatternCounter = 0;

    private final String[] attributeNames = { "tac", "physicalLayerSubCellId",
        "physicalLayerCellIdGroup", "earfcnul", "earfcndl" };

    /**
     * Constructor takes the name of the rule.
     *
     * @param name
     *            rule name
     */
    public EUtranCellCreation(final String name) {
        super(name);
    }

    /*
     * @see com.ericsson.de.onetwork.bs.rules.Rule#getName()
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /*
     * @see
     * com.ericsson.de.onetwork.bs.rules.Rule#applyRule(com.ericsson.de.onetwork
     * .bs.Network, com.ericsson.de.onetwork.dms.generics.Fdn)
     */
    @Override
    public Network apply(final Network network, final RuleConfig config) {
        final String moTypeHierarchy = config.getMoTypeHierarchy();
        final Gnm gnm = config.getGnm();
        final int networkNodeSize = network.getNetworkElements().size();
        final int networkCellSize = (int) (Math.floor(gnm.getCellPatternAverage()) * networkNodeSize);

        logger.info("{} cells will be implemented across a {} node network.", networkCellSize, networkNodeSize);

        cellPattern = gnm.getCellPattern();
        final Collection<NetworkElement> networkElements = network.getNetworkElements();

        for (final NetworkElement networkElement : networkElements) {
            final List<Mo> parentMoList = RuleUtility.getParentsOfMoForCreation(networkElement, moTypeHierarchy);

            final List<String> cellNames =
                    createRequiredMOs(networkElement.getMimVersion(), networkElement.getName(),
                            parentMoList, RuleUtility.getMoTypeToCreate(moTypeHierarchy));

            network.addNetworkCellNames(cellNames);
        }

        return network;
    }

    private List<String> createRequiredMOs(final String nodeMimVersion, final String nodeName, final List<Mo> parentMoList,
            final String requiredMoType) {
        final List<String> cellNames = new ArrayList<String>();
        final int cellsRequired = numberOfCellsRequiredForThisNetworkElement();
        int parentMoIndex = 0;
        int cellsCreated = 0;

        while (cellsCreated < cellsRequired) {
            final Mo parentMo = parentMoList.get(parentMoIndex);
            final int cellNamePostfix = cellsCreated + 1;
            final String cellName = nodeName + "-" + cellNamePostfix;

            createAndSetMo(nodeMimVersion, parentMo, requiredMoType, cellName);

            parentMoIndex = RuleUtility.updateParentMoIndex(parentMoIndex, parentMoList.size());
            cellsCreated++;
            cellNames.add(cellName);
        }

        return cellNames;
    }

    private void createAndSetMo(final String nodeMimVersion, final Mo parentMo, final String requiredMoType, final String cellName) {

        Mo createdMo;
        try {
            createdMo = super.moFactory.getMo(nodeMimVersion, parentMo, requiredMoType, cellName);
            setMandatoryAttributes(createdMo, cellName);
            parentMo.addChild(createdMo);
            logger.debug("Created Mo {} : {}", createdMo.getType(), createdMo.getName());
        } catch (InvalidPlatformRuntimeException | InvalidChildException | MaximumCardinalityExceededException e) {
            logger.error("Error: {}", e.getMessage());
        }
    }

    private void setMandatoryAttributes(final Mo mo, final String cellName) {
        final String managedObjectId = mo.getType() + "Id";
        final String cellId = cellName.split("-")[cellName.split("-").length - 1];

        mo.setAttributeValue(managedObjectId, cellName, CreationContext.FEATURE_CREATED);
        logger.debug("Setting {} to {}", managedObjectId, cellName);

        mo.setAttributeValue("cellId", cellId, CreationContext.FEATURE_CREATED);
        logger.debug("Setting cellId to {}", cellId);

        for (final String attributeName : attributeNames) {
            mo.setAttributeValue(attributeName, DEFAULT_ATTRIBUTE_VALUE, CreationContext.FEATURE_CREATED);
            logger.debug("Setting {} to {}", attributeName, DEFAULT_ATTRIBUTE_VALUE);
        }

    }

    private int numberOfCellsRequiredForThisNetworkElement() {
        if (cellPatternCounter == cellPattern.size()) {
            cellPatternCounter = 0;
        }

        return cellPattern.get(cellPatternCounter++);
    }
}
