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

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ericsson.de.onetwork.MimXmlFileTestConstants;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidPlatformRuntimeException;
import com.ericsson.de.onetwork.dms.exceptions.MaximumCardinalityExceededException;
import com.ericsson.de.onetwork.dms.momparser.MomParser;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Attribute;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.SystemCreated;
import com.ericsson.de.onetwork.dms.util.CardinalityAssessor;

/**
 * Unit tests for {@link MoFactory};
 *
 * @author edalrey
 * @since 1Network_15.14
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CardinalityAssessor.class)
public class MoFactoryTest {

    private final static String CPP_MIM_VERSION = "erbs_nodes/" + MimXmlFileTestConstants.CPP_MIM;
    private final static String COM_ECIM_MIM_VERSION = "sgsn_nodes/";
    private final static String MO_NAME = "1";
    private final static String MO_TYPE = "ENodeBFunction";

    @Mock
    private Class classMock;

    @Mock
    private MomParser momParserMock;

    @Mock
    private Mo parentMoMock;

    @Mock
    private Fdn fdnMock;

    @Mock
    private Relationship relationshipMock;

    @Mock
    private Attribute attributeMock;

    private Map<String, Class> classes;

    @Before
    public void setUp() {
        classes = new HashMap<>();
        classes.put(MO_TYPE, classMock);
    }

    @Test(expected = InvalidPlatformRuntimeException.class)
    public void whenCreatingMoWithDefaults_withInvalidPlatform_thenInvalidPlatformExceptionIsThrown() throws InvalidPlatformRuntimeException,
            InvalidChildException, MaximumCardinalityExceededException {
        when(classMock.getSystemCreated()).thenReturn(new SystemCreated());
        when(momParserMock.getMimClasses()).thenReturn(classes);
        when(momParserMock.getMimRelationships()).thenReturn(Collections.<String, Relationship>emptyMap());

        final boolean genericDataTypesWithDefaultValues = true;
        final MoFactory moFactory = new MoFactory(genericDataTypesWithDefaultValues);

        moFactory.getMo(COM_ECIM_MIM_VERSION, null, MO_TYPE, MO_NAME);
    }

    @Test(expected = InvalidChildException.class)
    public void whenCreatingMoWithDefaultsWithParent_whereChildMoTypeDoesNotExist_thenInavlidChildExceptionIsThrown()
            throws InvalidPlatformRuntimeException, InvalidChildException, MaximumCardinalityExceededException {
        when(classMock.getSystemCreated()).thenReturn(new SystemCreated());
        when(momParserMock.getMimClasses()).thenReturn(classes);
        when(momParserMock.getMimRelationships()).thenReturn(Collections.<String, Relationship>emptyMap());
        when(parentMoMock.isValidChildMoType(MO_TYPE)).thenReturn(false);
        when(parentMoMock.getFdn()).thenReturn(fdnMock);

        final MoFactory moFactory = new MoFactory(true);

        moFactory.getMo(CPP_MIM_VERSION, parentMoMock, MO_TYPE, MO_NAME);
    }

    @Test(expected = MaximumCardinalityExceededException.class)
    public void whenCreatingMoWithNoDefaultsWithParent_whereMaxCardinalityIsExceeded_thenMaximumCardinalityExceededExceptionIsThrown()
            throws InvalidPlatformRuntimeException, InvalidChildException, MaximumCardinalityExceededException {
        when(classMock.getSystemCreated()).thenReturn(new SystemCreated());
        when(momParserMock.getMimClasses()).thenReturn(classes);
        when(momParserMock.getMimRelationships()).thenReturn(Collections.<String, Relationship>emptyMap());
        when(parentMoMock.isValidChildMoType(MO_TYPE)).thenReturn(true);
        when(parentMoMock.getFdn()).thenReturn(fdnMock);
        mockStatic(CardinalityAssessor.class);
        when(CardinalityAssessor.isMaxCardinalityExceeded(MO_TYPE, parentMoMock)).thenReturn(new Boolean(true));

        final MoFactory moFactory = new MoFactory(true);

        moFactory.getMo(CPP_MIM_VERSION, parentMoMock, MO_TYPE, MO_NAME);
    }
}
