/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.ss;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.generics.NetworkElement;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;
import com.ericsson.de.onetwork.ss.util.SimServiceStringBuilder;

/**
 * Generates Managed Object scripts according to NETSim tool standard.
 * <p>
 * Managed Objects scripts are passed to NETSim simulator in order to create a
 * simulation.
 * <p>
 * DepreactedMos: RbsConfiguration
 *
 * @author qfatonu
 */
public class SimMoDataGenerator {

    private final static Logger logger = LoggerFactory.getLogger(SimMoDataGenerator.class);

    /** Holds reference to MO script data */
    private StringBuilder moDataHolder;

    /** An attribute data type lookup map from MOM to NETSim */
    final private static Map<String, String> moAttrDataTypeForNetsim;

    static {
        // Keys that directly come from MOM file
        moAttrDataTypeForNetsim = new HashMap<String, String>();
        moAttrDataTypeForNetsim.put("long", "Integer");
        moAttrDataTypeForNetsim.put("enumref", "Integer");
        moAttrDataTypeForNetsim.put("structref", "Struct");
        moAttrDataTypeForNetsim.put("moref", "Ref");
        moAttrDataTypeForNetsim.put("sequence", "Array");
        moAttrDataTypeForNetsim.put("boolean", "Boolean");
        moAttrDataTypeForNetsim.put("string", "String");
        moAttrDataTypeForNetsim.put("longlong", "Long");

        // Business layer related keys
        moAttrDataTypeForNetsim.put("struct", "Struct");
        moAttrDataTypeForNetsim.put("enum", "Integer");
    }

    /**
     * Processes the simulation object to load MO script data onto.
     *
     * @param sim
     *            the simulation where MO script data load to
     */
    public void loadMoScriptsOnto(final Sim sim) {

        final List<NetworkElement> networkElements = sim.getNetworkElements();

        for (final NetworkElement ne : networkElements) {
            final String neName = ne.getName();
            final Mo mo = ne.getRootMo();

            moDataHolder = new StringBuilder();
            populateMosWithoutSetMoRef(mo);
            sim.getNetworkElementToMoScriptMap().put(neName, new String(moDataHolder));
        }
    }

    /**
     * TODO: In future, this method should be able to exclude all MO Ref
     * attribute types of line while MO creation occurs. MO Ref should be set
     * after all MOs are created.
     * <p>
     *
     * @param mo
     *            the managed object data
     */
    private void populateMosWithoutSetMoRef(final Mo mo) {

        if (!mo.isSystemCreated()) {
            moDataHolder.append(createMoScript(mo));
        }

        for (final Mo childMo : mo.getChildren()) {
            populateMosWithoutSetMoRef(childMo);
        }
    }

    private String createMoScript(final Mo mo) {

        final String parentFdn = mo.getParentFdn() == null ? "\"\"" : mo.getParentFdn().toString();
        final String identity = mo.getName() == null ? "\"\"" : mo.getName();
        final String moType = mo.getType() == null ? "\"\"" : mo.getType();
        final int numOfAttr = mo.getAttributeNames().size();

        return fillCreateMoTemplateForCpp(parentFdn, identity, moType, numOfAttr, mo);
    }

    private String fillCreateMoTemplateForCpp(final String parentFdn, final String identity, final String moType, final int numOfAttr, final Mo mo) {

        final SimServiceStringBuilder moScript = new SimServiceStringBuilder();

        moScript.append("CREATE");
        moScript.append("(");
        moScript.append("  parent " + parentFdn);
        moScript.append("  identity " + identity);
        moScript.append("  moType " + moType);
        moScript.append("  exception none");
        moScript.append("  nrOfAttributes " + numOfAttr);
        moScript.appendWithoutNewLine(parseMoAttributes(mo));
        moScript.append(")");

        return moScript.toString();
    }

    private String parseMoAttributes(final Mo mo) {

        final StringBuilder moScript = new StringBuilder();

        for (final String moAttrName : mo.getFeaturePopulatedAttributeNames()) {
            final MoAttributeDataType moAttrDataType = mo.getAttributeByName(moAttrName);

            moScript.append(parseMoAttributeDataType(moAttrName, moAttrDataType, "", false));
        }
        return moScript.toString();
    }

    /**
     * Parses MO attribute data type according to NETSim MML command format in a
     * recursive way.
     *
     * @param moAttrName
     *            the attribute name of an MO
     * @param moAttrDataType
     *            the attribute data type of an MO
     * @param initialData
     *            the processed data which pass to the recursive function
     * @param listItem
     *            defines attribute whether a list item.
     * @return an MO script which defines attribute data
     */
    private String parseMoAttributeDataType(final String moAttrName, final MoAttributeDataType moAttrDataType, final String initialData,
            final boolean listItem) {

        final SimServiceStringBuilder moScript = new SimServiceStringBuilder();

        if (!initialData.equals("")) {
            moScript.append(initialData);
        }

        if (moAttrDataType == null) {
            return moScript.toString();
        }

        final String moAttrType = moAttrDataTypeForNetsim.get(moAttrDataType.getDataType().toLowerCase());
        final Object moAttrValue = moAttrDataType.getValue();
        final String moAttrValueClassName = moAttrValue.getClass().getSimpleName().toUpperCase();

        logger.debug("Parsed attribute: {} : {} : {}", moAttrName, moAttrValue.toString(), moAttrValueClassName);

        if (moAttrValueClassName.contains("MAP")) {
            @SuppressWarnings("unchecked")
            final Map<String, MoAttributeDataType> subAttrs = (Map<String, MoAttributeDataType>) moAttrDataType.getValue();
            if (!listItem) {
                moScript.append(2, moAttrName + " " + moAttrType);
            }
            moScript.append(listItem ? 2 : 4, "nrOfElements " + subAttrs.size());

            for (final Map.Entry<String, MoAttributeDataType> entry : subAttrs.entrySet()) {
                final String subMoAttrName = entry.getKey();
                final MoAttributeDataType subMoAttrDataType = entry.getValue();
                moScript.appendWithoutNewLine(4, parseMoAttributeDataType(subMoAttrName, subMoAttrDataType, "", false));
            }

        } else if (moAttrValueClassName.contains("LIST")) {
            @SuppressWarnings("unchecked")
            final List<MoAttributeDataType> subMoAttrList = (List<MoAttributeDataType>) moAttrDataType.getValue();
            final String listMoAttrType = subMoAttrList.get(0).getDataType();
            final int subMoAttrListSize = subMoAttrList.size();
            moScript.append(2, moAttrName + " " + moAttrType + " " + listMoAttrType + moScript.toString() + " " + subMoAttrListSize);

            for (final MoAttributeDataType subMoAttr : subMoAttrList) {
                moScript.appendWithoutNewLine(2, parseMoAttributeDataType(moAttrName, subMoAttr, "", true));
            }

        } else if (listItem) {
            moScript.append(2, moAttrDataType.getValue().toString());

        } else {

            final String moAttrDataTypeValue = moAttrDataType.toString().isEmpty() ? "\"\"" : moAttrDataType.toString();
            moScript.append(2, moAttrName + " " + moAttrDataTypeForNetsim.get(moAttrDataType.getDataType().toLowerCase()) + " "
                    + moAttrDataTypeValue);
        }

        return moScript.toString();
    }
}
