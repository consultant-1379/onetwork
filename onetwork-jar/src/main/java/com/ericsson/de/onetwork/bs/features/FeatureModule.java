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

package com.ericsson.de.onetwork.bs.features;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ericsson.de.onetwork.NetworkType;
import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleMap;

/**
 * A FeatureModule defines all the MOs and associated rules required to
 * apply that feature to a network.
 *
 * @author ecasjim
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "networkType",
    "ruleMap",
    "featureDependencies"
})
@XmlRootElement(name = "features")
public class FeatureModule implements Comparable<FeatureModule> {

    /** Name of the <code>FeatureModule</code>. */
    private final String name;

    /**
     * <code>RuleMap</code> object which defines what MOs and <code>Rule</code>
     * objects make up this Feature.
     */
    private final RuleMap ruleMap;

    /** Network type this feature is applicable to */
    private NetworkType networkType;

    /**
     * Features which need to be in place before this feature can be applied to
     * the network
     */
    private final List<String> featureDependencies = new ArrayList<String>();

    /**
     * Default constructor
     */
    public FeatureModule() {
        super();
        ruleMap = new RuleMap(new LinkedHashMap<String, RuleConfig>());
        name = "";
    }

    /**
     * Feature Module used to define a feature of a network.
     *
     * @param name
     *            name of the feature
     * @param ruleMap
     *            map of rules that need to implemented for a feature
     */
    public FeatureModule(final String name, final RuleMap ruleMap) {
        this.name = name;
        this.ruleMap = ruleMap;
    }

    /**
     * Used to get the name of the Feature Module.
     *
     * @return the Feature Module Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Used to retrieve the rule map of a Feature Module.
     *
     * @return RuleMap object
     */
    public RuleMap getRules() {
        return ruleMap;
    }

    /**
     * Used to set the network type this feature is applicable to.
     *
     * @param networkType
     *            network type enum
     */
    public void setNetworkType(final NetworkType networkType) {
        this.networkType = networkType;
    }

    /**
     * Used to get the network type of this feature.
     *
     * @return network type enum
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * Used to add a dependency to this feature.
     *
     * @param featureName
     *            dependency features name
     */
    public void addDependency(final String featureName) {
        featureDependencies.add(featureName);
    }

    /**
     * Returns a list of features which this feature is dependent on.
     *
     * @return list of feature names
     */
    public List<String> getFeatureDependencies() {
        return featureDependencies;
    }

    /**
     * Used to order a collection of Features so that all dependencies of a
     * feature are applied to a network beforehand.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final FeatureModule featureModule) {
        for (final String featureName : featureModule.getFeatureDependencies()) {
            if (getName().equalsIgnoreCase(featureName)) {
                return -1;
            }
        }
        return 1;
    }

    /* (non-Javadoc)
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
        if (!(obj instanceof FeatureModule)) {
            return false;
        }
        final FeatureModule other = (FeatureModule) obj;
        if (featureDependencies == null) {
            if (other.featureDependencies != null) {
                return false;
            }
        } else if (!featureDependencies.equals(other.featureDependencies)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (networkType != other.networkType) {
            return false;
        }
        if (ruleMap == null) {
            if (other.ruleMap != null) {
                return false;
            }
        } else if (!ruleMap.equals(other.ruleMap)) {
            return false;
        }
        return true;
    }
}
