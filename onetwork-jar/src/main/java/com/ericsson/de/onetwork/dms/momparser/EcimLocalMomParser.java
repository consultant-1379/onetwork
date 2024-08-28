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
import java.util.Collection;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.ericsson.de.onetwork.dms.momparser.predicate.ArtifactPredicate;
import com.ericsson.de.onetwork.dms.momparser.schema.config.MomParserConfig;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataType;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Enum;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.InterMim;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Mim;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Models;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Struct;

/**
 * Parses ECIM data from either a combination of a MOM XML file and mp.dtd file
 * or the {@code MomParserConfig} object.
 *
 * @author eaefhiq
 */
public class EcimLocalMomParser extends BaseLocalMomParser {

    /** Logger for parsing events. */
    private final static Logger logger = LoggerFactory.getLogger(EcimLocalMomParser.class);

    /**
     * Instantiates a new ECIM MOM parser using a MOM file and an mp.dtd file as
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
    public EcimLocalMomParser(final String momFragmentFolder, final String mpDtd, final String outputDir) throws FileNotFoundException,
            ParserConfigurationException, SAXException, JAXBException {
        logger.debug("ComEcim parsing using ( momFragmentsFolder = {}, dtdFile = {}, outputDirectory = {} )", momFragmentFolder, mpDtd, outputDir);

        final File fragFolder = new File(momFragmentFolder);
        logger.debug("ComEcim fragments located in: {}", fragFolder.toString());
        super.outputDirectory = outputDir;

        // store all fragment MOM files to an array
        final File[] momFiles = fragFolder.listFiles();
        logger.debug("ComEcim fragments used are: ", momFiles.toString());

        // bind the first MOM XML fragment to the {@link Models} object
        final String firstMomFragmentPath = momFiles[0].getAbsolutePath();
        logger.debug("Binding {} XML fragment to model", firstMomFragmentPath);
        final Models models = MomParserUtil.<Models>unmarshal(firstMomFragmentPath, mpDtd, Models.class);

        /*
         * Extracts the Mim and InterMim objects from the remaining models in
         * the XML file and adds them to the first model.
         */
        for (int i = 1; i < momFiles.length; i++) {
            models.getMimOrInterMimOrMib().addAll(
                    MomParserUtil.<Models>unmarshal(momFiles[i].getAbsolutePath(), mpDtd, Models.class).getMimOrInterMimOrMib());
        }
        init(models);
    }

    /**
     * Instantiates a new ECIM local XML mom parser using a
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
    public EcimLocalMomParser(final MomParserConfig config) throws FileNotFoundException, ParserConfigurationException, SAXException, JAXBException {
        this(config.getInput().getMomURI(), config.getInput().getMpDtdURI(), config.getOutput().getMomPool());
        super.outputDirectory = config.getOutput().getMomPool() + File.separator
                + config.getPlatformType() + File.separator
                + config.getNodeType() + File.separator
                + config.getOutput().getVersion() + File.separator;
    }

    /*
     * @see com.ericsson.de.onetwork.dms.momparser.AbstractLocalXmlMomParser#
     * mergeMimAndInterMim(com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.
     * Models)
     */
    @Override
    protected void mergeMimAndInterMim(final Models models) {
        /*
         * merge all {@code Mim} objects to a
         * single collection
         */
        final Collection<Mim> mims = MomParserUtil.<Mim>ruleFilter(models.getMimOrInterMimOrMib(), new ArtifactPredicate<Mim>(Mim.class));
        assignNamespaceToMos(mims);
        /*
         * merge all {@code InterMim} objects
         * to a single collection
         */
        final Collection<InterMim> interMims =
                MomParserUtil.<InterMim>ruleFilter(models.getMimOrInterMimOrMib(), new ArtifactPredicate<InterMim>(InterMim.class));
        mergeArtifacts(mims, interMims);
    }

    /**
     * Assigns namespaces to all MOs. For example, Mme:MmeFunction,
     * Sgsn_Mme:AclAcc
     *
     * @param mims
     *            the collection of MIM elements
     */
    private void assignNamespaceToMos(final Collection<Mim> mims) {
        // go through the {@code mims} assign the {@code namespace} to the
        // artifacts.
        for (final Mim mim : mims) {
            final String namespace = mim.getName();
            logger.debug(mim.getName());
            assignNamespaceToClasses(namespace, mim.getClazz());
            assignNamespaceToEnum(namespace,
                    MomParserUtil.<Enum>ruleFilter(mim.getStructOrEnumOrExceptionOrDerivedDataType(), new ArtifactPredicate<Enum>(Enum.class)));
            assignNamespaceToStruct(namespace,
                    MomParserUtil.<Struct>ruleFilter(mim.getStructOrEnumOrExceptionOrDerivedDataType(), new ArtifactPredicate<Struct>(Struct.class)));
            assignNamespaceToDerivedDataType(namespace,
                    MomParserUtil.<DerivedDataType>ruleFilter(mim.getStructOrEnumOrExceptionOrDerivedDataType(),
                            new ArtifactPredicate<DerivedDataType>(DerivedDataType.class)));
        }
    }

    /**
     * Assigns a namespace to the class artifacts.
     *
     * @param namespace
     *            the namespace of the MO
     * @param mimClasses
     *            the collection of {@code Class} objects from the model
     */
    private void assignNamespaceToClasses(final String namespace, final Collection<Class> mimClasses) {
        for (final Class mimClass : mimClasses) {
            mimClass.setName(namespace + ":" + mimClass.getName());
        }
    }

    /**
     * Assigns a namespace to the struct artifacts.
     *
     * @param namespace
     *            the namespace of the MO
     * @param structs
     *            the collection of {@code Struct}
     */
    private void assignNamespaceToStruct(final String namespace, final Collection<Struct> structs) {
        for (final Struct struct : structs) {
            struct.setName(namespace + ":" + struct.getName());
        }
    }

    /**
     * Assigns a namespace to the enum artifacts.
     *
     * @param namespace
     *            the namespace of the MO
     * @param mimEnums
     *            the collection of {@code Enum}
     */
    private void assignNamespaceToEnum(final String namespace, final Collection<Enum> mimEnums) {
        for (final Enum mimEnum : mimEnums) {
            mimEnum.setName(namespace + ":" + mimEnum.getName());
        }
    }

    /**
     * Assigns a namespace to the derivedDataType artifacts.
     *
     * @param namespace
     *            the namespace of the MO
     * @param mimDerivedDataTypes
     *            the collection of {@code DerivedDataType}
     */
    private void assignNamespaceToDerivedDataType(final String namespace, final Collection<DerivedDataType> mimDerivedDataTypes) {
        for (final DerivedDataType mimDerivedDataType : mimDerivedDataTypes) {
            mimDerivedDataType.setName(namespace + ":" + mimDerivedDataType.getName());
        }
    }
}
