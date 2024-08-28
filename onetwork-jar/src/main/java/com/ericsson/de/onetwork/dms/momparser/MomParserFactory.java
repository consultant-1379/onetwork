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

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ericsson.de.onetwork.dms.generics.Platform;
import com.ericsson.de.onetwork.dms.momparser.schema.config.MomParserConfig;

/**
 * A factory for creating instances of {@code MomParser} objects.
 *
 * @author eaefhiq
 */
public class MomParserFactory {

    /**
     * Creates a new {@code MomParser} object.
     *
     * @param nodePlatform
     *            the node type. For example, "cpp" for a CPP node.
     * @return an instance of a {@code MomParser} object or null if the node
     *         type is not supported.
     * @throws FileNotFoundException
     *             Thrown if the MOM file or the mp.dtd file in the
     *             {@code MomParserConfig} object
     *             does not exist.
     * @throws ParserConfigurationException
     *             Thrown if the MOM file or the mp.dtd file in the
     *             {@code MomParserConfig} object
     *             have corrupt data in them.
     * @throws SAXException
     *             Thrown if parsing fails.
     * @throws JAXBException
     *             Thrown if binding the XML data fails.
     */
    public static MomParser createMomParser(final Platform nodePlatform, final MomParserConfig config) throws FileNotFoundException,
            ParserConfigurationException, SAXException, JAXBException {
        switch (nodePlatform) {
            case CPP:
                return new CppLocalMomParser(config);
            case COMECIM:
                return new EcimLocalMomParser(config);
            default:
                break;
        }
        return null;
    }
}
