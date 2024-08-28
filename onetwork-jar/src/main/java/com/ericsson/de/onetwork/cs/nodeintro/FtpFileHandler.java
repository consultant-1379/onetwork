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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles client connection to FTP server which hosts the MIM files.
 *
 * @author eephmar
 */
public class FtpFileHandler {

    private final static Logger logger = LoggerFactory.getLogger(FtpFileHandler.class);
    private final FTPClient ftpClient = new FTPClient();
    private final static String USER_NAME = "anonymous";
    private final static String PASSWORD = " ";

    // Default FTP server details.
    public static final String FTP_SERVER = "ftp.lmera.ericsson.se";
    public static final String PARENT_DIR = "/project/netsim-ftp/simulations/NEtypes/";
    public static final int PORT = 21;

    /**
     * Class constructor that connects client to default FTP server.
     */
    public FtpFileHandler() throws IOException {
        connectToFTPServer();
    }

    /**
     * Class constructor that takes inputs for connecting to the specified FTP
     * server.
     *
     * @param ftpServer
     *            the FTP server
     * @param port
     *            FTP port number
     */
    public FtpFileHandler(final String ftpServer, final int port) throws IOException {
        connectToFTPServer(ftpServer, port);
    }

    /**
     * Disconnects the currently logged in user from the FTP server.
     */
    public void disconnectFromFTPServer() {
        logger.debug("Disconnecting from FTP server..");
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (final IOException ex) {
            logger.error("Disconnecting from Ftp server failed: " + ex.getMessage());
        }
        logger.debug("Disconnected from FTP server.");
    }

    /**
     * Provides connectivity to the FTP server.
     *
     * @param server
     *            the FTP server
     * @param port
     *            the FTP port number
     */
    public void connectToFTPServer(final String server, final int port) throws IOException {
        ftpClient.connect(server, port);

        final int replyCode = getFtpclient().getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            logger.error("Failed Connecting to FTP Server: " + replyCode);
            throw new IOException("Failed Connecting to FTP Server.");
        }

        logger.debug("Connected to " + server);
        loginToFTPServer(USER_NAME, PASSWORD);
    }

    /**
     * Connects the client to default FTP server "ftp.lmera.ericsson.se"
     */
    public void connectToFTPServer() throws IOException {
        connectToFTPServer(FTP_SERVER, PORT);
    }

    /**
     * Logs the user in to the specified FTP server.
     *
     * @param username
     *            - The user's username
     * @param password
     *            - The user's password
     * @exception IOException
     */
    public void loginToFTPServer(final String username, final String password) throws IOException {
        final boolean success = ftpClient.login(username, password);

        if (!success) {
            logger.debug("Could not login to the server");
            throw new IOException("Could not login to ftp server.");

        }
    }

    /**
     * Retrieves all the files from the latest CPP directory on the FTP server
     *
     * @param parentDirectory
     *            - The parent directory where cpp directories are held
     * @return {@code List<FTPFile>} - List of all the files from the FTP server
     * @exception Exception
     */
    public List<FTPFile> getFilesFromFTPServer(final String parentDirectory) throws IOException {
        final FTPFile[] parentDirectoryFiles = getFtpclient().listFiles(parentDirectory);
        final String latestCPPDirectory = getLatestCPPDirectory(parentDirectoryFiles);
        String separator = "";
        if (!parentDirectory.endsWith("/")) {
            separator = File.separator;
        }
        final List<FTPFile> cppDirectoryFiles = Arrays.asList(getFtpclient().listFiles(parentDirectory + separator + latestCPPDirectory));
        disconnectFromFTPServer();
        return cppDirectoryFiles;
    }

    /**
     * Checks for directories that starts with 'cpp.*' and gets the most recent
     * directory by date.
     *
     * @throws Exception
     *             Thrown when no valid cpp directory exists.
     */

    public String getLatestCPPDirectory(final FTPFile[] files) throws IOException {
        FTPFile latestCPPDir = null;
        for (final FTPFile file : files) {
            if (file.isDirectory()) {
                final String dirName = file.getName();
                if (dirName.startsWith("cpp")) {
                    if (latestCPPDir == null) {
                        latestCPPDir = file;
                    } else {
                        final Date latestCPPDate = latestCPPDir.getTimestamp().getTime();
                        final Date dirDate = file.getTimestamp().getTime();
                        if (latestCPPDate.before(dirDate)) {
                            latestCPPDir = file;
                        }
                    }
                }
            }
        }

        if (latestCPPDir == null) {
            throw new IOException("No valid CPP directory found");
        }
        logger.debug("The latest CPP directory: " + latestCPPDir.getName());
        return latestCPPDir.getName();
    }

    /**
     * Checks if client is connected to the FTP server
     *
     * @return true if client is connected to the FTP server
     */

    public boolean isConnected() {
        return getFtpclient().isConnected();
    }

    /**
     * Returns the ftp client instance
     */
    public FTPClient getFtpclient() {
        return ftpClient;
    }

    /**
     * Returns path of the latest CPP directory as string.
     */
    public String getLatestCppDirectoryPath(final String parentDirectory) throws IOException {
        final FTPFile[] parentDirectoryFiles = getFtpclient().listFiles(parentDirectory);
        final String latestCPPDirectory = getLatestCPPDirectory(parentDirectoryFiles);
        String separator = "";
        if (!parentDirectory.endsWith("/")) {
            separator = File.separator;
        }
        return parentDirectory + separator + latestCPPDirectory + "/";
    }

    /**
     * Returns the FTP Server
     */
    public static String getFtpServer() {
        return FTP_SERVER;
    }

    /**
     * Returns the parent directory of the CPP directory
     */
    public static String getParentDir() {
        return PARENT_DIR;
    }
}
