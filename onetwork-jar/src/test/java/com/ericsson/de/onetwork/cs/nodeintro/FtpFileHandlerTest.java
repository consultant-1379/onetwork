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

package com.ericsson.de.onetwork.cs.nodeintro;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.net.ftp.FTPClient;
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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.cs.nodeintro.FtpFileHandler;

/**
 * Unit tests for FtpFileHandler class
 *
 * @author eephmar
 */
public class FtpFileHandlerTest {
    private final static Logger logger = LoggerFactory.getLogger(FtpFileHandlerTest.class);
    private final static String USER_NAME = "anonymous";
    private final static String PASSWORD = " ";
    private final static String PARENT_PATH = "src/test/resources/cs/ftp/";
    FtpFileHandler ftpHandler;
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
        ftpHandler = new FtpFileHandler("localhost", stubFtpServer.getServerControlPort());
    }

    @AfterTest
    public void tearDown() {
        stubFtpServer.stop();
    }

    @BeforeMethod
    public void connectToFTP() throws IOException {
        if (!ftpHandler.isConnected()) {
            ftpHandler.connectToFTPServer("localhost", stubFtpServer.getServerControlPort());
        }
    }

    @AfterMethod
    public void disconnectFromFTP() {
        if (ftpHandler.isConnected()) {
            ftpHandler.disconnectFromFTPServer();
        }
    }

    @Test
    public void testConnectToFtpServerSuccess() throws IOException {
        assertTrue(ftpHandler.getFtpclient().isConnected());
    }

    @Test(expectedExceptions = IOException.class)
    public void testConnectToFtpServerFail() throws IOException {
        ftpHandler.connectToFTPServer("server does not exist", 21);
    }

    @Test
    public void testDisconnectFromFtpServer() throws IOException {
        ftpHandler.disconnectFromFTPServer();
        assertFalse(ftpHandler.getFtpclient().isConnected());
    }

    @Test
    public void testLoginToFtpServerSuccess() throws IOException {
        assertTrue(ftpHandler.getFtpclient().login("anonymous", " "));
    }

    @Test(expectedExceptions = IOException.class)
    public void testLoginToFtpServeFail() throws IOException {
        ftpHandler.loginToFTPServer("invalid user name", " ");
    }

    @Test
    public void testGetLatestCPPDirectoryWhereCPPExists() throws Exception {
        final RemoteFile remoteFile = new RemoteFile();
        final int port = stubFtpServer.getServerControlPort();
        remoteFile.setServer("localhost");
        remoteFile.setPort(port);
        final FTPFile[] ftpFiles = remoteFile.listFiles(new File(PARENT_PATH).getAbsolutePath());
        assertEquals("cpp9", ftpHandler.getLatestCPPDirectory(ftpFiles));
    }

    @Test(expectedExceptions = Exception.class)
    public void testCPPDirectoryDoNotExist() throws Exception {
        final FTPFile[] files = new FTPFile[1];
        ftpHandler.getLatestCPPDirectory(files);
    }

    @Test
    public void testGetLatestCPPDirectoryPathWherePathExists() throws Exception {
        final String expectedPath = new File(PARENT_PATH).getAbsolutePath() + File.separator + "cpp9/";
        final String actualPath = ftpHandler.getLatestCppDirectoryPath(new File(PARENT_PATH).getAbsolutePath());
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void testGetFilesFromFTPServer() throws Exception {
        ftpHandler.connectToFTPServer("localhost", stubFtpServer.getServerControlPort());

        final int port = stubFtpServer.getServerControlPort();
        final RemoteFile remoteFile = new RemoteFile();
        remoteFile.setServer("localhost");
        remoteFile.setPort(port);

        final FTPFile[] files = remoteFile.listFiles(new File(PARENT_PATH + "/cpp9/").getAbsolutePath());
        final List<FTPFile> ftpFiles = Arrays.asList(files);

        final String absoultePath = new File(PARENT_PATH).getAbsolutePath();
        final List<FTPFile> retrivedFtpFiles = ftpHandler.getFilesFromFTPServer(absoultePath);
        assertEquals(ftpFiles.size(), retrivedFtpFiles.size());
    }

    class RemoteFile {

        private String server;
        private int port;

        public String readFile(final String filename) throws IOException {

            final FTPClient ftpClient = new FTPClient();
            ftpClient.connect(server, port);

            if (ftpClient.isConnected()) {
                ftpClient.login(USER_NAME, PASSWORD);
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final boolean success = ftpClient.retrieveFile(filename, outputStream);
                ftpClient.disconnect();

                if (!success) {
                    throw new IOException("Retrieve file failed: " + filename);
                }
                return outputStream.toString();
            }

            return null;
        }

        public FTPFile[] listFiles(final String directory) throws IOException {
            final FTPClient ftpClient = new FTPClient();
            ftpClient.connect(server, port);
            ftpClient.login(USER_NAME, PASSWORD);
            final FTPFile[] files = ftpClient.listFiles(directory);
            ftpClient.disconnect();
            return files;

        }

        public void setServer(final String server) {
            this.server = server;
        }

        public void setPort(final int port) {
            this.port = port;
        }
    }
}
