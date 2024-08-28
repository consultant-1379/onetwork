
package com.ericsson.de.onetwork.cs.nodeintro;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.cs.exceptions.GetLatestMimsException;
import com.ericsson.de.onetwork.cs.nodeintro.LatestErbsMimsHandler;

/**
 * Unit tests for LatestErbsMimsHandler class
 *
 * @author eephmar
 */
public class LatestMimsHandlerTest {
    private final static Logger logger = LoggerFactory.getLogger(LatestMimsHandlerTest.class);
    private final static String USER_NAME = "anonymous";
    private final static String PASSWORD = " ";
    private final static String PARENT_PATH = "src/test/resources/cs/ftp/";
    LatestErbsMimsHandler mimHandler;
    List<FTPFile> ftpFiles = new ArrayList<>();
    FakeFtpServer stubFtpServer = new FakeFtpServer();

    @BeforeTest
    public void setupFakeFtpServer() throws Exception {
        final String filePath = PARENT_PATH + "cpp9/";
        stubFtpServer.addUserAccount(new UserAccount(USER_NAME, PASSWORD, new File(PARENT_PATH).getAbsolutePath()));
        logger.debug("Dir is: " + new File(filePath).getAbsolutePath());
        final FileSystem fileSystem = SystemUtils.IS_OS_WINDOWS ? new WindowsFakeFileSystem() : new UnixFakeFileSystem();
        final File dirs = new File(filePath);
        if (!dirs.exists()) {
            if (dirs.mkdirs()) {
                logger.debug("Multiple directories are created!");
            } else {
                logger.debug("Failed to create multiple directories!");
            }
        }
        final File file1 = new File(filePath + "ERBSA1_R28A.zip");
        file1.createNewFile();
        final File file2 = new File(filePath + "ERBSA2_R28A.zip");
        file2.createNewFile();
        fileSystem.add(new FileEntry(file1.getAbsolutePath()));
        fileSystem.add(new FileEntry(file2.getAbsolutePath()));
        stubFtpServer.setFileSystem(fileSystem);
        stubFtpServer.setServerControlPort(0);
        stubFtpServer.start();
        mimHandler = new LatestErbsMimsHandler("localhost", stubFtpServer.getServerControlPort(), new File(PARENT_PATH).getAbsolutePath());
    }

    @BeforeClass
    public void setUpFtpFiles() throws IOException {

        final Map<String, Calendar> ftpFileMap = new LinkedHashMap<>();
        ftpFileMap.put("ERBSG123-V1_R25.zip", new GregorianCalendar(2015, 10, 10, 10, 10));
        ftpFileMap.put("ERBSG123-V2_R25.zip", new GregorianCalendar(2015, 11, 10, 10, 10));
        ftpFileMap.put("ERBSG123-V2lim_R25.zip", new GregorianCalendar(2015, 11, 10, 10, 10));
        ftpFileMap.put("ERBSZ456_R25.zip", new GregorianCalendar(2015, 12, 10, 10, 10));
        ftpFileMap.put("MGwZ34_R25.zip", new GregorianCalendar(2015, 11, 10, 10, 10));

        for (final Map.Entry<String, Calendar> entry : ftpFileMap.entrySet()) {
            final FTPFile file = new FTPFile();
            file.setName(entry.getKey());
            file.setTimestamp(entry.getValue());
            ftpFiles.add(file);
        }
    }

    @AfterMethod
    public void clearMims() throws IOException {
        LatestErbsMimsHandler.getNodeMap().clear();
    }

    @Test
    public void testGettingNetsimVersionByMimName() throws IOException {
        mimHandler.setNodeMap("ERBSG1220-lim", "R27E");
        final String netsimVersion = mimHandler.getNetsimVersionFromSimulationName("ERBSG1220-lim_R27E.zip");
        assertEquals("R27E", netsimVersion);
    }

    @Test
    public void testGettingMimNames() throws IOException {
        mimHandler.setNodeMap("ERBSG1220-lim", "R27E");
        final String mims = mimHandler.getLatestMimNames();
        logger.debug("the mims: " + mims);
        assertEquals("{ \"lteNodes\":[ {\"name\":\"ERBSG1220-lim\"}]}", mims);
    }

    @Test
    public void testLatestMimBeingLimitedAndCurrentNot() throws IOException {
        final String latestMim = mimHandler.getLatestMim("ERBSG1220-lim", "ERBSG1220");
        assertEquals("ERBSG1220", latestMim);

    }

    @Test
    public void testLatestMimBeingLimitedAndCurrentNotButOfLowerVersion() throws IOException {
        final String latestMim = mimHandler.getLatestMim("ERBSG1220-V2lim", "ERBSG1220-V1");
        assertEquals("ERBSG1220-V1", latestMim);

    }

    @Test
    public void testLatestMimHaveLowerVersionThanCurrent() throws IOException {
        final String latestMim = mimHandler.getLatestMim("ERBSG1220-V2", "ERBSG1220-V99");
        assertEquals("ERBSG1220-V99", latestMim);

    }

    @Test
    public void testGettingLatestMimWithCurrentHavingLowerVersion() throws IOException {
        final String latestMim = mimHandler.getLatestMim("ERBSG1220-V3", "ERBSG1220-V2");
        assertEquals("ERBSG1220-V3", latestMim);
    }

    @Test
    public void testGetSimulationName() throws IOException {
        mimHandler.setNodeMap("ERBSA1", "R28A");
        final String actual = mimHandler.getSimulationName("LTE ERBS A1");
        assertEquals("ERBSA1_R28A.zip", actual);
    }

    @Test
    public void testFilterFTPFilesFromFTPServerNotContainInvalidMimsOrLowerVersions() throws GetLatestMimsException {
        mimHandler.filterFtpFilesForLteMims(ftpFiles);
        logger.debug("node values: " + LatestErbsMimsHandler.getNodeMap().keySet());
        assertFalse(LatestErbsMimsHandler.getNodeMap().containsKey("ERBSG123-V1"));
        assertFalse(LatestErbsMimsHandler.getNodeMap().containsKey("ERBSG123-V2lim"));
        assertFalse(LatestErbsMimsHandler.getNodeMap().containsKey("M-MGwA01"));
    }

    @Test
    public void testOrderOfMims() throws IOException, GetLatestMimsException {
        final String[] mimInOrder = { "ERBSZ456", "ERBSG123-V2" };
        final List<String> mimListInOrder = new ArrayList<String>(Arrays.asList(mimInOrder));
        mimHandler.filterFtpFilesForLteMims(ftpFiles);
        final Iterator<String> mimIterator = mimListInOrder.iterator();

        for (final Map.Entry<String, String> entry : LatestErbsMimsHandler.getNodeMap().entrySet()) {
            logger.debug(entry.getKey());
            assertEquals(mimIterator.next(), entry.getKey());
        }
    }
}
