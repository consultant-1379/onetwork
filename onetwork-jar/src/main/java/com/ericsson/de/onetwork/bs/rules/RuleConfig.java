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

import java.util.HashMap;
import java.util.Map;

import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;
import com.ericsson.de.onetwork.gnm.Gnm;

/**
 * Used to configure how a rule will be applied.
 * When a rule is selected for a feature, a RuleConfig object also
 * needs to be configured.
 *
 * @author ecasjim
 */
public class RuleConfig {

    /** Path to mo the rule applies to */
    private String moTypeHierarchy = "";

    /** If Mo name is non numerical, this is used */
    private String moName = null;

    /** Number of Mos per node */
    private int numberOfMosPerNode = 1;

    /** Number of Mos per cell */
    private int numberOfMosPerCell = Integer.MIN_VALUE;

    /** If a Gnm object is required this is true */
    private boolean gnmRequired = false;

    /** Gnm if required */
    private Gnm gnm = null;

    /** Starting value for numerical values */
    private Integer startingValue = 1;

    /** Used when Mos are system created and they don't need to be created */
    private boolean onlySetAttributes = false;

    /** By default, value is incrementing across a node */
    private boolean incrementingValue = true;

    /** If value is not incrementing */
    private boolean constantValue = false;

    /** Attributes types and corresponding values are stored in this map */
    private Map<String, MoAttributeDataType> attributes = new HashMap<String, MoAttributeDataType>();

    /** True when Refs need to be set */
    private boolean refToBeSet = false;

    /**
     * Map containing attribute Ref to be set <key> and mo type hierarchy
     * <value>
     */
    private Map<String, String> refAttrMap = null;

    /** Max value an Mo can have <value> */
    private int moMaxValue = Integer.MAX_VALUE;

    /** True when mo increments across network */
    private boolean incrementAcrossNetwork = false;

    /** True if values need to be extracted from network */
    private boolean extractValuesFromNetwork = false;

    /** Mo Type Hierarchy To Extract from network */
    private String moTypeHierarchyToExtract = null;

    /**
     * True when the number of Mos to create are dependent on the number of
     * cells on a node
     */
    private boolean numberOfMOsAsAMultipleOfCells = false;

    /** Determines if id attribute of an mo should be set */
    private boolean settingAttributeId = true;

    /**
     * Most basic constructor where Mo name defaults to one and only one is
     * created per node.
     *
     * @param moTypeHierarchy
     *            comma separated Path to mo with only types
     */
    public RuleConfig(final String moTypeHierarchy) {
        this.moTypeHierarchy = moTypeHierarchy;
    }

    /**
     * Used for creation of single Mo with non-incrementing name.
     *
     * @param moTypeHierarchy
     *            comma separated Path to mo with only types
     * @param moName
     *            mo name
     */
    public RuleConfig(final String moTypeHierarchy, final String moName) {
        this(moTypeHierarchy);
        this.moName = moName;
        incrementingValue = false;
    }

    /**
     * Used when number of Mos per node is greater than one.
     *
     * @param moTypeHierarchy
     *            comma separated Path to mo with only types
     * @param numberOfMosPerNode
     *            number of Mos per node to be created
     */
    public RuleConfig(final String moTypeHierarchy, final Integer numberOfMosPerNode) {
        this(moTypeHierarchy);
        this.numberOfMosPerNode = numberOfMosPerNode;
    }

    /**
     * Get the Mo Type Hierarchy
     *
     * @return the moTypeHierarchy
     */
    public String getMoTypeHierarchy() {
        return moTypeHierarchy;
    }

    /**
     * Set the Mo Type Hierarchy as comma separated Mo types.
     *
     * @param moTypeHierarchy
     *            the moTypeHierarchy to set
     */
    public void setMoTypeHierarchy(final String moTypeHierarchy) {
        this.moTypeHierarchy = moTypeHierarchy;
    }

    /**
     * Get Mo name.
     *
     * @return the moName
     */
    public String getMoName() {
        return moName;
    }

    /**
     * Set the Mo name.
     *
     * @param moName
     *            the moName to set
     */
    public void setMoName(final String moName) {
        this.moName = moName;
    }

    /**
     * Get the number of Mos to be created per node.
     *
     * @return the numberOfMosPerNode
     */
    public int getNumberOfMosPerNode() {
        return numberOfMosPerNode;
    }

    /**
     * Set the number of MOs to be created per node.
     *
     * @param numberOfMosPerNode
     *            the numberOfMosPerNode to set
     */
    public void setNumberOfMosPerNode(final int numberOfMosPerNode) {
        this.numberOfMosPerNode = numberOfMosPerNode;
    }

    /**
     * Is the GNM required for this rule.
     *
     * @return the gnmRequired
     */
    public boolean isGnmRequired() {
        return gnmRequired;
    }

    /**
     * Set whether the GNM is required for the rule.
     *
     * @param gnmRequired
     *            the gnmRequired to set
     */
    public void setGnmRequired(final boolean gnmRequired) {
        this.gnmRequired = gnmRequired;
    }

    /**
     * Get the GNM assigned to this rule.
     *
     * @return the gnm
     */
    public Gnm getGnm() {
        return gnm;
    }

    /**
     * Set the GNM of this rule.
     *
     * @param gnm
     *            the gnm to set
     */
    public void setGnm(final Gnm gnm) {
        this.gnm = gnm;
    }

    /**
     * Get the starting value for this Mo.
     *
     * @return the startingValue
     */
    public Integer getStartingValue() {
        return startingValue;
    }

    /**
     * Set the starting value for this Mo.
     *
     * @param startingValue
     *            the startingValue to set
     */
    public void setStartingValue(final int startingValue) {
        this.startingValue = startingValue;
    }

    /**
     * Determine if no Mos are being created and only attributes are being set
     * on this Mo.
     *
     * @return the onlySetAttributes
     */
    public boolean isOnlySetAttributes() {
        return onlySetAttributes;
    }

    /**
     * Define that no Mos are being created and only attributes are being set on
     * this Mo.
     *
     * @param onlySetAttributes
     *            the onlySetAttributes to set
     */
    public void setOnlySetAttributes(final boolean onlySetAttributes) {
        this.onlySetAttributes = onlySetAttributes;
    }

    /**
     * Determine if the Mo is incrementing only across a Node.
     *
     * @return the incrementingValue
     */
    public boolean isIncrementingAcrossNode() {
        return incrementingValue;
    }

    /**
     * Set that that Mo is only incrementing across a Node.
     *
     * @param incrementingValue
     *            the incrementingValue to set
     */
    public void setIncrementingAcrossNode(final boolean incrementingValue) {
        this.incrementingValue = incrementingValue;

        if (incrementingValue) {
            setConstantValue(false);
            setIncrementingAcrossNetwork(false);
        }
    }

    /**
     * Determine if Mo has a constant value.
     *
     * @return the constantValue
     */
    public boolean isConstantValue() {
        return constantValue;
    }

    /**
     * Set that Mo is a constant value.
     *
     * @param constantValue
     *            the constantValue to set
     */
    public void setConstantValue(final boolean constantValue) {
        this.constantValue = constantValue;
    }

    /**
     * Get attribute map of Mo rule.
     *
     * @return the attributes
     */
    public Map<String, MoAttributeDataType> getAttributes() {
        return attributes;
    }

    /**
     * Set attribute map of Mo rule.
     *
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(final Map<String, MoAttributeDataType> attributes) {
        this.attributes = attributes;
    }

    /**
     * Determine if Mo Refs need to be set.
     *
     * @return true when Refs need to be set
     */
    public boolean isRefToBeSet() {
        return refToBeSet;
    }

    /**
     * Set that Mo Refs need to be set.
     *
     * @param refToBeSet
     *            true when Refs need to be set
     */
    public void setRefToBeSet(final boolean refToBeSet) {
        this.refToBeSet = refToBeSet;
    }

    /**
     * Get attribute refs map.
     *
     * @return the refAttrMap
     */
    public Map<String, String> getRefAttrMap() {
        return refAttrMap;
    }

    /**
     * Set attribute refs map.
     *
     * @param refAttrMap
     *            the refAttrMap to set
     */
    public void setRefAttrMap(final Map<String, String> refAttrMap) {
        this.refAttrMap = refAttrMap;
    }

    /**
     * Set max value of an Mo that can be set on each cell.
     *
     * @param maxValue
     *            max value of Mo
     */
    public void setMaxValuePerCell(final int maxValue) {
        moMaxValue = maxValue;
    }

    /**
     * Get max value of an Mo that can be set on each cell.
     *
     * @return max value of Mo
     */
    public int getMaxValuePerCell() {
        return moMaxValue;
    }

    /**
     * Get number of Mos to be created per cell.
     *
     * @return the numberOfMosPerCell
     */
    public int getNumberOfMosPerCell() {
        return numberOfMosPerCell;
    }

    /**
     * Set the number of Mos to be created per cell.
     *
     * @param numberOfMosPerCell
     *            the numberOfMosPerCell to set
     */
    public void setNumberOfMosPerCell(final int numberOfMosPerCell) {
        this.numberOfMosPerCell = numberOfMosPerCell;
    }

    /**
     * Returns true if the Mo value continues to increment across the network.
     *
     * @return the incrementAcrossNetwork boolean
     */
    public boolean isIncrementingAcrossNetwork() {
        return incrementAcrossNetwork;
    }

    /**
     * Set true if the Mo value should continue increment across the network.
     *
     * @param incrementAcrossNetwork
     *            the incrementAcrossNetwork to set
     */
    public void setIncrementingAcrossNetwork(final boolean incrementAcrossNetwork) {
        this.incrementAcrossNetwork = incrementAcrossNetwork;

        if (incrementAcrossNetwork) {
            setIncrementingAcrossNode(false);
        }
    }

    /**
     * Set true when values need to be extracted from the network.
     *
     * @param seeting
     *            boolean
     */
    public void setExtractValuesFromNetwork(final boolean extractValuesFromNetwork) {
        this.extractValuesFromNetwork = extractValuesFromNetwork;
        setIncrementingAcrossNode(false);
        setIncrementingAcrossNetwork(false);
    }

    /**
     * Returns true when cell names need to be extracted from the network.
     *
     * @param seeting
     *            boolean
     */
    public boolean isExtractValuesFromNetwork() {
        return extractValuesFromNetwork;
    }

    /**
     * Set Mo Type Hierarchy of Mo values to extract from network.
     *
     * @param moTypeHierarchyToExtract
     *            Mo Type Hierarchy
     */
    public void setMoTypeHierarchyToExtractForMoValues(final String moTypeHierarchyToExtract) {
        this.moTypeHierarchyToExtract = moTypeHierarchyToExtract;
    }

    /**
     * Set Mo Type Hierarchy of Mo values to extract from network.
     *
     * @param string
     *            Mo Type Hierarchy
     */
    public String getMoTypeHierarchyToExtractForMoValues() {
        return moTypeHierarchyToExtract;
    }

    /**
     * Set that the number of Mos to create are dependent on the number of cells
     * on a node.
     *
     * @param numberOfMOsAsAMultipleOfCells
     *            boolean setting
     */
    public void setNumberOfMOsAsAMultipleOfCellsOnNode(final boolean numberOfMOsAsAMultipleOfCells) {
        this.numberOfMOsAsAMultipleOfCells = numberOfMOsAsAMultipleOfCells;

    }

    /**
     * Returns true if the number of Mos to create are dependent on the number
     * of cells on a node.
     *
     * @return boolean setting
     */
    public boolean isNumberOfMOsAsAMultipleOfCellsOnNode() {
        return numberOfMOsAsAMultipleOfCells;
    }

    /**
     * Sets whether attribute id of this mo should be automatically set.
     *
     * @param setting
     *            true if it needs to be set
     */
    public void setAttributeIdSetting(final boolean setting) {
        settingAttributeId = setting;
    }

    /**
     * Determines if the id attribute of this mo should be automatically set.
     *
     * @return returns true when it needs to be set
     */
    public boolean isSettingAttributeId() {
        return settingAttributeId;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (attributes == null ? 0 : attributes.hashCode());
        result = prime * result + (constantValue ? 1231 : 1237);
        result = prime * result + (gnm == null ? 0 : gnm.hashCode());
        result = prime * result + (gnmRequired ? 1231 : 1237);
        result = prime * result + (incrementingValue ? 1231 : 1237);
        result = prime * result + (moName == null ? 0 : moName.hashCode());
        result = prime * result + (moTypeHierarchy == null ? 0 : moTypeHierarchy.hashCode());
        result = prime * result + numberOfMosPerNode;
        result = prime * result + (onlySetAttributes ? 1231 : 1237);
        result = prime * result + startingValue;
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RuleConfig other = (RuleConfig) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (constantValue != other.constantValue) {
            return false;
        }
        if (gnm == null) {
            if (other.gnm != null) {
                return false;
            }
        } else if (!gnm.equals(other.gnm)) {
            return false;
        }
        if (gnmRequired != other.gnmRequired) {
            return false;
        }
        if (incrementingValue != other.incrementingValue) {
            return false;
        }
        if (moName == null) {
            if (other.moName != null) {
                return false;
            }
        } else if (!moName.equals(other.moName)) {
            return false;
        }
        if (moTypeHierarchy == null) {
            if (other.moTypeHierarchy != null) {
                return false;
            }
        } else if (!moTypeHierarchy.equals(other.moTypeHierarchy)) {
            return false;
        }
        if (numberOfMosPerNode != other.numberOfMosPerNode) {
            return false;
        }
        if (onlySetAttributes != other.onlySetAttributes) {
            return false;
        }
        if (startingValue != other.startingValue) {
            return false;
        }
        return true;
    }
}
