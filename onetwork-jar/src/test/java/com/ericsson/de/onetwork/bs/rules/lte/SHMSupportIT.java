
package com.ericsson.de.onetwork.bs.rules.lte;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.bs.Network;
import com.ericsson.de.onetwork.bs._1Network;
import com.ericsson.de.onetwork.bs.features.FeatureManager;
import com.ericsson.de.onetwork.bs.features.FeatureModule;
import com.ericsson.de.onetwork.bs.rules.Rule;
import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleManager;
import com.ericsson.de.onetwork.bs.rules.RuleMap;
import com.ericsson.de.onetwork.bs.rules.RuleUtility;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.generics.MoFactory;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.util.FileUtility;

/**
 * Used to verify SHM feature
 *
 * @author ecasjim
 */
public class SHMSupportIT {

    private static Network NETWORK = null;
    private final static FeatureManager FEATURE_MANAGER = new FeatureManager();
    private final static RuleManager RULE_MANAGER = new RuleManager(new MoFactory(true));
    private final static int TOTAL_NODES = 3;

    private final static String LICENSING_MOTYPE_HIERARCHY = "ManagedElement,SystemFunctions,Licensing";
    private final static int TOTAL_LICENSING_MOS_PER_NODE = 1;

    private final static String LOADMODULE_MOTYPE_HIERARCHY = "ManagedElement,SwManagement,LoadModule";
    private final static int TOTAL_LOADMODULES_MOS_PER_NODE = FileUtility.readFromCSV("/bs/features/SHM/LoadModulesProductName.csv").size();

    private final static String CONFIGURATION_VERSION_MOTYPE_HIERARCHY = "ManagedElement,SwManagement,ConfigurationVersion";
    private final static int CONFIGURATIONVERSION_MOS_PER_NODE = 1;

    private final static String SUBRACK_MOTYPE_HIERARCHY = "ManagedElement,Equipment,Subrack";
    private final static int SUBRACK_MOS_PER_NODE = 9;

    private final static String SLOT_MOTYPE_HIERARCHY = "ManagedElement,Equipment,Subrack,Slot";
    private final static int SLOT_MOS_PER_NODE = FileUtility.readFromCSV("/bs/features/SHM/slotProductRevisions.csv").size();

    private final static String RELIABLEPROGRAMUNITER_MOTYPE_HIERARCHY = "ManagedElement,SwManagement,ReliableProgramUniter";
    private final static int RELIABLEPROGRAMUNITER_MOS_PER_NODE = 1;

    private final static String PIUTYPE_MOTYPE_HIERARCHY = "ManagedElement,SwManagement,PiuType";
    private final static int PIUTYPE_MOS_PER_NODE = FileUtility.readFromCSV("/bs/features/SHM/piuTypeIdentitys.csv").size();

    private final static String SWALLOCATION_MOTYPE_HIERARCHY = "ManagedElement,SwManagement,SwAllocation";
    private final static int SWALLOCATION_MOS_PER_NODE = 1;

    private final static String REPERTOIRE_MOTYPE_HIERARCHY = "ManagedElement,SwManagement,Repertoire";
    private final static int REPERTOIRE_MOS_PER_NODE = 1;

    private final static String UPGRADEPACKAGE_MOTYPE_HIERARCHY = "ManagedElement,SwManagement,UpgradePackage";
    private final static int UPGRADEPACKAGE_MOS_PER_NODE = FileUtility.readFromCSV("/bs/features/SHM/upgradePackageNames.csv").size();

    @BeforeClass()
    public void setup() {
        NETWORK = new _1Network();
        addNodes(NETWORK);

        final FeatureModule SHMFeature = FEATURE_MANAGER.getFeatureModule("SHM");
        final RuleMap ruleMap = SHMFeature.getRules();
        final Map<RuleConfig, Rule> ruleObjectMap = RuleUtility.getRuleObjectsFromRuleNames(RULE_MANAGER, ruleMap.getMap());

        for (final Map.Entry<RuleConfig, Rule> entry : ruleObjectMap.entrySet()) {
            final Rule rule = entry.getValue();
            final RuleConfig ruleConfig = entry.getKey();
            rule.apply(NETWORK, ruleConfig);
        }
    }

    @Test
    public void verifyLicensingMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, LICENSING_MOTYPE_HIERARCHY), TOTAL_NODES * TOTAL_LICENSING_MOS_PER_NODE);
    }

    @Test
    public void verifyConfigurationVersionMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, CONFIGURATION_VERSION_MOTYPE_HIERARCHY), TOTAL_NODES * CONFIGURATIONVERSION_MOS_PER_NODE);
    }

    @Test
    public void verifySubrackMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, SUBRACK_MOTYPE_HIERARCHY), TOTAL_NODES * SUBRACK_MOS_PER_NODE);
    }

    @Test
    public void verifySlotMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, SLOT_MOTYPE_HIERARCHY), TOTAL_NODES * SLOT_MOS_PER_NODE);
    }

    @Test
    public void verifyReliableProgramUniterMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, RELIABLEPROGRAMUNITER_MOTYPE_HIERARCHY), TOTAL_NODES * RELIABLEPROGRAMUNITER_MOS_PER_NODE);
    }

    @Test
    public void verifyPiutypeMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, PIUTYPE_MOTYPE_HIERARCHY), TOTAL_NODES * PIUTYPE_MOS_PER_NODE);
    }

    @Test
    public void verifySwAllocationMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, SWALLOCATION_MOTYPE_HIERARCHY), TOTAL_NODES * SWALLOCATION_MOS_PER_NODE);
    }

    @Test
    public void verifyRepertoireMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, REPERTOIRE_MOTYPE_HIERARCHY), TOTAL_NODES * REPERTOIRE_MOS_PER_NODE);
    }

    @Test
    public void verifyUpgradePackageMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, UPGRADEPACKAGE_MOTYPE_HIERARCHY), TOTAL_NODES * UPGRADEPACKAGE_MOS_PER_NODE);
    }

    @Test
    public void verifyLoadModulesMosCreatedByFeature() throws InvalidChildException {
        Assert.assertEquals(getMoCountFromNetwork(NETWORK, LOADMODULE_MOTYPE_HIERARCHY), TOTAL_NODES * TOTAL_LOADMODULES_MOS_PER_NODE);
    }

    private int getMoCountFromNetwork(final Network network, final String moTypeHierarchy) throws InvalidChildException {
        final List<NetworkElement> networkElements = network.getNetworkElements();
        int foundMos = 0;

        for (final NetworkElement networkElement : networkElements) {
            foundMos += networkElement.getMosFromNe(moTypeHierarchy).size();
        }

        return foundMos;
    }

    private void addNodes(final Network network) {
        final List<NetworkElement> networkElements = new ArrayList<NetworkElement>();

        for (int i = 0; i < TOTAL_NODES; i++) {
            networkElements.add(new NetworkElement("LTE ERBS F1101", "LTE01ERBS00001"));
        }

        network.addNetworkElements(networkElements);
    }
}
