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

package com.ericsson.de.onetwork.bs.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.dms.exceptions.ChildNotFoundException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;

/**
 * Utility class for use by rules when creating MOs.
 *
 * @author ecasjim
 */
public class RuleUtility {

    private final static Logger logger = LoggerFactory
            .getLogger(RuleUtility.class);

    /**
     * Used to extract the MO type for creation from the mo type hierarchy
     * string.
     */
    private static final int OFFSET_OF_REQUIRED_MO = 1;

    /**
     * Used to extract the parent of the MO type for creation from the MO type
     * hierarchy string.
     */
    private static final int OFFSET_OF_PARENT_OF_REQUIRED_MO = 2;

    /**
     * Used to get all the parent MOs required when creating the MOs of a
     * feature. In order to create an MO, its parent MO must be known unless it
     * is the root MO.
     *
     * @param networkElement
     *            network element object to search
     * @param moTypeHierarchy
     *            mo type hierarchy used to isolate MOs in question
     * @return list of parent MOs
     */
    public static List<Mo> getParentsOfMoForCreation(final NetworkElement networkElement, final String moTypeHierarchy) {
        final String[] parentMoHierarchy = getMoTypeHierarchyAsArray(moTypeHierarchy);
        int level = 0;

        final Mo rootMo = networkElement.getRootMo();

        if (isAtRootMo(parentMoHierarchy, level, rootMo)) {
            level++;
        }

        final List<Mo> moList = getChildrenByType(rootMo, parentMoHierarchy[level]);
        return traverseToRequiredParentMos(moList, removeTopParentMo(parentMoHierarchy));
    }

    private static List<Mo> traverseToRequiredParentMos(List<Mo> moList, final String[] parentMos) {
        for (int i = 0; i < parentMos.length - OFFSET_OF_PARENT_OF_REQUIRED_MO; i++) {
            moList = getChildMosFromList(moList, parentMos[i + OFFSET_OF_REQUIRED_MO]);
        }

        return moList;
    }

    private static List<Mo> getChildMosFromList(final List<Mo> moList, final String childMoType) {
        final List<Mo> foundMos = new ArrayList<Mo>();

        for (final Mo mo : moList) {
            foundMos.addAll(getChildrenByType(mo, childMoType));
        }

        return foundMos;
    }

    private static String[] removeTopParentMo(final String[] parentMoHierarchy) {
        final int lastElementIndex = parentMoHierarchy.length;
        return Arrays.copyOfRange(parentMoHierarchy, 1, lastElementIndex);
    }

    /**
     * Extracts the MO type we are trying to create from the mo type hierarchy.
     *
     * @param moTypeHierarchy
     *            MO type hierarchy
     * @return MO type
     */
    public static String getMoTypeToCreate(final String moTypeHierarchy) {
        final String[] parentMos = getMoTypeHierarchyAsArray(moTypeHierarchy);
        return parentMos[parentMos.length - OFFSET_OF_REQUIRED_MO];
    }

    /**
     * Extracts the MO type of the parent we are trying to create from the MO
     * type hierarchy.
     *
     * @param moTypeHierarchy
     *            MO type hierarchy
     * @return MO type of the parent MO
     */
    public static String getParentMoType(final String moTypeHierarchy) {
        final String[] parentMos = getMoTypeHierarchyAsArray(moTypeHierarchy);
        return parentMos[parentMos.length - OFFSET_OF_PARENT_OF_REQUIRED_MO];
    }

    /**
     * Converts the mo type hierarchy String into a String array.
     *
     * @param moTypeHierarchy
     *            mo type hierarchy String
     * @return mo type hierarchy String array
     */
    public static String[] getMoTypeHierarchyAsArray(final String moTypeHierarchy) {
        return moTypeHierarchy.split(",");
    }

    /**
     * Wrapper method used to encapsulate the try-catch needed for using the
     * getChildrenByType method of the Mo class.
     *
     * @param mo
     *            MO object
     * @param type
     *            type of MO
     * @return list of children MOs of this type
     */
    public static List<Mo> getChildrenByType(final Mo mo, final String type) {
        List<Mo> childMoList = new ArrayList<Mo>();

        try {
            childMoList = mo.getChildrenByType(type);
        } catch (final InvalidChildException e) {
            logger.error(e.getMessage());
        }

        return childMoList;
    }

    /**
     * Used to determine if the MOs being searched for have been found at this
     * level of the network element.
     *
     * @param childMoList
     *            list of MOs returned from this level of network element
     * @param requiredMo
     *            type of the MO which is being searched for
     * @return boolean representing if searched for MO type has been found or
     *         not
     */
    public static boolean requiredMosNotFoundAtThisLevelOfNe(final List<Mo> childMoList, final String requiredMo) {
        return !childMoList.isEmpty() && !childMoList.get(0).getType().equalsIgnoreCase(requiredMo);
    }

    /**
     * Returns MOs of only one type from a list of MOs.
     *
     * @param moList
     *            list of MOs
     * @param moType
     *            Mo type
     * @return list of MOs of the type searched for
     */
    public static List<Mo> getPopulatedMoTypesFromList(final List<Mo> moList, final String moType) {
        final List<Mo> requiredMOs = new ArrayList<Mo>();

        for (final Mo mo : moList) {
            if (mo.getType().equalsIgnoreCase(moType)) {
                requiredMOs.add(mo);
            }
        }

        return requiredMOs;
    }

    /**
     * When the number of parent MOs are less than the number of child MOs which
     * need to be created, this method is used so that parent MOs can be reused
     * and have multiple children created under them.
     *
     * @param parentMoIndex
     *            current index within parent MO list
     * @param parentMoListSize
     *            size of parent MO list.
     * @return new index for parent MO list
     */
    public static int updateParentMoIndex(int parentMoIndex, final int parentMoListSize) {
        parentMoIndex++;

        if (parentMoIndex == parentMoListSize) {
            parentMoIndex = 0;
        }

        return parentMoIndex;
    }

    /**
     * Used to replace the rule names within the map with the rule objects.
     * These rule objects are retrieved from the rule manager.
     *
     * @param ruleManager
     *            rule manager
     * @param ruleDataWithRuleNames
     *            map with rule names
     * @return map with rule objects
     */
    public static Map<RuleConfig, Rule>
            getRuleObjectsFromRuleNames(final RuleManager ruleManager, final Map<String, RuleConfig> ruleDataWithRuleNames) {
        final Set<String> ruleNames = ruleDataWithRuleNames.keySet();
        final Map<RuleConfig, Rule> ruleDataWithRuleObjects = new LinkedHashMap<RuleConfig, Rule>();

        for (final String ruleName : ruleNames) {
            ruleDataWithRuleObjects.put(ruleDataWithRuleNames.get(ruleName), ruleManager.getRule(ruleName));
        }

        return ruleDataWithRuleObjects;
    }

    /**
     * Used to determine if required Mo is system created already.
     *
     * @param parentMo
     *            parent mo
     * @param requiredMoType
     *            required mo type
     * @return the required mo if present else null
     */
    public static Mo getMoIfSystemCreated(final Mo parentMo, final String requiredMoType, final String moValue) {
        try {
            final Mo mo = parentMo.getChildByTypeByName(requiredMoType, moValue);
            logger.debug("Mo {} : {} was system created.", mo.getType(), mo.getName());
            return mo;
        } catch (InvalidChildException | ChildNotFoundException e) {
            return null;
        }
    }

    private static boolean isAtRootMo(final String[] parentMos, final int level, final Mo rootMo) {
        return rootMo.getType().equalsIgnoreCase(parentMos[level]);
    }

    /**
     * Used to determine what value to set to an Mo.
     *
     * @param ruleConfig
     *            rule config currently being used
     * @param lastMoValue
     *            value the last Mo was set to (if any)
     * @param nodeNames
     *            set of node names currently in the network
     * @param nodeName
     *            name of the current network element
     * @return value to set Mo to
     */
    public static String determineMoValue(final RuleConfig ruleConfig, final String lastMoValue, final Set<String> nodeNames, final String nodeName) {
        if (ruleConfig.isIncrementingAcrossNode()) {
            if (lastMoValue == null || Integer.parseInt(lastMoValue) == ruleConfig.getMaxValuePerCell()) {
                return ruleConfig.getStartingValue().toString();
            }
            return incrementString(lastMoValue);
        } else if (ruleConfig.isIncrementingAcrossNetwork()) {
            if (lastMoValue != null) {
                return incrementString(lastMoValue);
            } else {
                return ruleConfig.getStartingValue().toString();
            }
        } else if (ruleConfig.isExtractValuesFromNetwork()) {
            final int numberOfMoNamesRequired = ruleConfig.getNumberOfMosPerNode();
            final List<String> extractedNodeNames = extractRequiredNodeNames(nodeNames, numberOfMoNamesRequired, nodeName);

            if (lastMoValue == null) {
                return extractedNodeNames.get(0);
            } else {
                for (int i = 0; i < extractedNodeNames.size(); i++) {
                    if (extractedNodeNames.get(i).equalsIgnoreCase(lastMoValue) && i + 1 < extractedNodeNames.size()) {
                        return extractedNodeNames.get(i + 1);
                    }
                }
            }
        }

        return ruleConfig.getMoName();
    }

    private static List<String> extractRequiredNodeNames(final Set<String> nodeNamesSet, int numberOfMoNamesRequired,
            final String nodeName) {
        int newMinIndex = 0;
        final int nodeNameIndex = findNodeNamePositionAndRemoveFromSet(nodeNamesSet, nodeName);
        final int halfRange = (int) Math.floor(numberOfMoNamesRequired / 2);

        if (nodeNamesSet.size() < numberOfMoNamesRequired) {
            numberOfMoNamesRequired = nodeNamesSet.size();
        }

        if (nodeNameIndex >= halfRange) {
            newMinIndex = nodeNameIndex - halfRange;
        }

        final Object[] nodeNameObjArr = Arrays.copyOfRange(nodeNamesSet.toArray(), newMinIndex, newMinIndex + numberOfMoNamesRequired);
        final String[] nodeNameStrArr = Arrays.copyOf(nodeNameObjArr, nodeNameObjArr.length, String[].class);
        return new ArrayList<String>(Arrays.asList(nodeNameStrArr));

    }

    private static int findNodeNamePositionAndRemoveFromSet(final Set<String> nodeNamesSet, final String nodeName) {
        int counter = 0;
        int foundIndex = 0;

        for (final String setNodeName : nodeNamesSet) {
            if (setNodeName.equalsIgnoreCase(nodeName)) {
                foundIndex = counter;
                nodeNamesSet.remove(setNodeName);
                break;
            }
            counter++;
        }

        return foundIndex;
    }

    private static String incrementString(final String value) {
        Integer moValue = Integer.parseInt(value);
        moValue++;
        return moValue.toString();
    }

    /**
     * Determines if a MoAttributeDataType object is correctly instantiated.
     *
     * @param MoAttributeDataType
     *            object in question
     * @return true if correctly instantiated
     */
    public static boolean isAttributeValid(final MoAttributeDataType attr) {
        if (attr == null || attr.getValue() == "" || attr.getValue() == null
                || attr.getDataType() == null || attr.getDataType() == "") {
            return false;
        }
        return true;
    }

    /**
     * Counts the number of cells on a node.
     *
     * @param networkElement
     *            a network element
     * @return number of mos
     */
    public static int getNumberOfCellsOnNode(final NetworkElement networkElement) {
        int totalTDD = 0, totalFDD = 0;
        try {
            // If the number of MOs to create are based on number of
            // cells on a node, the cell type needs to be added here.
            totalTDD = networkElement.getMosFromNe("ManagedElement,ENodeBFunction,EUtranCellTDD").size();
            totalFDD = networkElement.getMosFromNe("ManagedElement,ENodeBFunction,EUtranCellFDD").size();
        } catch (final InvalidChildException e) {
            logger.error("Cound not count Cells on {}", networkElement.getName());
        }

        return totalTDD + totalFDD;
    }

    /**
     * Determines if rule config is configured for a cell.
     *
     * @param ruleConfig
     *            rule config object.
     * @return true if rule config is configured for a cell
     */
    public static boolean isMoATypeOfCell(final RuleConfig ruleConfig) {
        return ruleConfig.getMoTypeHierarchyToExtractForMoValues().contains("FDD")
                || ruleConfig.getMoTypeHierarchyToExtractForMoValues().contains("TDD");
    }

    /**
     * Determines the value of the first Mo on the next NE.
     *
     * @param ruleConfig
     *            rule config object
     * @param lastMoValue
     *            last Mo value if any
     * @return Mo value for next Mo
     */
    public static String prepareMoValueForNextNE(final RuleConfig ruleConfig, String lastMoValue) {
        if (ruleConfig.isIncrementingAcrossNode() || ruleConfig.isExtractValuesFromNetwork()) {
            lastMoValue = null;
        }
        return lastMoValue;
    }
}
