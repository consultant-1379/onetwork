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

package com.ericsson.de.onetwork.dms;

import static com.ericsson.de.onetwork.dms.constants.RelationshipConstants.INVALID_ROOT_MO_TYPE;
import static com.ericsson.de.onetwork.dms.constants.RelationshipConstants.NO_PARENT_FOUND_INDICATOR;
import static com.ericsson.de.onetwork.dms.constants.RelationshipConstants.RELATIONSHIP_CHILD_TYPE_INDEX;
import static com.ericsson.de.onetwork.dms.constants.RelationshipConstants.RELATIONSHIP_PARENT_TYPE_INDEX;
import static com.ericsson.de.onetwork.dms.constants.RelationshipConstants.RELATIONSHIP_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.ericsson.de.onetwork.MimXmlFileTestConstants;
import com.ericsson.de.onetwork.dms.exceptions.DataModellerServiceException;
import com.ericsson.de.onetwork.dms.generics.Mo;
import com.ericsson.de.onetwork.dms.generics.Platform;
import com.ericsson.de.onetwork.dms.mo.datatype.MoAttributeDataType;
import com.ericsson.de.onetwork.dms.momparser.MomParser;
import com.ericsson.de.onetwork.dms.momparser.MomParserFactory;
import com.ericsson.de.onetwork.dms.momparser.schema.config.MomParserConfig;
import com.ericsson.de.onetwork.dms.momparser.schema.config.MomParserConfig.Input;
import com.ericsson.de.onetwork.dms.momparser.schema.config.MomParserConfig.Output;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Attribute;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;
import com.ericsson.de.onetwork.dms.util.MimFileUtility;
import com.ericsson.de.onetwork.dms.util.MimToPlatformMapper;
import com.ericsson.de.onetwork.dms.util.MoFormatter;
import com.ericsson.de.onetwork.ss.util.InvalidMimVersionFormatException;

/**
 * Storage solution for MOM.XML data within the scope of vertical slice,
 * expected to end by 1Network_15.15. One CPP (ERBS G.1.60) and one COM-ECIM
 * (SGSN 15B WPP-V5) are stored locally.
 *
 * @author edalrey
 * @since 1Network_15.14
 * @author eaefhiq
 */
public class MimDataStorage {

    private final static Logger logger = LoggerFactory.getLogger(MimDataStorage.class);

    private static String RESOURCES_DIRECTORY = MimDataStorage.class.getResource("/").getPath();

    private static String DTD_FILE_LOCATION = RESOURCES_DIRECTORY + "schema" + File.separator + "mp.dtd";

    private static String MIM_DIRECTORY = RESOURCES_DIRECTORY + "dms" + File.separator + "mims" + File.separator;

    private static MomParser PARSER;

    private static String CURRENT_MIM_VERSION = "";

    private static Map<String, Class> CLASSES;
    private static Map<String, Relationship> RELATONSHIPS;

    // MIM example = "LTE ERBS F1101"
    // This is called from rest call from GUI, before 1network service starts
    public static void loadMimVersionToMemory(final String mimVersion) throws DataModellerServiceException {

        try {
            if (!mimVersion.equals(CURRENT_MIM_VERSION)) {
                // Map MIM to config path
                final MomParserConfig mpConfig = new MomParserConfig();
                final Input input = new Input();
                final Output output = new Output();

                // TODO: an adapter method can be called to convert mimVersion
                // to the mim file name.
                if (MimToPlatformMapper.getPlatformByMimVersion(mimVersion) == Platform.CPP) {
                    input.setMomURI(MIM_DIRECTORY + MimFileUtility.getMimFileName(mimVersion));
                }
                else if (MimToPlatformMapper.getPlatformByMimVersion(mimVersion) == Platform.COMECIM) {
                    input.setMomURI(MIM_DIRECTORY + MimXmlFileTestConstants.getComEcimFile());
                }

                input.setMpDtdURI(DTD_FILE_LOCATION);
                output.setMomPool("");
                output.setVersion("");
                mpConfig.setInput(input);
                mpConfig.setOutput(output);
                logger.debug("Platform: " + MimToPlatformMapper.getPlatformByMimVersion(mimVersion).toString());

                logger.debug("MOM file URI: " + input.getMomURI());
                PARSER = MomParserFactory.createMomParser(MimToPlatformMapper.getPlatformByMimVersion(mimVersion), mpConfig);
                CLASSES = populateCppClasses();
                RELATONSHIPS = populateCppRelationships();
                CURRENT_MIM_VERSION = mimVersion;
            } else if (mimVersion.equals("")) {
                throw new DataModellerServiceException("MIM version is blank.");
            }
        } catch (IOException | ParserConfigurationException | SAXException | JAXBException | InvalidMimVersionFormatException e) {
            // TODO: Possibly create custom exception
            logger.debug("Exception occured at loadMimVersionToMemory. See msg:{}", e.getMessage());
            throw new DataModellerServiceException(e.getStackTrace().toString());
        }
    }

    private static Map<String, Class> populateCppClasses() {
        return PARSER.getMimClasses();
    }

    private static Map<String, Relationship> populateCppRelationships() {
        return PARSER.getMimRelationships();
    }

    /**
     * Return an mp.dtd {@link Relationship} based on an input {@link Mo} type.
     *
     * @param mimVersion
     *            version of the underlying node model
     * @param parentType
     *            the type of parent managed object
     * @param childType
     *            the type of child managed object
     * @return mp.dtd {@code Relationship} instance of a given type
     */
    public static Relationship getRelatonshipByParentAndChildMoTypes(final String parentType, final String childType) {
        final String relationshipName = parentType + RELATIONSHIP_SEPARATOR + childType;
        return getRelatonships(relationshipName);

    }

    /**
     * Return an mp.dtd {@link Class} based on an input {@link Mo} type.
     *
     * @param mimVersion
     *            version of the underlying node model
     * @param moType
     *            the type of managed object
     * @return mp.dtd {@code Class} instance of a given type
     */
    public static Class getClassByMoType(final String mimVersion, final String moType) {
        return getClass(moType);
    }

    private static Class getClass(final String moType) {
        final Class moClass = CLASSES.get(moType);
        if (null != moClass) {
            return moClass;
        } else {
            throw new IllegalStateException("Could not find Mo instance of input type: " + moType);
        }
    }

    private static Relationship getRelatonships(final String moType) {
        final Relationship moRelationship = RELATONSHIPS.get(moType);
        if (null != moRelationship) {
            return moRelationship;
        } else {
            throw new IllegalStateException("Could not find parent of input child type: " + moType);
        }
    }

    /**
     * Returns the {@link Mo} instance that is at the root managed object of the
     * given MOM XML tree structure.
     *
     * @param mimVersion
     *            version of the underlying node model
     * @return the {@code Mo} object that is at the root of the given MOM XML
     */
    public static Class getRootMoClass(final String mimVersion) {
        final Collection<Class> classesPerMim = getClassesByMimVersion(mimVersion);
        logger.debug("There are {} Classes in MIM {}", classesPerMim.size(), mimVersion);
        for (final Class moClass : classesPerMim) {
            final String moType = MoFormatter.removeComEcimNamespaceFrom(moClass.getName());
            final String parent = getParentMoType(mimVersion, moType);
            final String parentMoType = MoFormatter.removeComEcimNamespaceFrom(parent);
            if (NO_PARENT_FOUND_INDICATOR.equals(parentMoType) && !INVALID_ROOT_MO_TYPE.equals(moClass.getName())) {
                return moClass;
            }
        }
        logger.debug("Mim version {} contains no classes", mimVersion);
        return null;
    }

    private static Collection<Class> getClassesByMimVersion(final String mimVersion) {
        final Platform platform = MimToPlatformMapper.getPlatformByMimVersion(mimVersion);
        logger.debug("MIM {} is based on Platform {}", mimVersion, platform);
        return CLASSES.values();
    }

    private static String getParentMoType(final String mimVersion, final String moType) {
        for (final Relationship relationshipItem : getRelationshipsByMimVersion(mimVersion)) {
            final String[] relationshipComponents = relationshipItem.getName().split(RELATIONSHIP_SEPARATOR);
            if (MoFormatter.removeComEcimNamespaceFrom(relationshipComponents[RELATIONSHIP_CHILD_TYPE_INDEX]).equals(moType)) {
                return relationshipComponents[RELATIONSHIP_PARENT_TYPE_INDEX];
            }
        }
        return NO_PARENT_FOUND_INDICATOR;
    }

    private static Collection<Relationship> getRelationshipsByMimVersion(final String mimVersion) {
        return RELATONSHIPS.values();
    }

    /**
     * Returns the boolean variable that indicates if the specified {@code Mo}
     * object is system created or not.
     *
     * @param moType
     *            the type of managed object
     * @param childType
     * @return a boolean value based on whether the given {@code Mo} is system
     *         created or not
     */
    public static boolean isMoSystemCreated(final String mimVersion, final String moType) {
        final Class moClass = getClassByMoType(mimVersion, moType);
        if (moClass.getSystemCreated() != null) {
            return true;
        }
        return false;
    }

    /**
     * Return a list of {@link Mo} names that valid are valid children.
     *
     * @param mimVersion
     *            version of the underlying node model
     * @param moType
     *            the type of managed object
     * @return a list of {@code Mo} names that valid are valid children
     */
    public static List<String> getChildMoTypes(final String mimVersion, final String moType) {
        final List<String> children = new ArrayList<String>();
        for (final Relationship relationshipItem : getRelationshipsByMimVersion(mimVersion)) {
            final String[] relationshipComponents = relationshipItem.getName().split(RELATIONSHIP_SEPARATOR);
            final String parentName = MoFormatter.removeComEcimNamespaceFrom(relationshipComponents[RELATIONSHIP_PARENT_TYPE_INDEX]);
            if (parentName.equals(moType)) {
                final String childName = MoFormatter.removeComEcimNamespaceFrom(relationshipComponents[RELATIONSHIP_CHILD_TYPE_INDEX]);
                children.add(childName);
            }
        }
        return children;
    }

    /**
     * Return a list of {@link MoAttributeDataType} names that valid are valid
     * attributes.
     *
     * @param mimVersion
     *            version of the underlying node model
     * @param moType
     *            the type of managed object
     * @return a list of {@code MoAttributeDataType} names that valid are valid
     *         attributes
     */
    public static List<String> getAttributes(final String mimVersion, final String moType) {
        final List<String> attributeList = new ArrayList<String>();
        final Class moClass = getClassByMoType(mimVersion, moType);
        final List<Object> possibleAttributes = moClass.getActionOrAttribute();

        if (possibleAttributes != null) {
            for (final Object possibleAttribute : possibleAttributes) {
                if (possibleAttribute instanceof Attribute) {
                    final String attributeName = ((Attribute) possibleAttribute).getName();
                    attributeList.add(attributeName);
                }
            }
        }
        return attributeList;
    }

}
