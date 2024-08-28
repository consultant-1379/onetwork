
package com.ericsson.de.onetwork.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.ss.util.FileUtils;

/**
 * Verifies OnetworkNetsimHandler methods.
 *
 * @author qfatonu
 */
public class OnetworkNetsimHandlerTest {

    private final Logger logger = LoggerFactory.getLogger(OnetworkNetsimHandlerTest.class);

    private static final String SIM_NAME = "ERBSA930-V1_R25G.zip";
    private static final String NETSIM_VERSION = "R28G";

    private String simName;
    private Path filePath;

    @BeforeClass
    public void setUp() throws IOException {
        final OnetworkSshClient sshClient = new OnetworkSshClient();
        try {
            logger.debug("Copying simulation.");
            final Path parentFolder =
                    Paths.get(FileUtils.getPlatformIndependentPathString(OnetworkNetsimHandlerTest.class.getResource("/util").getPath()));
            logger.debug("parentFolder:{}", parentFolder);

            final Path remoteDestPath = Paths.get("/netsim/netsimdir");
            final List<Path> paths = FileUtils.getFiles(parentFolder);

            filePath = paths.get(0);
            simName = filePath.getFileName().toString();

            logger.debug("filePath={}", filePath);
            logger.debug("simName={}", simName);

            sshClient.copyFiles(paths, remoteDestPath);
            logger.debug("Copying finished ...");
        } finally {
            sshClient.close();
        }
    }

    @AfterClass
    public void cleanUp() throws IOException {
        final OnetworkSshClient sshClient = new OnetworkSshClient();
        try {
            logger.debug("Deleting simulationName: {}", simName);

            final String cmdToDeleteSim = "ls -v /netsim/netsimdir/" + simName;

            sshClient.executeCommand(cmdToDeleteSim);
            logger.debug("Deleting finished ...");
        } finally {
            sshClient.close();
        }
    }

    @Test
    public void openSimulation() throws IOException {
        Assert.assertEquals(OnetworkNetsimHandler.openSimulation(SIM_NAME), true);
    }

    @Test
    public void getNetsimVersion() throws IOException {
        Assert.assertEquals(OnetworkNetsimHandler.getNetsimVersion(), NETSIM_VERSION);
    }

    @Test
    public void isNetsimVersionHigherThanCurrentNetsimVersion_with_LowerMinorRelease() {
        final String minimumRequriedNetsimVersion = "R28F";
        final String currentNetsimVersion = "R28G";
        Assert.assertEquals(false,
                OnetworkNetsimHandler.isNetsimVersionHigherThanCurrentNetsimVersion(minimumRequriedNetsimVersion, currentNetsimVersion));
    }

    @Test
    public void isNetsimVersionHigherThanCurrentNetsimVersion_with_LowerMajorRelease() {
        final String minimumRequriedNetsimVersion = "R27H";
        final String currentNetsimVersion = "R28G";
        Assert.assertEquals(false,
                OnetworkNetsimHandler.isNetsimVersionHigherThanCurrentNetsimVersion(minimumRequriedNetsimVersion, currentNetsimVersion));
    }

    @Test
    public void isNetsimVersionHigherThanCurrentNetsimVersion_with_HigherMajorRelease() {
        final String minimumRequriedNetsimVersion = "R29A";
        final String currentNetsimVersion = "R28G";
        Assert.assertEquals(true,
                OnetworkNetsimHandler.isNetsimVersionHigherThanCurrentNetsimVersion(minimumRequriedNetsimVersion, currentNetsimVersion));
    }

    @Test
    public void isNetsimVersionHigherThanCurrentNetsimVersion_with_HigherMinorRelease() {
        final String minimumRequriedNetsimVersion = "R28H";
        final String currentNetsimVersion = "R28G";
        Assert.assertEquals(true,
                OnetworkNetsimHandler.isNetsimVersionHigherThanCurrentNetsimVersion(minimumRequriedNetsimVersion, currentNetsimVersion));
    }

    @Test
    void getNetsimVersionFromSimName() {
        final String expectedNetsimVersion = "R25G";
        Assert.assertEquals(OnetworkNetsimHandler.getNetsimVersionFromSimName(SIM_NAME), expectedNetsimVersion);
    }

}
