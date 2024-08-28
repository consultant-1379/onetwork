
package com.ericsson.de.onetwork.ss.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.util.ServerUtility;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/**
 * Verifies all SshClient methods.
 *
 * @author qfatonu
 */
public class SshClientTest {

    private final static Logger logger = LoggerFactory.getLogger(SshClientTest.class);

    // Non-direct access from local windows machines
    private static String USER_1 = "netsim";
    private static String USER_1_PWD = "netsim";
    private static String USER_1_HOST = "netsimv006-04.athtem.eei.ericsson.se";

    // Direct access from local windows machines
    private static String USER_2 = "qfatonu";
    private static String USER_2_PWD = "qfatonu";
    private static String USER_2_HOST = "atrclin2.athtem.eei.ericsson.se";

    private static String COMMAND = "hostname; pwd ";

    private SshClient sshClient;

    @BeforeMethod
    public void beforeMethod() {
        sshClient = new SshClient();
    }

    @AfterMethod
    public void afterMethod() {
        sshClient.close();
    }

    @Test
    public void authUserPasswordAndConnect() throws IOException {
        if (ServerUtility.isRunningOnWindowsServer() || ServerUtility.isRunningOnJenkinsServer()) {
            sshClient.authUserPassword(USER_2, USER_2_PWD);
            sshClient.connect(USER_2_HOST);
        } else if (ServerUtility.isRunningOnNetsimServerApartFromProductionServer()) {
            sshClient.authUserPassword(USER_1, USER_1_PWD);
            sshClient.connect(USER_1_HOST);
        }
    }

    @Test
    public void executeCommand() throws IOException {
        if (ServerUtility.isRunningOnWindowsServer()
                || ServerUtility.isRunningOnJenkinsServer()) {
            sshClient.authUserPassword(USER_2, USER_2_PWD);
            sshClient.connect(USER_2_HOST);
        } else if (ServerUtility.isRunningOnNetsimServerApartFromProductionServer()) {
            sshClient.authUserPassword(USER_1, USER_1_PWD);
            sshClient.connect(USER_1_HOST);
        } else {
            sshClient.authUserPassword(USER_1, USER_1_PWD);
            sshClient.connect(USER_1_HOST);
        }

        sshClient.executeCommand(COMMAND);
    }

    @Test
    public void authUserPasswordAndConnectThroughDefaultTunnel() throws IOException {
        sshClient.authUserPassword(USER_1, USER_1_PWD);
        sshClient.connectThroughDefaultTunnel(USER_1_HOST);
    }

    @Test
    public void executeCommandThroughDefaultTunnel() throws IOException, JSchException {
        sshClient.authUserPassword(USER_1, USER_1_PWD);
        sshClient.connectThroughDefaultTunnel(USER_1_HOST);

        sshClient.executeCommand(COMMAND);
    }

    @Test
    public void copyFiles() throws IOException {
        if (ServerUtility.isRunningOnWindowsServer()
                || ServerUtility.isRunningOnJenkinsServer()) {
            sshClient.authUserPassword(USER_2, USER_2_PWD);
            sshClient.connect(USER_2_HOST);
        } else if (ServerUtility.isRunningOnNetsimServerApartFromProductionServer()) {
            sshClient.authUserPassword(USER_1, USER_1_PWD);
            sshClient.connect(USER_1_HOST);
        } else {
            sshClient.authUserPassword(USER_1, USER_1_PWD);
            sshClient.connect(USER_1_HOST);
        }
        final Path parentFolder = Paths.get("src/test/resources/ss/util");

        final Path remoteDestPath = Paths.get("/tmp/test/onenetwork/");

        final List<Path> paths = FileUtils.getSourceFiles(parentFolder);

        sshClient.copyFiles(paths, remoteDestPath);

    }

    @Test
    public void copyFilesThroughDefaultTunnel() throws IOException, JSchException, SftpException {
        sshClient.authUserPassword(USER_1, USER_1_PWD);
        sshClient.connectThroughDefaultTunnel(USER_1_HOST);

        final Path parentFolder = Paths.get("src/test/resources/ss/util");
        final Path remoteDestPath = Paths.get("/tmp/test/onenetwork/");

        final List<Path> paths = FileUtils.getSourceFiles(parentFolder);

        sshClient.copyFiles(paths, remoteDestPath);

    }

    @Test
    public void executeScriptThroughDefaultTunnel() throws IOException, JSchException, SftpException {
        sshClient.authUserPassword(USER_1, USER_1_PWD);
        sshClient.connectThroughDefaultTunnel(USER_1_HOST);

        final Path scriptPath1 = Paths.get("src/test/resources/ss/util/simple_script.sh");

        final Path remoteDestPath1 = Paths.get("/tmp/test/script/onenetwork");

        final List<Path> paths = new ArrayList<>();
        paths.add(scriptPath1);

        sshClient.copyFiles(paths, remoteDestPath1);

        final String script = remoteDestPath1.toString().replace("\\", "/") + "/" + scriptPath1.getFileName().toString();
        logger.debug("script:{}", script);

        final String cmd1 = "chmod +x " + script + "; perl -i -pe 's/\\r//g' " + script + ";" + script;
        sshClient.executeCommand(cmd1);
    }

    @Test
    public void executeScriptThroughDefaultTunnelWithReturnOutput() throws IOException, JSchException, SftpException {
        sshClient.authUserPassword(USER_1, USER_1_PWD);
        sshClient.connectThroughDefaultTunnel(USER_1_HOST);

        final String cmd1 = "ls ~ / wc -l";
        final String commandOutput = sshClient.executeCommandv2(cmd1);
        logger.debug("commandOutput:{}", commandOutput);
    }

    @Test
    public void copyFromRemoteServerToLocalhostThroughDefaultTunnel() throws IOException {

        sshClient.authUserPassword(USER_1, USER_1_PWD);
        sshClient.connectThroughDefaultTunnel(USER_1_HOST);

        final String remoteFile = "file01AtRemoteServer.txt";
        final String remoteFilePath = "/tmp/test/onenetwork/" + remoteFile;

        final String resourcePath = SshClientTest.class.getResource("/ss/util").getPath();
        final String localDestPath = FileUtils.getPlatformIndependentPathString(resourcePath);

        logger.debug("full_localDestPath: {}", localDestPath + "/" + remoteFile);

        sshClient.executeCommand("touch " + remoteFilePath);
        sshClient.copyFrom(remoteFilePath, localDestPath);

    }

    @Test
    public void copyFromRemoteServerToLocalhost() throws IOException {
        if (ServerUtility.isRunningOnWindowsServer()
                || ServerUtility.isRunningOnJenkinsServer()) {
            sshClient.authUserPassword(USER_2, USER_2_PWD);
            sshClient.connect(USER_2_HOST);
        } else if (ServerUtility.isRunningOnNetsimServerApartFromProductionServer()) {
            sshClient.authUserPassword(USER_1, USER_1_PWD);
            sshClient.connect(USER_1_HOST);
        } else {
            sshClient.authUserPassword(USER_1, USER_1_PWD);
            sshClient.connect(USER_1_HOST);
        }
        final String remoteFilePath = "/tmp/test/onenetwork/file02AtRemoteServer.txt";
        final String resourceFolder = SshClientTest.class.getResource("/ss/util").getPath();
        final String localDestPath = FileUtils.getPlatformIndependentPathString(resourceFolder);

        sshClient.executeCommand("touch " + remoteFilePath);
        sshClient.copyFrom(remoteFilePath, localDestPath);

    }
}
