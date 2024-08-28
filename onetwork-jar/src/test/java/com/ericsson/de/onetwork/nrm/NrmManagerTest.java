
package com.ericsson.de.onetwork.nrm;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NrmManagerTest {

    private NrmManager nrmManager;

    @BeforeClass
    public void setUp() {
        nrmManager = new NrmManager();
    }

    @Test
    public void getNrm() {
        Assert.assertEquals("R1", nrmManager.getNrm("R1").getName());
    }

    @Test
    public void getNrmList() {
        final List<String> nrmNames = new ArrayList<String>();
        nrmNames.addAll(nrmManager.getNrmList());

        Assert.assertEquals(2, nrmNames.size());
    }

    @Test
    public void getNrmListElements() {
        final List<String> nrmNames = new ArrayList<String>();
        nrmNames.addAll(nrmManager.getNrmList());

        Assert.assertEquals("R2", nrmNames.get(0));
        Assert.assertEquals("R1", nrmNames.get(1));
    }

    @Test
    public void getNrmElementContents() {
        Assert.assertEquals("LTE_R1", nrmManager.getNrm("R1").getGnmNames().get(0));
        Assert.assertEquals("CORE_R1", nrmManager.getNrm("R1").getGnmNames().get(1));
    }
}
