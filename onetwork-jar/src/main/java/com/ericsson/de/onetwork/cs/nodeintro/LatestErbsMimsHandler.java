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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.cs.exceptions.GetLatestMimsException;

/**
 * Handles the filtering of FTP files to produce a list of the latest MIMs as
 * per MIM version.
 *
 * @author eephmar
 */
public class LatestErbsMimsHandler {
    private final static Logger logger = LoggerFactory.getLogger(LatestErbsMimsHandler.class);
    private final static Map<String, String> nodeMap = new LinkedHashMap<>();
    private final static int NUM_OF_MIM = 10;

    private final String ftpServer;
    private final int port;
    private final String parentDirectory;
    private final int numOfMims;

    private FtpFileHandler ftpHandler;

    /**
     * Class constructor that takes inputs for connecting and retrieving files
     * from the FTP server.
     *
     * @param ftpServer
     *            the FTP server that hosts all the simulation zip files
     * @param port
     *            the FTP port number
     * @param parentDirectory
     *            parent directory of all CPP directories
     * @param numOfMims
     *            number of MIM file to be created
     * @throws IOException
     *             if server communication fails
     */
    public LatestErbsMimsHandler(final String ftpServer, final int port, final String parentDirectory, final int numOfMims) throws IOException {
        this.ftpServer = ftpServer;
        this.port = port;
        this.parentDirectory = parentDirectory;
        this.numOfMims = numOfMims;
        connectToMimFilesFtpServer();
        createNodeMap();
    }

    /**
     * Class constructor that takes inputs for connecting and retrieving files
     * from the FTP server. The default num of of MIM file is 10.
     *
     * @param ftpServer
     *            the FTP server that hosts all the simulation zip files
     * @param port
     *            the FTP port number
     * @param parentDirectory
     *            parent directory of all CPP directories
     * @throws IOException
     *             if server communication fails
     */

    public LatestErbsMimsHandler(final String ftpServer, final int port, final String parentDirectory) throws IOException {
        this(ftpServer, port, parentDirectory, NUM_OF_MIM);
    }

    private void connectToMimFilesFtpServer() throws IOException {
        ftpHandler = new FtpFileHandler();
        ftpHandler.connectToFTPServer(ftpServer, port);
    }

    /**
     * Returns simulation name of the MIM if the MIM is contained in the
     * {@code nodeMap }
     *
     * @param mimName
     *            name of MIM in the form of "LTE ERBS A110"
     * @return simulationName
     *         name of simulation in the format "ERBSA110_R25A.zip". Returns
     *         null if MIM is not found in the map.
     */
    public String getSimulationName(final String mimName) {
        final String[] names = mimName.split(" ");
        final String simplifiedName = names[1] + names[2];
        String simulationName = null;

        if (nodeMap.containsKey(simplifiedName)) {
            final String version = getNodeMap().get(simplifiedName);
            simulationName = simplifiedName + "_" + version + ".zip";
        }
        return simulationName;
    }

    /**
     * Gets the NETSim version of the MIM file.
     *
     * @param simName
     *            The name of the MIM that the client wants to get the NETSim
     *            version of
     * @return the NETSim version
     */
    public String getNetsimVersionFromSimulationName(final String simName) {
        final String simlifiedSimName = simName.substring(0, simName.indexOf('.'));
        final String netsimVersion = simlifiedSimName.split("_")[1];
        logger.debug("Netsim version for " + simName + " is: " + netsimVersion);
        return netsimVersion;
    }

    /**
     * This method returns json string respresentation of the Top 10 LTE Nodes
     * taken from the ftp site.
     *
     * @return nodesToJson - The top 10 lte mims in json string format
     */
    public String getLatestMimNames() {
        final StringBuilder sb = new StringBuilder();
        String separator = "";

        for (final Map.Entry<String, String> entry : nodeMap.entrySet()) {
            final String name = entry.getKey();
            sb.append(separator);
            sb.append("{\"name\":" + "\"" + name + "\"}");
            separator = ",";
        }

        final String mimNamesToJson = "{ \"lteNodes\":[ " + sb.toString() + "]}";
        logger.debug("Top Mims in json format: " + mimNamesToJson);
        return mimNamesToJson;

    }

    private void createNodeMap() throws GetLatestMimsException, IOException {
        if (nodeMap.isEmpty()) {
            filterFtpFilesForLteMims(ftpHandler.getFilesFromFTPServer(parentDirectory));
            if (nodeMap.size() < 1) {
                throw new GetLatestMimsException("Unable to retrieve any files from the server.");
            }
        }
    }

    /**
     * Returns a Matcher object e.g. anything that is ERBS.
     *
     * @param fileName
     *            the name of the file that needs to be matched to the pattern
     */
    private static Matcher getErbsNodeMatcher(final String fileName) {
        final String patternString = "(ERBS)([A-Z])([0-9]+)(.*)_(.*)(\\.zip)";
        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(fileName);
        return matcher;
    }

    /**
     * TODO: Filter should only filter the contents
     * Filters the all files from the FTP server to get only valid LTE MIMs and
     * stores it into a map.
     *
     * @param ftpFiles
     *            all files from FTP server's latest CPP directory
     */
    public void filterFtpFilesForLteMims(final List<FTPFile> ftpFiles) {

        final List<FTPFile> mimList = new ArrayList<>();
        populateErbsMimList(ftpFiles, mimList);

        // Sort the mims in descending order to match sorting of valid mim
        // version set.
        sortMimListByDate(mimList);

        // after sorting by date, take the top 10 mims based on the groups.
        for (int groupIndex = 0; groupIndex < mimList.size() && nodeMap.size() < numOfMims; groupIndex++) {
            setMimVersionsOfLatestMims(mimList, groupIndex);
        }

        int i = 1;
        final StringBuilder output = new StringBuilder();
        for (final String key : nodeMap.keySet()) {
            output.append(i++ + ": " + key + "\n");
        }
        logger.debug("Output= \n{}", output);
    }

    private void setMimVersionsOfLatestMims(final List<FTPFile> mimList, final int groupIndex) {

        final String mim = mimList.get(groupIndex).getName();
        final String currentGroup = setCurrentGroup(mim);
        String latestMim = null;
        String latestMimNetsimVersion = "";

        for (int mimIndex = 0; mimIndex < mimList.size(); mimIndex++) {
            final String currentMimFile = mimList.get(mimIndex).getName();
            final String currentMimGroup = setCurrentGroup(currentMimFile);

            if (currentGroup.equals(currentMimGroup)) {
                latestMim = initializeLatestMim(latestMim, currentMimFile);
                final Matcher matcher = getErbsNodeMatcher(currentMimFile);
                if (matcher.matches()) {
                    latestMimNetsimVersion = matcher.group(5);
                }
            }
        }
        latestMim = getSimplifiedMimName(latestMim);
        nodeMap.put(latestMim, latestMimNetsimVersion);
    }

    private String setCurrentGroup(final String mim) {
        Matcher matcher;
        String currentGroup = "";
        matcher = getErbsNodeMatcher(mim);

        if (matcher.matches()) {
            final String groupLetter = matcher.group(2);
            final String groupNumber = matcher.group(3);
            currentGroup = groupLetter + groupNumber;
        }
        return currentGroup;
    }

    /**
     * Initializes latest Mim.
     */
    private String initializeLatestMim(String latestMim, final String currentMim) {

        latestMim = latestMim == null ? currentMim : getLatestMim(latestMim, currentMim);
        return latestMim;
    }

    /**
     * Simplifies the Latest MIM's name to exclude Netsim Version and .zip
     * keywords:
     * e.g. ERBSA930-V3-lim_R27A.zip becomes ERBSA930-V3-lim.
     */
    private String getSimplifiedMimName(String latestMim) {

        final Matcher m = getErbsNodeMatcher(latestMim);
        if (m.matches()) {
            latestMim = m.group(1) + m.group(2) + m.group(3) + m.group(4);
        }
        return latestMim;
    }

    /**
     * Filters ftp files retrieved from the server to only get ERBS nodes and
     * stores valid mim versions and the valid ERBS files
     * for further processing.
     *
     * @param ftpFiles
     *            All files from the latest cpp directory from the FTP server
     */
    private void populateErbsMimList(final List<FTPFile> ftpFiles, final List<FTPFile> mimList) {

        Matcher m = null;
        for (final FTPFile file : ftpFiles) {
            m = getErbsNodeMatcher(file.getName());
            if (m.matches()) {
                mimList.add(file);
            }
        }
    }

    /**
     * Determines the latest MIM based on their MIM version.
     * If there are multiple versions of a MIM file, it takes the most recent
     * Version, preferably
     * the Full version (without -lim)
     *
     * @param latestMim
     *            The MIM that is currently set as the most recent version of
     *            the MIM
     *            e.g. if there are multiple versions of a MIM e.g.:
     *            ERBSA1, ERBSA1-V1, ERBSA1-V2,
     *            then ERBSA1-V2 is the most recent. Also if multiple versions
     *            exist, and there are limited version with higher version
     *            number
     *            e.g. ERBSA1-V1, ERBSA1-V99-lim, the full version is still
     *            preferred
     * @param currentMim
     *            The current MIM
     */
    public String getLatestMim(String latestMim, final String currentMim) {

        // Pattern to check if there is a Version of mim
        final Pattern patternVersion = Pattern.compile("(ERBS.*-)(V)([0-9]+)(.*)");
        final Matcher latestMatcher = patternVersion.matcher(latestMim);
        final Matcher currentMatcher = patternVersion.matcher(currentMim);
        if (!currentMim.contains("lim") && latestMim.contains("lim")) {
            latestMim = currentMim;

        } else if (latestMatcher.matches() && currentMatcher.matches()) {
            // e.g. To cover instances like ERBSE163. Set current to latest if
            // version is higher than latest
            final int currentMimVersionNumber = Integer.parseInt(currentMatcher.group(3));
            final int latestMimVersionNumber = Integer.parseInt(latestMatcher.group(3));
            if (currentMimVersionNumber > latestMimVersionNumber) {
                latestMim = currentMim;
            }
        }

        return latestMim;
    }

    /**
     * Sorts the mim files in descending order by date and grouped by mim
     * version)
     */
    private List<FTPFile> sortMimListByDate(final List<FTPFile> lteFiles) {

        Collections.sort(lteFiles, new Comparator<FTPFile>() {
            @Override
            public int compare(final FTPFile object1, final FTPFile object2) {

                final Date fileDate1 = object1.getTimestamp().getTime();
                final Date fileDate2 = object2.getTimestamp().getTime();
                return fileDate2.compareTo(fileDate1);
            }
        });

        return lteFiles;
    }

    /**
     * Returns a map of the latest LTE mims
     */
    public static Map<String, String> getNodeMap() {
        return nodeMap;
    }

    /**
     * Sets a map of the latest mim nodes
     *
     * @param key
     *            the name of the mim e.g. ERBSA120
     * @param value
     *            the netsim version of the mim e.g R27A
     */
    public void setNodeMap(final String key, final String value) {
        nodeMap.put(key, value);
    }

}
