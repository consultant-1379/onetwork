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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.NetworkRequest;
import com.ericsson.de.onetwork.NetworkType;
import com.ericsson.de.onetwork.bs.features.FeatureManager;
import com.ericsson.de.onetwork.bs.features.FeatureModule;
import com.ericsson.de.onetwork.gnm.Gnm;
import com.ericsson.de.onetwork.gnm.GnmManager;
import com.ericsson.de.onetwork.gnm.GnmRequestException;

/**
 * This class orchestrates the business service layer from a user
 * <code>NetworkRequest</code> to a built <code>Network</code> object.
 *
 * @author ecasjim
 */
public class BusinessServiceController {

    private final static Logger logger = LoggerFactory.getLogger(BusinessServiceController.class);

    /** ProceduralEngine object */
    private final ProceduralEngine engine;

    /** FeatureManager object */
    private final FeatureManager featureManager;

    /**
     * The constructor initialises the <code>ProceduralEngine</code> and
     * <code>FeatureManager</code>.
     */
    public BusinessServiceController() {
        engine = new ProceduralEngine();
        featureManager = new FeatureManager();
    }

    /**
     * Builds a network using information from the network request.
     *
     * @param networkRequest
     *            object containing all network request data.
     * @return network object with requested features applied.
     * @throws GnmRequestException
     *             thrown when there is a issue retrieving requested
     *             <code>Gnm</code>.
     */
    public Network buildNetwork(final NetworkRequest networkRequest) throws GnmRequestException {
        final List<FeatureModule> featureModules = getRequiredFeatureModules(networkRequest);
        final Gnm gnm = GnmManager.getGnm(networkRequest.getGnmRevision());
        updateGnm_OnlyWhenEndUserSetTheMimVersion(gnm, networkRequest.getMimVersion());
        return engine.buildNetwork(featureModules, gnm, networkRequest.getNetworkNodeSize());
    }

    private void updateGnm_OnlyWhenEndUserSetTheMimVersion(final Gnm gnm, final String mimVersion) {
        if (!mimVersion.isEmpty()) {
            logger.debug("mimVersion is updated by end user to \"{}\"", mimVersion);
            final Map<String, Double> mimUsage = new HashMap<String, Double>();
            mimUsage.put(mimVersion, 100.0d);
            gnm.setMimPercentages(mimUsage);
        }
    }

    /**
     * Used to retrieve mandatory and user defined feature modules by analysing
     * the <code>NetworkRequest</code> object.
     *
     * @param networkRequest
     *            object containing all network request data.
     * @return Collection of feature modules.
     * @throws GnmRequestException
     *             thrown when there is a issue retrieving requested
     *             <code>Gnm</code>.
     */
    private List<FeatureModule> getRequiredFeatureModules(final NetworkRequest networkRequest) throws GnmRequestException {
        final List<FeatureModule> mandatoryFeatureModules = getMadatoryFeatureModules(networkRequest);

        if (isUserDefiningFeatures(networkRequest)) {
            final Collection<FeatureModule> userDefinedFeatureModules = getUserDefinedFeatureModules(networkRequest);

            for (final FeatureModule userDefinedFeatureModule : userDefinedFeatureModules) {
                if (!mandatoryFeatureModules.contains(userDefinedFeatureModule)) {
                    mandatoryFeatureModules.add(userDefinedFeatureModule);
                }
            }
        }

        return mandatoryFeatureModules;
    }

    private boolean isUserDefiningFeatures(final NetworkRequest networkRequest) {
        return !networkRequest.getFeatureNames().isEmpty();
    }

    /**
     * Gets user defined feature modules by analysing the
     * <code>NetworkRequest</code> object.
     *
     * @param networkRequest
     *            object containing all network request data.
     * @return Collection of feature modules as requested by the user.
     */
    private Collection<FeatureModule> getUserDefinedFeatureModules(final NetworkRequest networkRequest) {
        final Collection<FeatureModule> features = new ArrayList<FeatureModule>();

        for (final String featureName : networkRequest.getFeatureNames()) {
            features.add(featureManager.getFeatureModule(featureName));
        }

        return features;
    }

    /**
     * Determines mandatory feature modules required by analysing network
     * request.
     *
     * @param networkRequest
     *            object containing all network request data.
     * @return Collection of feature modules that are mandatory for this network
     *         type.
     * @throws GnmRequestException
     *             thrown when there is a issue retrieving requested
     *             <code>Gnm</code>.
     */
    private List<FeatureModule> getMadatoryFeatureModules(final NetworkRequest networkRequest) throws GnmRequestException {
        List<FeatureModule> featureModules = new ArrayList<FeatureModule>();
        final Gnm gnm = GnmManager.getGnm(networkRequest.getGnmRevision());

        switch (gnm.getNetworkType()) {
            case CORE:
                featureModules = getMandatoryCoreFeatureModules();
                break;
            case GRAN:
                throw new GnmRequestException(NetworkType.GRAN.name() + " Gnm not currently supported.");
            case WRAN:
                throw new GnmRequestException(NetworkType.WRAN.name() + " Gnm not currently supported.");
            case LTE:
                featureModules = getMandatoryLTEFeatureModules();
                break;
        }

        return featureModules;
    }

    /**
     * TODO: Mandatory feature modules of a network should be stored in a DB
     * Returns mandatory feature modules required for a Core network request.
     *
     * @return
     */
    private List<FeatureModule> getMandatoryCoreFeatureModules() {
        final List<FeatureModule> featureModules = new ArrayList<FeatureModule>();
        return featureModules;
    }

    /**
     * TODO: Mandatory feature modules of a network should be stored in a DB
     * Returns mandatory feature modules required for a LTE network request.
     *
     * @return Collection of feature modules that are mandatory LTE networks.
     */
    private List<FeatureModule> getMandatoryLTEFeatureModules() {
        final List<FeatureModule> features = new ArrayList<FeatureModule>();
        features.add(featureManager.getFeatureModule("Cell Pattern Assignment"));
        return features;
    }
}
