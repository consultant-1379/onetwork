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

package com.ericsson.de.onetwork;

import java.util.HashMap;
import java.util.Map;

/**
 * Central location for Mim used in testing.
 *
 * @author edalrey
 * @since 1NETWORK_15.15
 */
public class MimXmlFileTestConstants {

    public static final String CPP_MIM = "LTE ERBS F1101";;
    public static final String COM_ECIM_MIM = "CORE SGSN 15B-WPP-V5";

    public static Map<String, String> mimToFile;

    static {
        mimToFile = new HashMap<>();
        mimToFile.put(CPP_MIM, "Netsim_ERBS_NODE_MODEL_vF_1_101.xml");
        mimToFile.put(COM_ECIM_MIM, "SGSN_15B-WPP-V5");
    }

    public static String getCppFile() {
        return mimToFile.get(CPP_MIM);
    }

    public static String getComEcimFile() {
        return mimToFile.get(COM_ECIM_MIM);
    }

}
