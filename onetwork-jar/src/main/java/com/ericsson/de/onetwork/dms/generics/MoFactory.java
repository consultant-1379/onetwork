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

package com.ericsson.de.onetwork.dms.generics;

import java.util.List;

import com.ericsson.de.onetwork.dms.MimDataStorage;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidPlatformRuntimeException;
import com.ericsson.de.onetwork.dms.exceptions.MaximumCardinalityExceededException;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.util.CardinalityAssessor;
import com.ericsson.de.onetwork.dms.util.DefaultAttributeValueSetter;
import com.ericsson.de.onetwork.dms.util.MimToPlatformMapper;

/**
 * The {@code MoFactory} is used to produce {@link Mo} instances.
 *
 * @author edalrey
 * @since 1Network_15.12
 */
public class MoFactory {

    /**
     * If this is set to true, then all attributes on the returned {@link Mo}
     * are set, if it exists, to the default value defined in the MOM.
     */
    private final boolean useDefaultAttributes;

    /**
     * Initialises an {@link MoFactory} instance that is used to create an
     * {@link Mo} instance based on the specified MOM xml.
     *
     * @param useDefaults
     *            a boolean value that denotes that {@link GenericDataType}
     *            instances will contain default values as specified in the MOM
     *            xml
     * @throws InvalidModelException
     *             thrown when it is not possible to create a new
     *             {@code CppLocalMomParser} due to the following exceptions:
     *             FileNotFoundException, ParserConfigurationException,
     *             SAXException, or JAXBException.
     */
    public MoFactory(final boolean useDefaults) {
        useDefaultAttributes = useDefaults;
    }

    /**
     * Returns the requested {@link Mo} based on the specified parent
     * {@link Fdn}, managed object type, and managed object name. Then
     * associates the {@code Mo} object to the specified {@link NetworkElement}.
     *
     * @param mimVersion
     *            the version of the underlying node model
     * @param parentMo
     *            the parent {@code Mo}
     * @param moType
     *            the type of managed object
     * @param moName
     *            the name of the {@code Mo}
     * @return the requested {@code Mo} instance
     * @throws InvalidModelRuntimeException
     *             thrown when an unsupported Platform is used
     * @throws InvalidChildException
     *             thrown when the requested {@code Mo} type is not a child of
     *             the parent {@code Mo}
     * @throws MaximumCardinalityExceededException
     *             thrown when the parent {@code Mo} already contains the
     *             maximum number of {@code Mo}'s of the the requested type
     * @throws InvalidModelException
     *             thrown when the model cannot be determined correctly from the
     *             {@code NetworkElement}'s version
     */
    public Mo getMo(final String mimVersion, final Mo parentMo, final String moType, final String moName) throws InvalidPlatformRuntimeException,
            InvalidChildException, MaximumCardinalityExceededException {
        Mo managedObject = null;

        if (isValidMoToCreate(moType, parentMo)) {
            if (isUnderMaximumCardinatlity(parentMo, moType)) {
                managedObject = createMo(mimVersion, parentMo, moType, moName);
            } else {
                throw new MaximumCardinalityExceededException(
                        String.format("No more %s can be added to %s=%s", moType, parentMo.getFdn().getType(), parentMo.getFdn().getName()));
            }
        } else {
            throw new InvalidChildException(String.format("%s is not a child of %s", moType, parentMo.getFdn().getType()));
        }

        if (useDefaultAttributes) {
            DefaultAttributeValueSetter.setAllDefaultAttributes(mimVersion, managedObject);
        }
        return managedObject;
    }

    private boolean isValidMoToCreate(final String moType, final Mo parentMo) {
        // The null case if for when there is no parent defined for the Mo being
        // created, e.g., the root level Mo.
        return null == parentMo || parentMo.isValidChildMoType(moType);
    }

    private boolean isUnderMaximumCardinatlity(final Mo parentMo, final String moType) throws InvalidChildException {
        // The null case if for when there is no parent defined for the Mo being
        // created, e.g., the root level Mo.
        return null == parentMo || !CardinalityAssessor.isMaxCardinalityExceeded(moType, parentMo);
    }

    private Mo createMo(final String mimVersion, final Mo parentMo, final String type, final String name) throws InvalidPlatformRuntimeException {
        final Platform platform = MimToPlatformMapper.getPlatformByMimVersion(mimVersion);
        Mo newManagedObject;
        final Class moClass = MimDataStorage.getClassByMoType(mimVersion, type);
        switch (platform) {
            case CPP:
                newManagedObject = new CppMo(moClass, parentMo, type, name);
                break;
            case COMECIM:
                newManagedObject = new ComEcimMo(moClass, parentMo, type, name);
                break;
            default:
                throw new InvalidPlatformRuntimeException(String.format("%s is not a valid model.", mimVersion));
        }

        final List<String> attributeNames = MimDataStorage.getAttributes(mimVersion, type);
        newManagedObject.setAttributeNames(attributeNames);
        final List<String> childTypes = MimDataStorage.getChildMoTypes(mimVersion, type);
        newManagedObject.setChildTypes(childTypes);

        return newManagedObject;
    }

}
