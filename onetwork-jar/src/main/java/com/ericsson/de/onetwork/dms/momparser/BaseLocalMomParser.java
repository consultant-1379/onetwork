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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.de.onetwork.dms.momparser.predicate.ArtifactPredicate;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataType;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Enum;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.InterMim;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Mim;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Models;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Struct;

/**
 * The Class {@code BaseLocalMomParser} provides a skeletal implementation of
 * the MomParser interface to read the input files (MOM XML and mp.dtd)
 * from the local directory and exports extracted artifacts to the local
 * directory in XML format.
 *
 * @author eaefhiq
 */
public class BaseLocalMomParser implements MomParser {

    /** Logger for parsing events . */
    private final static Logger logger = LoggerFactory.getLogger(BaseLocalMomParser.class);

    /** Stores {@code Class} objects from the MOM file */
    protected Map<String, Class> mimClasses = new HashMap<>();

    /**
     * Stores {@code Struct} objects from the MOM file
     */
    protected Collection<Struct> mimStructs;

    /**
     * Stores {@code Enum} objects from the MOM file
     */
    protected Collection<Enum> mimEnums;

    /**
     * Stores {@code DerivedDataType} objects from the MOM file
     */
    protected Collection<DerivedDataType> derivedDataTypes;

    /**
     * Stores {@code Relationship} objects from the MOM file
     */
    protected Map<String, Relationship> mimRelationships = new HashMap<>();

    /**
     * {@code Enum}, {@code Struct} and {@code DerivedDataType} objects are
     * stored in the same collection in {@code Mim} and {@code InterMim} objects
     * in the {@code Models}. All these artifacts are extracted and merged to a
     * single collection. They can fall under the name AttributeSpecification.
     */
    protected Collection<Object> mergedMimAttributeSpecifications = new ArrayList<Object>();

    /** The output directory for the exported artifact XML files. */
    protected String outputDirectory;

    /*
     * @see
     * com.ericsson.de.onenetwork.momparser.MomParser#extract(java.lang.String)
     */
    @Override
    public void exportToXml() {
        try {
            final File rootDir = new File(outputDirectory);
            // Clear the output directory
            if (!rootDir.exists()) {
                rootDir.mkdirs();
            } else {
                rootDir.delete();
                rootDir.mkdirs();
            }
            // Create the sub directories for the artifacts
            final Map<String, String> artifactDir = createSubDirs(rootDir);
            marshalAllElements(artifactDir);
        } catch (final JAXBException | NullPointerException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

    /*
     * @see com.ericsson.de.onenetwork.momparser.MomParser#getClasses()
     */
    @Override
    public Map<String, Class> getMimClasses() {
        return mimClasses;
    }

    /*
     * @see com.ericsson.de.onenetwork.momparser.MomParser#getEnums()
     */
    @Override
    public Collection<Enum> getMimEnums() {
        if (mimEnums == null) {
            mimEnums = MomParserUtil.<Enum>ruleFilter(mergedMimAttributeSpecifications,
                    new ArtifactPredicate<Enum>(Enum.class));
        }

        return mimEnums;
    }

    /*
     * @see com.ericsson.de.onenetwork.momparser.MomParser#getStructs()
     */
    @Override
    public Collection<Struct> getMimStructs() {
        if (mimStructs == null) {
            mimStructs = MomParserUtil.<Struct>ruleFilter(mergedMimAttributeSpecifications,
                    new ArtifactPredicate<Struct>(Struct.class));
        }
        return mimStructs;
    }

    /*
     * @see
     * com.ericsson.de.onenetwork.momparser.MomParser#getMimDerivedDataTypes()
     */
    @Override
    public Collection<DerivedDataType> getMimDerivedDataTypes() {
        if (derivedDataTypes == null) {
            derivedDataTypes = MomParserUtil.<DerivedDataType>ruleFilter(mergedMimAttributeSpecifications,
                    new ArtifactPredicate<DerivedDataType>(DerivedDataType.class));
        }
        return derivedDataTypes;
    }

    /*
     * @see com.ericsson.de.onenetwork.momparser.MomParser#getRelationships()
     */
    @Override
    public Map<String, Relationship> getMimRelationships() {
        return mimRelationships;
    }

    /**
     * Initializes merged artifacts from the MOM file.
     *
     * @param models
     *            the models element extracted from the MOM file
     */
    protected void init(final Models models) {
        mergeMimAndInterMim(models);

    }

    /**
     * Extracts all {@code Mim} and {@code InterMim} objects from the
     * {@code Models} object. Merges all {@code Mim} objects and all
     * {@code InterMim} objects to a single collection.
     * <p>The {@code Mim } object defines all artifacts (ENUM, ClASS,
     * RELATIONSHIP and STRUCT) used from the MOM file.</p>
     * <p>The {@code InterMim} object defines relationships between Managed
     * Elements (ME).</p>
     *
     * @param models
     *            the models element extracted from the MOM file
     */
    protected void mergeMimAndInterMim(final Models models) {
        final Collection<Mim> mims = MomParserUtil.<Mim>ruleFilter(models.getMimOrInterMimOrMib(), new ArtifactPredicate<Mim>(Mim.class));
        final Collection<InterMim> interMims =
                MomParserUtil.<InterMim>ruleFilter(models.getMimOrInterMimOrMib(), new ArtifactPredicate<InterMim>(InterMim.class));
        mergeArtifacts(mims, interMims);
    }

    protected void mergeArtifacts(final Collection<Mim> mims, final Collection<InterMim> interMims) {
        mimClasses = ArtifactMerger.mergeAllClassArtifacts(mims);
        mimRelationships = ArtifactMerger.mergeAllRelationshipArtifacts(mims, interMims);
        mergedMimAttributeSpecifications = ArtifactMerger.mergeAllAttributeSpecificationArtifacts(mims);
    }

    /**
     * Marshals all artifacts (STRUCT, ENUM, CLASS, and RELATIONSHIP) to the
     * specified directory .
     *
     * @param artifactDirectory
     *            the directory where the exported artifacts will be stored
     * @throws JAXBException
     *             Thrown if binding the XML data fails.
     */
    protected void marshalAllElements(final Map<String, String> artifactDirectory) throws JAXBException {

        // Marshal the class artifacts to the class directory.
        for (final Class mimClass : getMimClasses().values()) {
            MomParserUtil.marshal(mimClass, artifactDirectory.get("class") + File.separator + mimClass.getName());
        }
        // Marshal the enum artifacts to the enum directory.
        for (final Enum mimEnum : getMimEnums()) {
            MomParserUtil.marshal(mimEnum, artifactDirectory.get("enum") + File.separator + mimEnum.getName());
        }

        // Marshal the relationship artifacts to the relationship directory
        for (final Relationship relationship : getMimRelationships().values()) {

            MomParserUtil.marshal(relationship,
                    artifactDirectory.get("relationship") + File.separator + relationship.getName());
        }
        // Marshal the struct artifacts to the struct directory
        for (final Struct struct : getMimStructs()) {
            MomParserUtil.marshal(struct, artifactDirectory.get("struct") + File.separator + struct.getName());
        }
    }

    /**
     * Creates the sub directories for the different artifacts.
     * <br/>For example: {class->../class/, struct->../struct/,...}
     *
     * @param rootDir
     *            the root directory for the exported artifacts
     * @return the directory map for the different artifacts.
     */
    protected Map<String, String> createSubDirs(final File rootDir) {
        final Map<String, String> directoryMap = new HashMap<String, String>();
        // create a directory for the class artifacts
        final String classDir = rootDir.getAbsolutePath() + File.separator
                + "class";
        directoryMap.put("class", classDir);
        new File(classDir).mkdirs();

        // create a directory for the struct artifacts
        final String structDir = rootDir.getAbsolutePath() + File.separator
                + "struct";
        directoryMap.put("struct", structDir);
        new File(structDir).mkdirs();

        // create a directory for the enum artifacts
        final String enumDir = rootDir.getAbsolutePath() + File.separator + "enum";
        directoryMap.put("enum", enumDir);
        new File(enumDir).mkdirs();

        // create a directory for the relationship artifacts
        final String relationshipDir = rootDir.getAbsolutePath() + File.separator
                + "relationship";
        directoryMap.put("relationship", relationshipDir);
        new File(relationshipDir).mkdirs();

        return directoryMap;
    }

}
