/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.de.onetwork.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.ss.util.SshClient;

/**
 * Wrapper SSH client for ONETWORK environment only. It will enable easier
 * update when NETSim server credentials has changed.
 *
 * @author qfatonu
 */
public class OnetworkSshClient {

    private final static Logger logger = LoggerFactory.getLogger(OnetworkSshClient.class);

    public final static String NETSIM_SERVER = "netsimv006-04.athtem.eei.ericsson.se";
    public final static String NETSIM_USERNAME = "netsim";
    public final static String NETSIM_PASSWORD = "netsim";

    private final SshClient sshClient;

    /**
     * Initialize newly created this object.
     */
    public OnetworkSshClient() {
        sshClient = new SshClient();
    }

    public void close() {
        sshClient.close();
    }

    /**
     * Executes commands and scripts remotely and returns the output as a
     * String.
     *
     * @param command
     *            the command to be executed. For example "ls -la; cd /" or
     *            "/x/y/z/scriptName.sh"
     * @return
     * @throws IOException
     *             if command fails to execute
     */
    public int executeCommand(final String command) throws IOException {
        authenticateAndConnectToRemoteServer();
        return sshClient.executeCommand(command);
    }

    /**
     * Executes commands and scripts remotely and displays output through logs.
     *
     * @param command
     *            the command to be executed. For example "ls -la; cd /" or
     *            "/x/y/z/scriptName.sh"
     * @return
     * @throws IOException
     *             if command fails to execute
     */
    public String executeCommandv2(final String command) throws IOException {
        authenticateAndConnectToRemoteServer();
        return sshClient.executeCommandv2(command);
    }

    /**
     * Copies list of files to remote host.
     *
     * @param paths
     *            the list of path of files
     * @param remoteDestPath
     *            remote destination folder
     * @return true if all files are copied successfully
     * @throws IOException
     *             if copy operation files
     */
    public boolean copyFiles(final List<Path> paths, final Path remoteDestPath) throws IOException {
        authenticateAndConnectToRemoteServer();
        return sshClient.copyFiles(paths, remoteDestPath);
    }

    /**
     * Copies requested file from remote host to local destination.
     *
     * @param paths
     *            the list of path of files
     * @param localDestFolderPath
     *            local destination folder
     * @return true if all files are copied successfully
     * @throws IOException
     *             if copy operation files
     */
    public boolean copyFrom(final String remoteFilePath, final String localDestFolderPath) throws IOException {
        authenticateAndConnectToRemoteServer();
        return sshClient.copyFrom(remoteFilePath, localDestFolderPath);
    }

    private void authenticateAndConnectToRemoteServer() throws IOException {
        logger.debug("Authanticate and connect to={}", NETSIM_SERVER);

        sshClient.authUserPassword(NETSIM_USERNAME, NETSIM_PASSWORD);

        if (ServerUtility.isRunningOnWindowsServer()
                || ServerUtility.isRunningOnJenkinsServer()) {
            sshClient.connectThroughDefaultTunnel(NETSIM_SERVER);
        } else if (ServerUtility.isRunningOnProductionNetsimServer()
                || ServerUtility.isRunningOnNetsimServerApartFromProductionServer()) {
            sshClient.connect(NETSIM_SERVER);
        } else {
            sshClient.connect(NETSIM_SERVER);
        }
    }
}
