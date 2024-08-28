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
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.bs.rules.RuleMap;

/**
 * Verifies that the FeatureManager can retrieve the requested FeatureModule
 * object.
 *
 * @author ecasjim
 */
public class FeatureManagerTest {

    private FeatureManager featureManager;
    private String lteFeatureName;
    private String coreFeatureName;

    @BeforeTest
    public void setUp() {
        featureManager = new FeatureManager();
        lteFeatureName = "Cell Pattern Assignment";
        coreFeatureName = "Hardware Item Support";
    }

    @Test
    public final void verifyModuleRetrieval() {
        final FeatureManager featureManager = new FeatureManager();
        final FeatureModule basicERBSFeatureModule = featureManager.getFeatureModule(lteFeatureName);
        Assert.assertNotNull(basicERBSFeatureModule);
    }

    @Test
    public final void verifyFeaturesReturnedForLTENetworkType() {
        List<FeatureModule> features = new ArrayList<FeatureModule>();
        features = featureManager.getFeatureListForNetworkType("LTE");
        boolean found = false;

        for (final FeatureModule feature : features) {
            if (feature.getName().equalsIgnoreCase(lteFeatureName)) {
                found = true;
            }
        }

        Assert.assertTrue(found);
    }

    @Test
    public final void verifyFeaturesReturnedForCORENetworkType() {
        List<FeatureModule> features = new ArrayList<FeatureModule>();
        features = featureManager.getFeatureListForNetworkType("CORE");
        boolean found = false;

        for (final FeatureModule feature : features) {
            if (feature.getName().equalsIgnoreCase(coreFeatureName)) {
                found = true;
            }
        }

        Assert.assertTrue(found);
    }

    @Test
    public final void verifyFeatureOrderAllowsForDependcies() {
        List<FeatureModule> featureModules = new ArrayList<FeatureModule>();

        final FeatureModule eutran = new FeatureModule("EUtran Support", new RuleMap());
        eutran.addDependency("Cell Pattern Assignment");
        final FeatureModule utran = new FeatureModule("Utra Support", new RuleMap());
        utran.addDependency("Cell Pattern Assignment");
        final FeatureModule cells = new FeatureModule("Cell Pattern Assignment", new RuleMap());

        featureModules.add(eutran);
        featureModules.add(utran);
        featureModules.add(cells);

        featureModules = FeatureManager.orderFeaturesAllowingForDependencies(featureModules);

        Assert.assertEquals(featureModules.get(0).getName(), "Cell Pattern Assignment");
    }
}
