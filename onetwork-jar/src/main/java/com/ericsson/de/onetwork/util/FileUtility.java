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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.bs.rules.general.BasicMoCreation;

/**
 * Used to read feature data from csv files in /src/main/resources
 * TODO: To be replaced by DB in future sprint.
 *
 * @author ecasjim
 */
public class FileUtility {
    private final static Logger logger = LoggerFactory.getLogger(BasicMoCreation.class);

    /**
     * Used to read specific csv file.
     *
     * @param fileName
     *            file to read
     * @return list of strings representing each element in file
     */
    public static List<String> readFromCSV(final String fileName) {
        final String cvsSplitBy = ",";

        final InputStream in = FileUtility.class.getResourceAsStream(fileName);
        final BufferedInputStream bf = new BufferedInputStream(in);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(bf, StandardCharsets.UTF_8));

        String attributesSingleString = null;
        List<String> attributes = new ArrayList<String>();

        try {
            attributesSingleString = reader.readLine();
            attributes = new ArrayList<String>(Arrays.asList(attributesSingleString.split(cvsSplitBy)));
            logger.debug("Successfully read values from {}", fileName);

        } catch (final IOException e) {
            logger.error(e.getMessage());
        }

        return attributes;
    }
}
