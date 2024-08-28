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

package com.ericsson.de.onetwork.gnm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ericsson.de.onetwork.NetworkType;

/**
 * The GNM object defines the basic requirements a Network must satisfy.
 *
 * @author ecasjim
 * @author eaefhiq
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "mimPercentages",
    "networkType",
    "gnmRevision",
    "cellPattern"
})
@XmlRootElement(name = "gnm")
public class Gnm {

    /**
     * Represents the MIM percentages within an instance of a Gnm.
     * MIM percentages define the percentage of a MIM type within a network.
     */
    private Map<String, Double> mimPercentages;

    /**
     * Enum defining the network type.
     */
    private final NetworkType networkType;

    /**
     * The <code>Gnm</code> revision.
     */
    private final String gnmRevision;

    /**
     * Used to determine how many cells are required per
     * <code>NetworkElement</code>.
     */
    private final List<Integer> cellPattern;

    /**
     * Instantiates a new gnm.
     *
     * @param gnmRevision
     *            the gnm revision
     * @param networkType
     *            the network type
     * @param mimPercentages
     *            the mim percentages
     * @param cellPattern
     *            the cell pattern
     */
    public Gnm(final String gnmRevision, final NetworkType networkType, final Map<String, Double> mimPercentages, final List<Integer> cellPattern) {
        this.gnmRevision = gnmRevision;
        this.networkType = networkType;
        this.mimPercentages = mimPercentages;
        this.cellPattern = cellPattern;
    }

    /**
     * Default constructor
     */
    public Gnm() {
        super();
        gnmRevision = "";
        networkType = NetworkType.LTE;
        mimPercentages = new HashMap<String, Double>();
        cellPattern = new ArrayList<Integer>();
    }

    /**
     * Returns the network type.
     *
     * @return network type as an Enum.
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * Returns the Gnm revision.
     *
     * @return Gnm revision as a String.
     */
    public String getGnmRevision() {
        return gnmRevision;
    }

    /**
     * Returns MIM usage percentages for an instance of a Gnm.
     *
     * @return the mimPercentages Map containing MIM percentages in the format
     *         Map<String, Double>
     */
    public Map<String, Double> getMimUsage() {
        return mimPercentages;
    }

    /**
     * @param mimPercentages
     *            the mimPercentages to set
     */
    public void setMimPercentages(final Map<String, Double> mimPercentages) {
        this.mimPercentages = mimPercentages;
    }

    /**
     * Returns the cell pattern as a list of integers.
     *
     * @return the cellPattern list of integers.
     */
    public List<Integer> getCellPattern() {
        return cellPattern;
    }

    /**
     * Returns the cell pattern average.
     *
     * @return average cell pattern as a Double.
     */
    public Double getCellPatternAverage() {
        return new Double(getCellPatternTotal() / cellPattern.size());
    }

    /**
     * Returns the cell pattern total.
     *
     * @return average cell pattern total as a int.
     */
    public int getCellPatternTotal() {
        int totalCellsInPattern = 0;

        for (final Integer cell : cellPattern) {
            totalCellsInPattern += cell;
        }

        return totalCellsInPattern;
    }
}
