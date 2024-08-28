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

// Resources
// http://stackoverflow.com/questions/28850188/ssh-tunneling-not-working-via-jsch
// http://bitfish.eu/java/control-your-server-over-ssh-with-java/
// http://www.beanizer.org/site/index.php/en/Articles/Java-ssh-tunneling-with-jsch.html
// http://www.jcraft.com/jsch/examples/Exec.java.html

package com.ericsson.de.onetwork.ss.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Generic ssh client provides copying files and running commands remotely.
 * <p>
 * Usage details:
 * // @formatter:off
 * <blockquote><pre>
 * SshClient sshClient = new SshClient();
 * try{
 *     sshClient.authUserPassword(user, pass);
 *     sshClient.connect(remoteHost);
 * ...// do your job
 * } finally{
 *     sshClient.close();
 * }
 * </pre></blockquote>
 * // @formatter:off
 *
 * @author qfatonu
 */
public class SshClient {

    private final static Logger logger = LoggerFactory.getLogger(SshClient.class);

    /**
     * In order to reach remote server we need following server details while
     * using tunneling
     */
    private static String TUNNNELING_HOST = "atrclin2.athtem.eei.ericsson.se";
    private static String TUNELLING_USER_NAME = "qfatonu";
    private static String TUNNELING_USER_PWD = "qfatonu";
    private static int TUNNELING_HOST_PORT_TO = 22;

    /** Remote host user password */
    private String password;

    /** Remote host user name */
    private String user;

    /** Remote host */
    private String host;

    /** Remote host private key */
    private Path privateKey;

    /** Default connection session */
    private Session firstSession;

    /** Default tunneling connection session */
    private Session secondSession;

    /** Defines that tunneling is used */
    private boolean tunneled = false;

    /**
     * Authenticates SshClient with given user name and password.
     *
     * @param user
     *            the remote host user name
     * @param password
     *            the remote host passowrd
     */
    public void authUserPassword(final String user, final String password) {
        this.user = user;
        this.password = password;
    }

    /**
     * Authenticates SshClient with given user and private key. In order to use
     * this feature end user has to copy the public key onto remote server's
     * machine. See the below link for more details.
     *
     * @param user
     *            the remote host user name
     * @param privateKey
     *            the private key used by user in order to connect without
     *            password to remote host
     * @see <a href=
     *      "https://www.digitalocean.com/community/tutorials/how-to-set-up-ssh-keys--2"
     *      >ssh keys setup</a>
     */
    public void authUserPublicKey(final String user, final Path privateKey) {
        this.user = user;
        this.privateKey = privateKey;
    }

    /**
     * Connects SshClient to remote host.
     *
     * @param host
     *            the remote host name
     * @throws IOException
     *             if connection fails
     */
    public void connect(final String host) throws IOException {
        this.host = host;
        if (firstSession == null || isTunneled() == false) {
            try {
                // allow connections to all hosts
                JSch.setConfig("StrictHostKeyChecking", "no");
                final JSch jsch = new JSch();
                firstSession = jsch.getSession(user, host);

                // create a session connected to port 2233 on the local host.
                if (privateKey != null) {
                    jsch.addIdentity(privateKey.toString());
                }

                if (password != null) {
                    firstSession.setPassword(password);
                } else if (privateKey == null) {
                    throw new IOException("Either privateKey nor password is set. Please call one of the authentication method.");
                }

                firstSession.connect();
                logger.debug("Connected directly to:{}", host);
                setTunneled(false);

            } catch (final JSchException ex) {
                throw new IOException("Connection error for " + host + ". Error detail:" +  ex.getMessage());
            }
        }

    }

    /**
     * Connects the SshClient to remote host via default tunneling host details.
     *
     * @param host
     *            the remote host where user wants to connect to
     * @throws IOException
     *             if connection fails
     */
    public void connectThroughDefaultTunnel(final String host) throws IOException {
        final int tunnelingHostPortFrom = getRandomPortAddr();
        this.host = host;
        if (secondSession == null) {
            try {
                // allow connections to all hosts
                JSch.setConfig("StrictHostKeyChecking", "no");

                final JSch jsch = new JSch();
                firstSession = jsch.getSession(TUNELLING_USER_NAME, TUNNNELING_HOST);
                firstSession.setPassword(TUNNELING_USER_PWD);

                firstSession.setPortForwardingL(tunnelingHostPortFrom, host, TUNNELING_HOST_PORT_TO);
                firstSession.connect();
                firstSession.openChannel("direct-tcpip");

                // create a session
                if (privateKey != null) {
                    jsch.addIdentity(privateKey.toString());
                }

                if (password != null) {
                    firstSession.setPassword(password);
                } else if (privateKey == null) {
                    throw new IOException("Either privateKey nor password is set. Please call one of the authentication method.");
                }

                secondSession = jsch.getSession(user, "localhost", tunnelingHostPortFrom);
                secondSession.setPassword(password);
                secondSession.setConfig("StrictHostKeyChecking", "no");

                secondSession.connect(); // now we're connected to the secondary system
                logger.debug("Connected from={}:{} to={}:{}", TUNNNELING_HOST,tunnelingHostPortFrom, host, TUNNELING_HOST_PORT_TO);

                setTunneled(true);

            } catch (final JSchException ex) {
                throw new IOException("Connection error for " + host + ". Error detail:" +  ex.getMessage());
            }
        }
    }

    /**
     * Executes commands and scripts remotely and returns the output as a String.
     *
     * @param command
     *            the command to be executed. For example "ls -la; cd /" or
     *            "/x/y/z/scriptName.sh"
     * @return
     * @throws IOException
     *             if command fails to execute
     */
    public int executeCommand(final String command) throws IOException {

        int exitStatus = -100;
        Channel channel = null;
        InputStream stdout = null;
        try {
            if (isTunneled()) {
                channel = secondSession.openChannel("exec");
            } else {
                channel = firstSession.openChannel("exec");
            }
            logger.debug("tunneled:{}", isTunneled());

            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            stdout = channel.getInputStream();

            // TODO: Redirect to a logger
            ((ChannelExec) channel).setErrStream(System.err);

            channel.connect();

            while (true) {
                final byte[] tmpArray = new byte[1024];
                while (stdout.available() > 0) {
                    final int i = stdout.read(tmpArray, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    final String stdOutput = new String(tmpArray, 0, i);
                    logger.debug("\n{}", stdOutput);
                }
                if (channel.isClosed()) {
                    if (stdout.available() > 0) {
                        continue;
                    }
                    exitStatus = channel.getExitStatus();

                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (final Exception ee) {
                    // unimportant time exception error
                }
            }
        } catch (final JSchException ex) {
            throw new IOException(ex);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }

            if (stdout != null) {
                stdout.close();
            }

        }
        logger.debug("exitStatus:{}", exitStatus);
        return exitStatus;
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

        final StringBuilder commandOutPut = new StringBuilder();

        int exitStatus = -100;
        Channel channel = null;
        InputStream stdout = null;
        try {
            if (isTunneled()) {
                channel = secondSession.openChannel("exec");
            } else {
                channel = firstSession.openChannel("exec");
            }
            logger.debug("tunneled:{}", isTunneled());

            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            stdout = channel.getInputStream();

            // TODO: Redirect to a logger
            ((ChannelExec) channel).setErrStream(System.err);

            channel.connect();

            while (true) {
                final byte[] tmpArray = new byte[1024];
                while (stdout.available() > 0) {
                    final int i = stdout.read(tmpArray, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    final String stdOutput = new String(tmpArray, 0, i);
                    commandOutPut.append(stdOutput);
                    logger.debug("\n{}", stdOutput);
                }
                if (channel.isClosed()) {
                    if (stdout.available() > 0) {
                        continue;
                    }
                    exitStatus = channel.getExitStatus();

                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (final Exception ee) {
                    // unimportant time exception error
                }
            }
        } catch (final JSchException ex) {
            throw new IOException(ex);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }

            if (stdout != null) {
                stdout.close();
            }

        }
        logger.debug("exitStatus:{}", exitStatus);
        return commandOutPut.toString();
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

        final int numOfFiles = paths.size();
        int numOfFilesCopiedSuccesfully = 0;

        ChannelSftp sftp = null;
        try {
            if (isTunneled()) {
                sftp = (ChannelSftp) secondSession.openChannel("sftp");
            } else {
                sftp = (ChannelSftp) firstSession.openChannel("sftp");
            }
            logger.debug("tunneled=" + isTunneled());

            sftp.connect();
            logger.debug("Connected remoteServer={}", host);

            final String remoteDestPathName = remoteDestPath.toString().replace("\\", "/");
            logger.debug("Trying to access folderName: {}", remoteDestPathName);
            sftp.cd("/");

            final String[] remoteDestFolderNames = remoteDestPathName.split("/");
            for (int i = 1; i < remoteDestFolderNames.length; i++) {
                final String folder = remoteDestFolderNames[i];

                if (folder.length() > 0) {
                    try {
                        logger.debug("Command to be executed: cd {}", folder);
                        sftp.cd(folder);
                    } catch (final SftpException e) {
                        logger.debug("Command to be executed: mkdir {}; cd {}", folder, folder);
                        sftp.mkdir(folder);
                        sftp.cd(folder);
                    }
                }
            }

            logger.debug("Start uploading");
            for (final Path path : paths) {
                try (final InputStream in = Files.newInputStream(path)) {
                    logger.debug("Uploading fileName:{}", path.getFileName());
                    sftp.put(in, path.getFileName().toString());
                    numOfFilesCopiedSuccesfully++;
                } catch (final IOException ex) {
                    logger.info("Error occured while reading file:{}", ex.getMessage());
                }
            }

            // upload the files
            sftp.disconnect();

        } catch (final JSchException | SftpException ex) {
            throw new IOException(ex);
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
        }
        return numOfFiles == numOfFilesCopiedSuccesfully;
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

        final int numOfFiles = 1;
        int numOfFilesCopiedSuccesfully = 0;

        ChannelSftp sftp = null;
        try {
            if (isTunneled()) {
                sftp = (ChannelSftp) secondSession.openChannel("sftp");
            } else {
                sftp = (ChannelSftp) firstSession.openChannel("sftp");
            }
            logger.debug("tunneled={}", isTunneled());

            sftp.connect();
            logger.debug("Connected remoteServer={}", host);

            logger.debug("Trying to copy file from->{}:{} to->{}", host, remoteFilePath.toString(), localDestFolderPath.toString() );
            sftp.get(remoteFilePath.toString(), localDestFolderPath.toString());
            numOfFilesCopiedSuccesfully++;
            sftp.disconnect();

        } catch (final JSchException | SftpException ex) {
            logger.error("Error occured while reading file:{}", ex.getMessage());
            throw new IOException(ex);
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
        }
        return numOfFiles == numOfFilesCopiedSuccesfully;
    }


    /**
     * Closes the all the open session in order to release the reosurces.
     */
    public void close() {

        if (secondSession != null) {
            secondSession.disconnect();
            secondSession = null;
        }
        if (firstSession != null) {
            firstSession.disconnect();
            firstSession = null;
        }
    }

    /**
     * Returns the tunneling session status.
     *
     * @return the tunneling session status
     */
    public boolean isTunneled() {
        return tunneled;
    }

    /**
     * Sets the tunneling session status
     *
     * @param tunneled
     *            the tunneled to set
     */
    public void setTunneled(final boolean tunneled) {
        this.tunneled = tunneled;

    }

    private int getRandomPortAddr(){
        return ThreadLocalRandom.current().nextInt(1024, 65535);
    }
}
