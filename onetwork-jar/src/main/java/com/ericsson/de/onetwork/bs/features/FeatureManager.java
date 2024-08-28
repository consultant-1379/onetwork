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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.NetworkType;
import com.ericsson.de.onetwork.bs.rules.RuleMap;
import com.ericsson.de.onetwork.bs.rules.data.CellPattern;
import com.ericsson.de.onetwork.bs.rules.data.EUtranRelations;
import com.ericsson.de.onetwork.bs.rules.data.ExternalUtraNetwork;
import com.ericsson.de.onetwork.bs.rules.data.HardwareItem;
import com.ericsson.de.onetwork.bs.rules.data.SHM;
import com.ericsson.de.onetwork.bs.rules.data.UtranRelations;

/**
 * Used to store and access all the available features supported by 1Network.
 *
 * @author ecasjim
 */
@Path("/featuremanager")
public class FeatureManager {

    private final static Logger logger = LoggerFactory
            .getLogger(FeatureManager.class.getName());

    /**
     * Map used to stored feature objects in memory.
     * Map Key is the feature name as a String and the Value is the
     * <code>FeatureModule</code>.
     */
    private final Map<String, FeatureModule> featureMap = new HashMap<String, FeatureModule>();

    /**
     * Constructor calls this method to populate the map storing all the
     * supported features.
     */
    public FeatureManager() {
        populateFeatureModulesMap();
    }

    /**
     * Returns the requested feature module object.
     *
     * @param featureModuleName
     *            name of feature module required.
     * @return FeatureModule object.
     */
    public FeatureModule getFeatureModule(final String featureModuleName) {
        return featureMap.get(featureModuleName);
    }

    /**
     * Returns all features.
     *
     * @return list of features supported
     */
    @GET
    @Path("/features")
    @Produces({ "application/json" })
    public List<FeatureModule> getAllFeatures() {
        final List<FeatureModule> features = new ArrayList<FeatureModule>();

        for (final Entry<String, FeatureModule> entry : featureMap.entrySet())
        {
            features.add(featureMap.get(entry.getValue().getName()));
        }

        return features;
    }

    /**
     * Returns a list of the features which are supported for a network type.
     *
     * @param networkType
     *            network type in question
     * @return list of features supported
     */
    @GET
    @Path("/features/{networkType}")
    @Produces({ "application/json" })
    public List<FeatureModule> getFeatureListForNetworkType(@PathParam("networkType") final String networkType) {
        final List<FeatureModule> featuresForNetworkType = new ArrayList<FeatureModule>();

        final NetworkType networkTypeEnum = NetworkType.valueOf(networkType.toUpperCase());

        for (final Entry<String, FeatureModule> entry : featureMap.entrySet())
        {
            final FeatureModule feature = entry.getValue();
            if (feature.getNetworkType().equals(networkTypeEnum)) {
                featuresForNetworkType.add(featureMap.get(entry.getValue().getName()));
            }
        }

        return featuresForNetworkType;
    }

    /*
     * TODO: This method will be used to populate the map of feature modules from a DB.
     * TODO: Feature names should be ENUMs
     */
    private void populateFeatureModulesMap() {
        // LTE features
        final FeatureModule cellPatternAssignmentFeature = new FeatureModule("Cell Pattern Assignment", CellPattern.getRules());
        cellPatternAssignmentFeature.setNetworkType(NetworkType.LTE);
        featureMap.put(cellPatternAssignmentFeature.getName(), cellPatternAssignmentFeature);

        final FeatureModule SHMFeature = new FeatureModule("SHM", SHM.getRules());
        SHMFeature.setNetworkType(NetworkType.LTE);
        featureMap.put(SHMFeature.getName(), SHMFeature);

        final FeatureModule EUtranFeature = new FeatureModule("EUtran Support", EUtranRelations.getRules());
        EUtranFeature.setNetworkType(NetworkType.LTE);
        EUtranFeature.addDependency(cellPatternAssignmentFeature.getName());
        featureMap.put(EUtranFeature.getName(), EUtranFeature);

        final RuleMap utraRuleMap = ExternalUtraNetwork.getRules();
        utraRuleMap.addAdditionalRules(UtranRelations.getRules().getMap());
        final FeatureModule utranFeature = new FeatureModule("Utra Support", utraRuleMap);
        utranFeature.setNetworkType(NetworkType.LTE);
        utranFeature.addDependency(cellPatternAssignmentFeature.getName());

        featureMap.put(utranFeature.getName(), utranFeature);

        // CORE feature
        final FeatureModule hardwareItemSupportFeature = new FeatureModule("Hardware Item Support", HardwareItem.getRules());
        hardwareItemSupportFeature.setNetworkType(NetworkType.CORE);
        featureMap.put(hardwareItemSupportFeature.getName(), hardwareItemSupportFeature);
    }

    public static List<FeatureModule> orderFeaturesAllowingForDependencies(final List<FeatureModule> featureModules) {
        logger.debug("Received features in this order: ");
        logFeatureRequestInfo(featureModules);

        Collections.sort(featureModules);

        logger.debug("Applying features in new order: ");
        logFeatureRequestInfo(featureModules);
        return featureModules;
    }

    private static void logFeatureRequestInfo(final List<FeatureModule> featureModules) {
        for (int i = 0; i < featureModules.size(); i++) {
            logger.debug("{} : {}", i + 1, featureModules.get(i).getName());
        }
    }
}
