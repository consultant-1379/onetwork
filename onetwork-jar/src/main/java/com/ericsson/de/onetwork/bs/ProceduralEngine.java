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

package com.ericsson.de.onetwork.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.bs.features.FeatureManager;
import com.ericsson.de.onetwork.bs.features.FeatureModule;
import com.ericsson.de.onetwork.bs.rules.Rule;
import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleManager;
import com.ericsson.de.onetwork.bs.rules.RuleMap;
import com.ericsson.de.onetwork.bs.rules.RuleUtility;
import com.ericsson.de.onetwork.dms.generics.MoFactory;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.gnm.Gnm;

/**
 * The ProceduralEngine class provides network build and user defined features
 * application functionalities.
 * <p>
 * A empty Network object will be created and then each Network Element of the
 * network will be created.
 * Each Feature Module will be then broken down into its MO Descriptions.
 * </p> <p>
 * With these MO Descriptions, the Procedural Engine will request the MOs from
 * the
 * MoFactory and then apply the rules to these MOs as required.
 * </p>
 *
 * @author ecasjim
 */
public class ProceduralEngine {

    private final static Logger logger = LoggerFactory
            .getLogger(ProceduralEngine.class.getName());

    /** Used for retrieving Rule objects. */
    private final RuleManager ruleManager;

    /** Used for retrieving MO objects. */
    private final MoFactory moFactory = new MoFactory(true);

    /** Defines the network. */
    private Gnm gnm;

    /** Used for retrieving MIM types for NEs as defined by GNM. */
    private MimSelector mimSelector;

    /** Used for naming NEs. */
    private NameGenerator nameGenerator;

    /**
     * The constructor initialises the rule manager and passes the MO Factory to
     * it. The rule manager gives all requested rules access to the MO Factory
     * so that each rule can request the MOs required to satisfy a rule.
     */
    public ProceduralEngine() {
        ruleManager = new RuleManager(moFactory);
    }

    /**
     * Builds the network as defined by the feature modules and
     * <code>Gnm</code>.
     *
     * @param featureModules
     *            list of feature modules required for this network.
     * @param gnm
     *            the GNM object for this network.
     * @param networkSize
     *            size of network requested.
     * @return a Network object.
     */
    public Network buildNetwork(final List<FeatureModule> featureModules, final Gnm gnm, final int networkSize) {
        this.gnm = gnm;
        logNetworkRequestDetails(featureModules, this.gnm, networkSize);

        mimSelector = new MimSelector(gnm, networkSize);
        nameGenerator = new NameGenerator();

        final Network network = new _1Network();
        populateNetworkWithSystemCreatedNEs(network, networkSize);
        applyFeaturesToNetwork(network, featureModules);

        logger.info("Network build complete.");
        return network;
    }

    private void populateNetworkWithSystemCreatedNEs(final Network network, final int requiredNEs) {
        logger.info("Building {} node {} network...", requiredNEs, gnm.getNetworkType());

        final List<NetworkElement> networkElements = new ArrayList<NetworkElement>();

        for (int currentNEs = 0; currentNEs < requiredNEs; currentNEs++) {
            final String mimName = mimSelector.getNextMimType();
            networkElements.add(new NetworkElement(mimName, nameGenerator.getNextNeName(mimName)));
        }

        logger.info("{} node(s) created.", requiredNEs);

        network.addNetworkElements(networkElements);
    }

    private void applyFeaturesToNetwork(final Network network, List<FeatureModule> featureModules) {
        featureModules = FeatureManager.orderFeaturesAllowingForDependencies(featureModules);

        for (final FeatureModule feature : featureModules) {
            applyFeatureToNetwork(network, feature);
        }
    }

    private Network applyFeatureToNetwork(final Network network, final FeatureModule feature) {
        logger.info("Applying {} feature to network...", feature.getName());

        final RuleMap ruleNamesMap = feature.getRules();
        final Map<RuleConfig, Rule> ruleObjectMap = RuleUtility.getRuleObjectsFromRuleNames(ruleManager, ruleNamesMap.getMap());
        applyRulesToNetwork(network, ruleObjectMap);

        logger.info("{} feature applied to network.", feature.getName());
        return network;
    }

    private void applyRulesToNetwork(final Network network, final Map<RuleConfig, Rule> ruleData) {
        for (final Map.Entry<RuleConfig, Rule> entry : ruleData.entrySet()) {
            final Rule rule = entry.getValue();
            final RuleConfig ruleConfig = entry.getKey();
            addGnmIfRequired(ruleConfig);
            rule.apply(network, ruleConfig);
        }
    }

    private void addGnmIfRequired(final RuleConfig ruleConfig) {
        if (ruleConfig.isGnmRequired()) {
            ruleConfig.setGnm(gnm);
        }
    }

    private void logNetworkRequestDetails(final List<FeatureModule> featureModules, final Gnm gnm, final int numberOfNodes) {
        String featureNames = "";

        for (final FeatureModule feature : featureModules) {
            if (!(feature == null)) {
                featureNames += feature.getName() + " : ";
            }
        }

        logger.info("Received request to build {} node {} Network with features: {}",
                numberOfNodes, gnm.getNetworkType(), featureNames);
    }
}
