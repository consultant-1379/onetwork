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

package com.ericsson.de.onetwork.dms.momparser;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.ericsson.de.onetwork.dms.momparser.schema.config.MomParserConfig;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Models;

/**
 * Parses CPP data from either a combination of a MOM XML file and mp.dtd file
 * or using the {@code MomParserConfig} object.
 *
 * @author eaefhiq
 */
public class CppLocalMomParser extends BaseLocalMomParser {

    private final static Logger logger = LoggerFactory.getLogger(CppLocalMomParser.class);

    /**
     * Instantiates a new CPP MOM parser using a MOM file and an mp.dtd file as
     * inputs.
     *
     * @param momFile
     *            the MOM file path
     * @param mpDtd
     *            the mp.dtd file path
     * @param outputDir
     *            the output directory for the exported XML files
     * @throws FileNotFoundException
     *             Thrown if the MOM file or the mp.dtd file does not exist.
     * @throws ParserConfigurationException
     *             Thrown if the MOM file or the mp.dtd file have corrupt data
     *             in them.
     * @throws SAXException
     *             Thrown if parsing fails.
     * @throws JAXBException
     *             Thrown if binding the XML data fails.
     */
    public CppLocalMomParser(final String momFile, final String mpDtd, final String outputDir) throws FileNotFoundException,
            ParserConfigurationException, SAXException, JAXBException {
        logger.debug("Cpp parsing using ( momFile = {}, dtdFile = {}, outputDirectory = {} )", momFile, mpDtd, outputDir);

        super.outputDirectory = outputDir;
        final Models models = MomParserUtil.unmarshal(momFile, mpDtd, Models.class);
        init(models);
    }

    /**
     * Instantiates a new CPP local XML mom parser using a
     * {@code MomParserConfig} object as an input.
     *
     * @param momConfig
     *            the MOM configuration object.
     * @throws FileNotFoundException
     *             Thrown if the MOM file or the mp.dtd file in the
     *             {@code MomParserConfig} object does not exist.
     * @throws ParserConfigurationException
     *             Thrown if the MOM file or the mp.dtd file in the
     *             {@code MomParserConfig} object have corrupt data in them.
     * @throws SAXException
     *             Thrown if parsing fails.
     * @throws JAXBException
     *             Thrown if binding the XML data fails.
     */
    public CppLocalMomParser(final MomParserConfig momConfig) throws FileNotFoundException, ParserConfigurationException, SAXException, JAXBException {
        this(momConfig.getInput().getMomURI(), momConfig.getInput().getMpDtdURI(), momConfig.getOutput().getMomPool());
        super.outputDirectory =
                momConfig.getOutput().getMomPool()
                        + File.separator + momConfig.getPlatformType()
                        + File.separator + momConfig.getNodeType()
                        + File.separator + momConfig.getOutput().getVersion()
                        + File.separator;
    }

}
