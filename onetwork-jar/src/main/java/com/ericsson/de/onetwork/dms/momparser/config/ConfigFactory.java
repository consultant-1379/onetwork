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

package com.ericsson.de.onetwork.dms.momparser.config;

import javax.xml.bind.JAXBException;

import com.ericsson.de.onetwork.dms.momparser.MomParserUtil;
import com.ericsson.de.onetwork.dms.momparser.schema.config.MomParserConfig;

/**
 * Generates instances of {@code MomParserConfig} objects for MOM parsers.
 *
 * @author eaefhiq
 */
public class ConfigFactory {

    /**
     * Creates a new {@code MomParserConfig} object defining the configuration
     * for the MOM parser.
     *
     * @param configXml
     *            the path to the XML file defining the MOM parser configuration
     *            file
     * @return an instance of a {@code MomParserConfig} object, which defines
     *         the configuration for a MOM parser
     * @throws JAXBException
     *             Thrown if binding the XML data fails.
     */
    public static MomParserConfig createConfig(final String configXml) throws JAXBException {
        return MomParserUtil.<MomParserConfig>unmarshal(configXml, MomParserConfig.class);
    }
}
