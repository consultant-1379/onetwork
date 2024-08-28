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

package com.ericsson.de.onetwork.util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for determining underneath execution server.
 *
 * @author edalrey
 * @author qfatonu
 * @since 1Network_15.14
 */
public class ServerUtility {

    private final static Logger logger = LoggerFactory.getLogger(ServerUtility.class);
    private final static String JENKINS_SERVER = "lmera.ericsson.se";
    private final static String PRODUCTION_SERVER = "netsimv006-04";
    private final static String MINT_VM = "atclvm12";

    /**
     * Determines if a build is occurring on a Jenkins server or elsewhere.
     *
     * @return true is build is currently running on a Jenkins server
     */
    public static boolean isRunningOnJenkinsServer() {
        try {
            final String hostname = InetAddress.getLocalHost().getHostName();
            logger.debug("Current host is: {}", hostname);
            return hostname.contains(JENKINS_SERVER);
        } catch (final UnknownHostException ex) {
            logger.error("The host is unknown: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Determines if the running environment is Windows.
     *
     * @return true if the current working environment is Windows
     */
    public static boolean isRunningOnWindowsServer() {
        try {
            final String hostname = InetAddress.getLocalHost().getHostName();
            logger.debug("Current host is: {}", hostname);
            // regular expression: first 2 character start with IE
            return Pattern.compile("^IE").matcher(hostname).find();
        } catch (final UnknownHostException ex) {
            logger.error("Error: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Determines if the running environment is a general NETSim box.
     *
     * @return true if the current working environment is NETSim box
     */
    public static boolean isRunningOnNetsimServerApartFromProductionServer() {
        try {
            final String hostname = InetAddress.getLocalHost().getHostName();
            logger.debug("Current host is: {}", hostname);
            // regular expression: first 5 character start with netsim
            return Pattern.compile("^netsim").matcher(hostname).find() && !isRunningOnProductionNetsimServer();
        } catch (final UnknownHostException ex) {
            logger.error("Error: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Determines if the running environment is a Production NETSim box.
     *
     * @return true if the current working environment is a Production NETSim
     *         box
     */
    public static boolean isRunningOnProductionNetsimServer() {
        try {
            final String hostname = InetAddress.getLocalHost().getHostName();
            logger.debug("Current host is: {}", hostname);
            return Pattern.compile(PRODUCTION_SERVER).matcher(hostname).find();
        } catch (final UnknownHostException ex) {
            logger.error("Error: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Determines if the running environment is a development Mint VM.
     *
     * @return true if the current working environment is a development Mint VM.
     */
    public static boolean isRunningOnMintVM() {
        try {
            final String hostname = InetAddress.getLocalHost().getHostName();
            logger.debug("Current host is: {}", hostname);
            return Pattern.compile(MINT_VM).matcher(hostname).find();
        } catch (final UnknownHostException ex) {
            logger.error("Error: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Retrieves the absolute path for the file after it is downloaded to a
     * server.
     *
     * @param resourcesDir
     *            name of the resource directory
     * @param requiredFileName
     *            name of the file
     * @return the absolute path for the file after it is downloaded to a server
     */
    public static String getAbsolutePathOnServer(final String resourcesDir, final String requiredFileName) {
        final File destinationFile = new File(requiredFileName);
        final String destinationFileAbsolutePath = destinationFile.getAbsolutePath();
        logger.debug("Absolute path of {} is {}", requiredFileName, destinationFileAbsolutePath);
        return destinationFileAbsolutePath;
    }

    /**
     * Observing static utility class methods output.
     *
     * @param args
     *            unused
     */
    public static void main(final String... args) {
        logger.debug("isRunningOnJenkinsServer():{}", isRunningOnJenkinsServer());
        logger.debug("isRunningOnWindowsServer():{}", isRunningOnWindowsServer());
        logger.debug("isRunningOnProductionNetsimServer():{}", isRunningOnProductionNetsimServer());
        logger.debug("isRunningOnNetsimServerApartFromProductionServer():{}", isRunningOnNetsimServerApartFromProductionServer());
        logger.debug("isRunningOnMintVM():{}", isRunningOnMintVM());
    }
}
