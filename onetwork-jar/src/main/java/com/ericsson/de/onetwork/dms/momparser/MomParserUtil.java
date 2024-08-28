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
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.collections.Predicate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.ericsson.de.onetwork.dms.momparser.trim.Trimmer;
import com.ericsson.de.onetwork.dms.momparser.trim.TrimmerFactory;

/**
 * Provides a set of utilities for binding between XML files and objects and for
 * manipulation of collections.
 *
 * @author eaefhiq
 */
public class MomParserUtil {

    /**
     * Creates an {@code XMLReader} object, which is used for reading XML files.
     *
     * @param validating
     *            set to true if using validation, false otherwise
     * @return an {@code XMLReader} object, which reads XML files
     * @throws ParserConfigurationException
     *             Thrown if the XML schema does not exist.
     * @throws SAXException
     *             Thrown if parsing fails.
     */
    private static XMLReader getXmlReader(final boolean validating) throws ParserConfigurationException, SAXException {
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        if (validating) {
            parserFactory.setNamespaceAware(true);
            parserFactory.setValidating(true);
        }
        return parserFactory.newSAXParser().getXMLReader();
    }

    /**
     * Binds an XML file to an object using an XML file, an XML schema
     * definition file and the object to bind the XML file to as inputs. For
     * example, binds Cdma2000CellRelation.xml to an instance of the
     * {@code Class} object.
     *
     * @param <T>
     *            the generic type
     * @param xmlFile
     *            the XML file that binds to the {@code classToBeBound} object.
     *            For example, Cdma2000CellRelation.xml
     * @param dtdFile
     *            the mp.dtd file
     * @param classToBeBound
     *            the class to be bound to the XML file
     * @return an instance of the {@code classToBeBound} class, bound with an
     *         XML file. This class has the same type as the original input
     *         {@code classToBeBound}
     * @throws FileNotFoundException
     *             Thrown if either of {@code xmlFile} or {@code dtdFile} 
     *             doesn't exist.
     * @throws ParserConfigurationException
     *             Thrown if {@code xmlFile} or {@code dtdFile} have corrupt
     *             data in them.
     * @throws SAXException
     *             Thrown if parsing fails.
     * @throws JAXBException
     *             Thrown if binding the XML data fails.
     */
    public static <T> T unmarshal(final String xmlFile, final String dtdFile, final Class<T> classToBeBound) throws FileNotFoundException,
            ParserConfigurationException, SAXException, JAXBException {
        final InputSource inSrc = new InputSource(new java.io.FileInputStream(xmlFile));
        inSrc.setSystemId(dtdFile);
        final SAXSource source = new SAXSource(MomParserUtil.getXmlReader(false), inSrc);
        final Unmarshaller unmarshaller = JAXBContext.newInstance(classToBeBound).createUnmarshaller();
        return (T) unmarshaller.unmarshal(source);
    }

    /**
     * Binds an XML file to an object using an XML file and the object to bind
     * the XML file to as inputs. For example, binds Cdma2000CellRelation.xml to
     * an instance of the {@code Class} object.
     *
     * @param <T>
     *            the generic type
     * @param xmlFile
     *            the XML file that binds to the {@code classToBeBound} object
     * @param dtdFile
     *            the mp.dtd file
     * @param classToBeBound
     *            the class to be bound to the XML file
     * @return an instance of the {@code classToBeBound} class, bound with an
     *         XML file. This class has the same type as the original input
     *         {@code classToBeBound}
     * @throws FileNotFoundException
     *             Thrown if either of {@code xmlFile} or {@code dtdFile} 
     *             doesn't exist.
     * @throws ParserConfigurationException
     *             Thrown if {@code xmlFile} or {@code dtdFile} have corrupt
     *             data in them.
     * @throws SAXException
     *             Thrown if parsing fails.
     * @throws JAXBException
     *             Thrown if binding the XML data fails.
     */
    public static <T> T unmarshal(final String xmlFile, final Class<T> classToBeBound) throws JAXBException {
        final File file = new File(xmlFile);
        final JAXBContext jaxbContext = JAXBContext.newInstance(classToBeBound);
        final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (T) jaxbUnmarshaller.unmarshal(file);
    }

    /**
     * Binds an object to an XML file. For example, binds an instance of the
     * {@code Class} object to Cdma2000CellRelation.xml
     *
     * @param marshaledObject
     *            the object to be marshaled to an XML file.
     * @param xmlFileURI
     *            the XML file URL
     * @throws JAXBException
     *             Thrown if binding the XML data fails.
     */
    public static void marshal(final Object marshaledObject, final String xmlFileURI) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(marshaledObject.getClass());
        final Marshaller marsheller = context.createMarshaller();
        marsheller.marshal(marshaledObject, new File(xmlFileURI + ".xml"));

    }

    /**
     * Filters out the collection rules defined by a given predicate. For
     * example, remove any description tags and the information contained within
     * them.
     *
     * @param <T>
     *            the generic type object
     * @param collection
     *            a collection of objects
     * @param predicate
     *            determines if an object will be trimmed and added to the
     *            collection to be returned
     * @return a collection of results filtered according to the rules in the
     *         {@code predicate} object
     */
    public static <T> Collection<T> ruleFilter(final Collection collection, final Predicate predicate) {
        final Collection<T> filteredResults = new ArrayList<T>();
        for (final Object unfilteredObject : collection) {
            if (predicate.evaluate(unfilteredObject)) {
                // Trim unused tags from the class;
                final Trimmer trimmer = TrimmerFactory.getTrimmer(unfilteredObject);
                if (trimmer != null) {
                    trimmer.trim();
                }
                filteredResults.add((T) unfilteredObject);
            }
        }
        return filteredResults;
    }

}
