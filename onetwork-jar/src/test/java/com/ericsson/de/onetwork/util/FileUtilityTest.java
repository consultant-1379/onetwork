
package com.ericsson.de.onetwork.util;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FileUtilityTest {

    @Test
    public void readLoadModulesFromCSV() {
        final List<String> loadModules = FileUtility.readFromCSV("/bs/features/SHM/slotProductRevisions.csv");
        Assert.assertTrue(loadModules.size() > 0);
    }
}
