
package com.ericsson.de.onetwork.dms.util;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.ss.util.InvalidMimVersionFormatException;

/**
 * Verifies MimFileUtility methods.
 *
 * @author qfatonu
 */
public class MimFileUtilityTest {

    private static final String MIM_FILE_NAME = "Netsim_ERBS_NODE_MODEL_vF_1_101.xml";
    private static final String MIM_VERSION = "LTE ERBS F1101";

    private static final String MIM_FILE_NAME_v2 = "ERBSNodeLimited_G_1_220-V1.xml";
    private static final String MIM_VERSION_v2 = "LTE ERBS G1220-V1lim";

    @Test
    public void isMimFileAvailable() {
        Assert.assertEquals(MimFileUtility.isMimFileAvailable("F1101"), true);
    }

    @Test
    public void getMimFileNameLocally() {
        Assert.assertEquals(MimFileUtility.getMimFileNameLocally("F1101"), MIM_FILE_NAME);
    }

    @Test
    public void getMimFileName() throws InvalidMimVersionFormatException, IOException {

        Assert.assertEquals(MimFileUtility.getMimFileName(MIM_VERSION), MIM_FILE_NAME);
    }

    @Test
    public void copyMimFile() throws IOException {
        final String simName = "ERBSA930-V1_R25G.zip";
        Assert.assertEquals(MimFileUtility.copyMimFile(simName), true);
    }

    // TODO: Disable or update MIM version when it failed. MIM_VERSION should be
    // within latest 10 MIM version range.
    @Test
    public void getMimFile_v1() throws InvalidMimVersionFormatException, IOException {
        Assert.assertEquals(MimFileUtility.getMimFileName(MIM_VERSION), MIM_FILE_NAME);
    }

    // TODO: Disable or update MIM version when it failed. MIM_VERSION should be
    // within latest 10 MIM version range.
    @Test
    public void getMimFile_v2() throws InvalidMimVersionFormatException, IOException {
        Assert.assertEquals(MimFileUtility.getMimFileName(MIM_VERSION_v2), MIM_FILE_NAME_v2);

    }

}
