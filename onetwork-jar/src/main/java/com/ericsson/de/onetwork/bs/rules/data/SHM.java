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

package com.ericsson.de.onetwork.bs.rules.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.de.onetwork.bs.rules.RuleConfig;
import com.ericsson.de.onetwork.bs.rules.RuleMap;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;
import com.ericsson.de.onetwork.util.FileUtility;

/**
 * TODO: Rule data should be in DB
 * SHM rule data is populated in this class.
 *
 * @author ecasjim
 */
public class SHM {

    public static RuleMap getRules() {
        final Map<String, RuleConfig> ruleMap = new LinkedHashMap<String, RuleConfig>();
        final String nodeNamePlaceholder = "<NodeName>";

        // Licensing MO
        final RuleConfig ruleConfigLicensing = new RuleConfig("ManagedElement,SystemFunctions,Licensing");

        final Map<String, MoAttributeDataType> licensingAttrMap = new HashMap<String, MoAttributeDataType>();
        licensingAttrMap.put("userLabel", new MoAttributeDataType("String", "ENM14BLICENSEING"));
        licensingAttrMap.put("LicensingId", new MoAttributeDataType("String", "30Q"));
        licensingAttrMap.put("licenseFileUrl", new MoAttributeDataType("String", "http://10.128.163.227:80/cello/licensing/"));
        licensingAttrMap.put("fingerprint", new MoAttributeDataType("String", nodeNamePlaceholder + "_fp"));

        ruleConfigLicensing.setAttributes(licensingAttrMap);
        ruleMap.put("BasicMoCreation_" + ruleConfigLicensing.hashCode(), ruleConfigLicensing);

        // ConfigurationVersion MO
        final RuleConfig ruleConfig_ConfigurationVersion = new RuleConfig("ManagedElement,SwManagement,ConfigurationVersion");

        final Map<String, MoAttributeDataType> ConfigurationVersionAttrMap = new HashMap<String, MoAttributeDataType>();
        ConfigurationVersionAttrMap.put("currentUpgradePackage", new MoAttributeDataType("Ref",
                "ManagedElement=$managedElement,SwManagement=1,UpgradePackage=1"));
        ConfigurationVersionAttrMap.put("userLabel", new MoAttributeDataType("String", "BakupInventoryforENM"));
        ConfigurationVersionAttrMap.put("timeForAutoCreatedCV", new MoAttributeDataType("String", "10:00"));
        ConfigurationVersionAttrMap.put("startableConfigurationVersion", new MoAttributeDataType("String", "CXPENM100"));
        ConfigurationVersionAttrMap.put("rollbackList", new MoAttributeDataType("Array String", "CXPENM101"));
        ConfigurationVersionAttrMap.put("rollbackInitTimerValue", new MoAttributeDataType("Integer", "30"));
        ConfigurationVersionAttrMap.put("rollbackInitCounterValue", new MoAttributeDataType("Integer", "5"));
        ConfigurationVersionAttrMap.put("restoreConfirmationDeadline", new MoAttributeDataType("String", "CXPENM1201"));
        ConfigurationVersionAttrMap.put("lastCreatedCv", new MoAttributeDataType("String", "CXPENM1201Yesterday"));
        ConfigurationVersionAttrMap.put("executingCv", new MoAttributeDataType("String", "CXPENM1201executing"));
        ConfigurationVersionAttrMap.put("currentLoadedConfigurationVersion", new MoAttributeDataType("String", "CXPENM1201Loaded"));
        ConfigurationVersionAttrMap.put("ConfigurationVersionId", new MoAttributeDataType("String", "007"));
        ConfigurationVersionAttrMap.put("configOpCountdown", new MoAttributeDataType("Integer", "10"));
        ConfigurationVersionAttrMap.put("configAdmCountdown", new MoAttributeDataType("Integer", "130"));
        ConfigurationVersionAttrMap.put("listOfHtmlResultFiles", new MoAttributeDataType("Array String", "CXPENM120130S"));

        final List<MoAttributeDataType> storedConfigurationVersionsArray = new ArrayList<MoAttributeDataType>();
        Map<String, MoAttributeDataType> ConfigurationVersionAttributesStruct = new HashMap<String, MoAttributeDataType>();
        ConfigurationVersionAttributesStruct.put("time", new MoAttributeDataType("String", "backuptest1"));
        ConfigurationVersionAttributesStruct.put("identity", new MoAttributeDataType("String", "someidentity"));
        ConfigurationVersionAttributesStruct.put("type", new MoAttributeDataType("String", "STANDARD"));
        ConfigurationVersionAttributesStruct.put("upgradePackageId", new MoAttributeDataType("String", "1586"));
        ConfigurationVersionAttributesStruct.put("operatorName", new MoAttributeDataType("String", "shmtest"));
        ConfigurationVersionAttributesStruct.put("operatorComment", new MoAttributeDataType("String", "someComment"));
        ConfigurationVersionAttributesStruct.put("date", new MoAttributeDataType("String", "Thu Jun 21 17:32:05 2007"));
        ConfigurationVersionAttributesStruct.put("status", new MoAttributeDataType("String", "OK"));

        storedConfigurationVersionsArray.add(new MoAttributeDataType("Struct", ConfigurationVersionAttributesStruct));
        storedConfigurationVersionsArray.add(new MoAttributeDataType("Struct", ConfigurationVersionAttributesStruct));
        storedConfigurationVersionsArray.add(new MoAttributeDataType("Struct", ConfigurationVersionAttributesStruct));
        storedConfigurationVersionsArray.add(new MoAttributeDataType("Struct", ConfigurationVersionAttributesStruct));
        storedConfigurationVersionsArray.add(new MoAttributeDataType("Struct", ConfigurationVersionAttributesStruct));
        storedConfigurationVersionsArray.add(new MoAttributeDataType("Struct", ConfigurationVersionAttributesStruct));

        ConfigurationVersionAttributesStruct = new HashMap<String, MoAttributeDataType>();
        ConfigurationVersionAttributesStruct.put("time", new MoAttributeDataType("String", "1"));
        ConfigurationVersionAttributesStruct.put("identity", new MoAttributeDataType("String", "2"));
        ConfigurationVersionAttributesStruct.put("type", new MoAttributeDataType("String", "3"));
        ConfigurationVersionAttributesStruct.put("upgradePackageId", new MoAttributeDataType("String", "4"));
        ConfigurationVersionAttributesStruct.put("operatorName", new MoAttributeDataType("String", "5"));
        ConfigurationVersionAttributesStruct.put("operatorComment", new MoAttributeDataType("String", "6"));
        ConfigurationVersionAttributesStruct.put("date", new MoAttributeDataType("String", "7"));
        ConfigurationVersionAttributesStruct.put("status", new MoAttributeDataType("String", "8"));

        storedConfigurationVersionsArray.add(new MoAttributeDataType("Struct", ConfigurationVersionAttributesStruct));
        final MoAttributeDataType moAttrDataTypeInSequence = new MoAttributeDataType("Sequence", storedConfigurationVersionsArray);
        ConfigurationVersionAttrMap.put("storedConfigurationVersions", moAttrDataTypeInSequence);

        ruleConfig_ConfigurationVersion.setAttributes(ConfigurationVersionAttrMap);
        ruleMap.put("BasicMoCreation_" + ruleConfig_ConfigurationVersion.hashCode(), ruleConfig_ConfigurationVersion);

        // LoadModule MOs
        final List<String> loadModules = FileUtility.readFromCSV("/bs/features/SHM/LoadModulesProductName.csv");

        for (int i = 0; i < loadModules.size(); i++) {
            final Map<String, MoAttributeDataType> loadModulesAttrMap = new HashMap<String, MoAttributeDataType>();
            final RuleConfig ruleConfig_LoadModule = new RuleConfig("ManagedElement,SwManagement,LoadModule", loadModules.get(i));
            final String productDataValue = new String(nodeNamePlaceholder + "_" + String.valueOf(i + 1));

            final Map<String, MoAttributeDataType> loadModulesAttributesStruct = new HashMap<String, MoAttributeDataType>();
            loadModulesAttributesStruct.put("productNumber", new MoAttributeDataType("String", productDataValue));
            loadModulesAttributesStruct.put("productRevision", new MoAttributeDataType("String", productDataValue));
            loadModulesAttributesStruct.put("productName", new MoAttributeDataType("String", productDataValue));
            loadModulesAttributesStruct.put("productInfo", new MoAttributeDataType("String", productDataValue));
            loadModulesAttributesStruct.put("productionDate", new MoAttributeDataType("String", productDataValue));

            final MoAttributeDataType loadModulesArray = new MoAttributeDataType("Struct", loadModulesAttributesStruct);
            loadModulesAttrMap.put("productData", loadModulesArray);

            ruleConfig_LoadModule.setAttributes(loadModulesAttrMap);
            ruleMap.put("BasicMoCreation_" + ruleConfig_LoadModule.hashCode(), ruleConfig_LoadModule);
        }

        // Subrack MOs
        final String subrackProductName = "ERBS14B", subrackProductNumber = "E.1.120", subrackProductRevision = "CXP21", subrackSerialNumber =
                "lienb0635", subrackProductionDate = "5";

        final Map<String, MoAttributeDataType> subrackAttrMap = new HashMap<String, MoAttributeDataType>();
        RuleConfig ruleConfig_Subrack;
        final int subracksToCreatePerNode = 9;

        ruleConfig_Subrack = new RuleConfig("ManagedElement,Equipment,Subrack", subracksToCreatePerNode);
        ruleConfig_Subrack.setIncrementingAcrossNode(true);

        final Map<String, MoAttributeDataType> operationalProductDataStruct = new HashMap<String, MoAttributeDataType>();
        operationalProductDataStruct.put("productName", new MoAttributeDataType("String", subrackProductName));
        operationalProductDataStruct.put("productNumber", new MoAttributeDataType("String", subrackProductNumber));
        operationalProductDataStruct.put("productRevision", new MoAttributeDataType("String", subrackProductRevision));
        operationalProductDataStruct.put("serialNumber", new MoAttributeDataType("String", subrackSerialNumber));
        operationalProductDataStruct.put("productionDate", new MoAttributeDataType("String", subrackProductionDate));

        final MoAttributeDataType subrackArray = new MoAttributeDataType("Struct", operationalProductDataStruct);
        subrackAttrMap.put("operationalProductData", subrackArray);

        // Second struct
        final Map<String, MoAttributeDataType> administrativeProductDataStruct = new HashMap<String, MoAttributeDataType>();
        administrativeProductDataStruct.put("productName", new MoAttributeDataType("String", nodeNamePlaceholder));
        administrativeProductDataStruct.put("productNumber", new MoAttributeDataType("String", nodeNamePlaceholder));
        administrativeProductDataStruct.put("productRevision", new MoAttributeDataType("String", nodeNamePlaceholder));
        administrativeProductDataStruct.put("productInfo", new MoAttributeDataType("String", nodeNamePlaceholder));
        administrativeProductDataStruct.put("productionDate", new MoAttributeDataType("String", nodeNamePlaceholder));

        final MoAttributeDataType subrackArray2 = new MoAttributeDataType("Struct", administrativeProductDataStruct);
        subrackAttrMap.put("administrativeProductData", subrackArray2);

        final String userLabel = new String(subrackProductName);
        subrackAttrMap.put("userLabel", new MoAttributeDataType("String", userLabel));

        final String moValuePlaceholder = "<MoValue>";
        final String subrackPosition = moValuePlaceholder + "B";
        subrackAttrMap.put("subrackPosition", new MoAttributeDataType("String", subrackPosition));

        ruleConfig_Subrack.setAttributes(subrackAttrMap);
        ruleMap.put("BasicMoCreation_" + ruleConfig_Subrack.hashCode(), ruleConfig_Subrack);

        // Slot MOs
        final String productName = "ERBS14B", productNumber = "ENM14B", serialNumber = "3oQ", ProductionDate = "";

        final Map<String, MoAttributeDataType> slotAttrMap = new HashMap<String, MoAttributeDataType>();
        final List<String> slotProductRevisions = FileUtility.readFromCSV("/bs/features/SHM/slotProductRevisions.csv");
        RuleConfig ruleConfig_slots;
        final int mosToCreatePerNode = 28;

        for (int counter = 0; counter < slotProductRevisions.size(); counter++) {
            ruleConfig_slots = new RuleConfig("ManagedElement,Equipment,Subrack,Slot", mosToCreatePerNode);
            ruleConfig_slots.setIncrementingAcrossNode(true);

            final Map<String, MoAttributeDataType> slotAttributesStruct = new HashMap<String, MoAttributeDataType>();
            slotAttributesStruct.put("productName", new MoAttributeDataType("String", productName));
            slotAttributesStruct.put("productNumber", new MoAttributeDataType("String", productNumber));
            slotAttributesStruct.put("productRevision", new MoAttributeDataType("String", slotProductRevisions.get(counter)));
            slotAttributesStruct.put("serialNumber", new MoAttributeDataType("String", serialNumber));
            slotAttributesStruct.put("productionDate", new MoAttributeDataType("String", ProductionDate));

            final MoAttributeDataType slotArray = new MoAttributeDataType("Struct", slotAttributesStruct);
            slotAttrMap.put("productData", slotArray);

            ruleConfig_slots.setAttributes(slotAttrMap);
            ruleMap.put("BasicMoCreation_" + ruleConfig_slots.hashCode(), ruleConfig_slots);
        }

        // ReliableProgramUniter MOs
        final RuleConfig ruleConfigReliableProgramUniter = new RuleConfig("ManagedElement,SwManagement,ReliableProgramUniter", "sctp");
        ruleConfigReliableProgramUniter.setAttributeIdSetting(false);
        ruleMap.put("BasicMoCreation_" + ruleConfigReliableProgramUniter.hashCode(), ruleConfigReliableProgramUniter);

        // PiuTypeIdentitys MOs
        final List<String> piuTypeIdentitys = FileUtility.readFromCSV("/bs/features/SHM/piuTypeIdentitys.csv");

        for (final String piuTypeIdentity : piuTypeIdentitys) {
            final RuleConfig ruleConfigPiuTypeIdentitys = new RuleConfig("ManagedElement,SwManagement,PiuType", piuTypeIdentity);
            ruleMap.put("BasicMoCreation_" + ruleConfigPiuTypeIdentitys.hashCode(), ruleConfigPiuTypeIdentitys);
        }

        // SwAllocation MOs
        final RuleConfig ruleConfigSwAllocation = new RuleConfig("ManagedElement,SwManagement,SwAllocation");
        ruleMap.put("BasicMoCreation_" + ruleConfigSwAllocation.hashCode(), ruleConfigSwAllocation);

        // Repertoire MOs
        final RuleConfig ruleConfigRepertoire = new RuleConfig("ManagedElement,SwManagement,Repertoire");
        ruleMap.put("BasicMoCreation_" + ruleConfigRepertoire.hashCode(), ruleConfigRepertoire);

        // upgradePackageNames MOs
        final List<String> upgradePackageNames = FileUtility.readFromCSV("/bs/features/SHM/upgradePackageNames.csv");

        for (final String upgradePackageName : upgradePackageNames) {
            final RuleConfig ruleConfigPiuTypeIdentitys = new RuleConfig("ManagedElement,SwManagement,UpgradePackage", upgradePackageName);
            final Map<String, MoAttributeDataType> upgradePackageNamesAttrMap = new HashMap<String, MoAttributeDataType>();

            final Map<String, MoAttributeDataType> upgradePackageStruct = new HashMap<String, MoAttributeDataType>();
            upgradePackageStruct.put("productName", new MoAttributeDataType("String", upgradePackageName));
            upgradePackageStruct.put("productNumber", new MoAttributeDataType("String", upgradePackageName));
            upgradePackageStruct.put("productRevision", new MoAttributeDataType("String", upgradePackageName));
            upgradePackageStruct.put("productInfo", new MoAttributeDataType("String", upgradePackageName));
            upgradePackageStruct.put("productionDate", new MoAttributeDataType("String", upgradePackageName));

            final MoAttributeDataType administrativeDataType = new MoAttributeDataType("Struct", upgradePackageStruct);
            upgradePackageNamesAttrMap.put("administrativeData", administrativeDataType);

            // Second struct
            final List<MoAttributeDataType> loadModuleListArray = new ArrayList<MoAttributeDataType>();
            loadModuleListArray.add(new MoAttributeDataType("Ref", "ManagedElement=1,SwManagement=1,LoadModule=aal0_dynamic"));
            loadModuleListArray.add(new MoAttributeDataType("Ref", "ManagedElement=1,SwManagement=1,LoadModule=equipmp"));
            loadModuleListArray.add(new MoAttributeDataType("Ref", "ManagedElement=1,SwManagement=1,LoadModule=etm4v2atm"));

            final MoAttributeDataType loadModuleDataType = new MoAttributeDataType("Array", loadModuleListArray);
            upgradePackageNamesAttrMap.put("loadModuleList", loadModuleDataType);

            ruleConfigPiuTypeIdentitys.setAttributes(upgradePackageNamesAttrMap);

            ruleMap.put("BasicMoCreation_" + ruleConfigPiuTypeIdentitys.hashCode(), ruleConfigPiuTypeIdentitys);
        }

        return new RuleMap(ruleMap);
    }
}
