
package com.ericsson.de.onetwork.ss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.dms.generics.NetworkElement;

/**
 * Verifies that <code>Simulation</code> class methods behave correctly.
 */
public class SimTest {

    Map<String, String> nesMoScripts = new HashMap<>();
    List<NetworkElement> networkElements = new ArrayList<>();
    Sim sim = new Sim(networkElements);
    final String simName = "simName01";

    @Test
    public void testAppendMmlCmd() {
        final String command = "mmlCommand01";
        sim.appendMMlCmd(command);
        Assert.assertEquals(sim.getMmlCmd(), command);
    }

    @Test
    public void testGetName() {
        sim.setName(simName);
        Assert.assertEquals(sim.getName(), simName);
    }

    @Test
    public void testGetworkElementToMoScriptMap() {
        Assert.assertEquals(sim.getNetworkElementToMoScriptMap(), nesMoScripts);
    }

    @Test
    public void testGetNetworkElements() {
        Assert.assertEquals(sim.getNetworkElements(), networkElements);
    }

    @Test
    public void setName() {
        sim.setName(simName);
        Assert.assertEquals(simName, sim.getName());
    }
}
