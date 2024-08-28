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

package com.ericsson.de.onetwork.cs;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gson.Gson;

/**
 * Represents a customer defined network.
 *
 * @author eaefhiq
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "gnmName",
    "size",
    "mimVersion",
    "features"
})
@XmlRootElement(name = "clientDefinedNetwork")
public class ClientDefinedNetwork {

    /** The GNM name. */
    private String gnmName;

    /** The size of the network. */
    private int size;

    /** The size of the network. */
    private String mimVersion;

    /** The features of the network */
    private List<String> features;

    /**
     * Gets the GNM name.
     *
     * @return the GNM name
     */
    public String getGnmName() {
        return gnmName;
    }

    /**
     * Sets the GNM name.
     *
     * @param gnmName
     *            the new GNM name
     */
    public void setGnmName(final String gnmName) {
        this.gnmName = gnmName;
    }

    /**
     * Gets the size of the network.
     *
     * @return the size of the network.
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size of the network.
     *
     * @param size
     *            the new size of the network.
     */
    public void setSize(final int size) {
        this.size = size;
    }

    /**
     * Gets the MIM version of the network element.
     *
     * @return the mimVersion
     */
    public String getMimVersion() {
        return mimVersion;
    }

    /**
     * Sets the MIM version of the network element.
     *
     * @param mimVersion
     *            the mimVersion to set
     */
    public void setMimVersion(final String mimVersion) {
        this.mimVersion = mimVersion;
    }

    /**
     * Gets the features of the network.
     *
     * @return the features of the network.
     */
    public List<String> getFeatures() {
        return features;
    }

    /**
     * Sets the features of the network.
     *
     * @param features
     *            the new features of the network.
     */
    public void setFeatures(final List<String> features) {
        this.features = features;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
