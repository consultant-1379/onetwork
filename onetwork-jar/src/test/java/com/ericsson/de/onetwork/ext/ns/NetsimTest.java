
package com.ericsson.de.onetwork.ext.ns;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Verifies NETSim upgradeability.
 *
 * @author qfatonu
 */
public class NetsimTest {

    @Test(enabled = false)
    public void upgradeNetsim() throws IOException {
        Assert.assertEquals(Netsim.upgradeToLatestVersion(), true);
    }
}
