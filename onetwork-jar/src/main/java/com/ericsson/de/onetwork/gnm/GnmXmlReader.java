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

package com.ericsson.de.onetwork.gnm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ericsson.de.onetwork.NetworkType;

/**
 * The implementation of the <code>GnmReader</code> interface for reading
 * <code>Gnm</code> XML files.
 *
 * @author ecasjim
 */
public class GnmXmlReader implements GnmReader {

    private final static Logger logger = LoggerFactory.getLogger(GnmXmlReader.class.getName());

    /*
     * @see com.ericsson.de.onetwork.gnm.GnmReader#getGnm(java.lang.String)
     */
    @Override
    public Gnm getGnm(final String gnmRevision) throws GnmRequestException {
        final Document doc = createDocumentFromFile(gnmRevision);

        final String networkType = doc.getDocumentElement().getAttribute("type");

        if (!NetworkType.isMember(networkType)) {
            throw new GnmRequestException(networkType + " is not a valid network type.");
        }

        final List<Integer> cellPatternList = extractCellPatternData(doc);
        final Map<String, Double> mimUsage = extractMimData(doc);

        return new Gnm(gnmRevision, NetworkType.valueOf(networkType), mimUsage, cellPatternList);
    }

    public String getGNMData(final String pathToAttribute, final String baseLTEGNMRevision) throws GnmRequestException {
        final Document doc = createDocumentFromFile(baseLTEGNMRevision);
        final List<String> pathToAttributeSplit = Arrays.asList(pathToAttribute.split(","));
        final int requiredValueIndex = pathToAttributeSplit.size() - 1;
        final NodeList nList = doc.getElementsByTagName(pathToAttributeSplit.get(0));
        Element element = null;

        for (int i = 1; i < pathToAttributeSplit.size(); i++) {
            element = (Element) nList.item(0);
        }

        return getTextValue(element, pathToAttributeSplit.get(requiredValueIndex));
    }

    private List<Integer> extractCellPatternData(final Document doc) {
        final List<Integer> cellPattern = new ArrayList<Integer>();
        final NodeList nList = doc.getElementsByTagName("cellpattern");

        for (int i = 0; i < nList.getLength(); i++) {
            final Element element = (Element) nList.item(i);
            final String cellNumber = getTextValue(element, "cellnumber");
            cellPattern.addAll(splitCellPatternStringIntoList(cellNumber));
        }

        return cellPattern;
    }

    private Map<String, Double> extractMimData(final Document doc) {
        final Map<String, Double> mimUsage = new HashMap<String, Double>();
        final NodeList nList = doc.getElementsByTagName("mimentry");

        for (int i = 0; i < nList.getLength(); i++) {
            final Element element = (Element) nList.item(i);
            final String mimname = getTextValue(element, "mimname");
            final Double mimpercentage = getDoubleValue(element, "mimpercentage");
            mimUsage.put(mimname, mimpercentage);
        }
        return mimUsage;
    }

    private List<Integer> splitCellPatternStringIntoList(final String cellPattern) {
        final List<Integer> cellPatternAsInteger = new ArrayList<Integer>();

        for (final String cellPatternElement : cellPattern.split(",")) {
            cellPatternAsInteger.add(Integer.parseInt(cellPatternElement));
        }

        return cellPatternAsInteger;
    }

    /**
     * Takes a XML element and the tag name.
     * The tag is located and the text content is retrieved.
     * <p>
     * Example: For <employee><name>John</name></employee> XML snippet,
     * if the Element points to employee node and tagName is 'name',
     * "John" will be returned.
     * </p>
     */
    private String getTextValue(final Element ele, final String tagName) {
        String textVal = "";
        final NodeList nodeList = ele.getElementsByTagName(tagName);

        if (nodeList != null && nodeList.getLength() > 0) {
            final Element element = (Element) nodeList.item(0);
            textVal = element.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    /**
     * Returns the result of getTextValue as a Double.
     */
    private Double getDoubleValue(final Element element, final String tagName) {
        return Double.parseDouble(getTextValue(element, tagName));
    }

    private Document createDocumentFromFile(final String gnmRevision) throws GnmRequestException {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc;

        logger.debug("Attempting to read GNM from {}", gnmRevision);
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("bs/nrms/" + gnmRevision + ".xml"));

        } catch (final ParserConfigurationException | SAXException | IllegalArgumentException | IOException e) {
            logger.error("Cannot read GNM from file: {}", e.getClass().toString());
            throw new GnmRequestException(e);
        }

        return doc;
    }
}
