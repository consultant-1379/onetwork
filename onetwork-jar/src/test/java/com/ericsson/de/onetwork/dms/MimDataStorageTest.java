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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.de.onetwork.MimXmlFileTestConstants;
import com.ericsson.de.onetwork.dms.exceptions.DataModellerServiceException;
import com.ericsson.de.onetwork.dms.momparser.MomParser;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;

/**
 * Unit tests for {@link MimDataStorage};
 *
 * @author edalrey
 * @since 1Network_15.14
 */
@RunWith(MockitoJUnitRunner.class)
public class MimDataStorageTest {

    private final static String CPP_MIM_VERSION = MimXmlFileTestConstants.CPP_MIM;
    private final static String INVALID_MO_TYPE = "invalid";
    private final static String NON_SYSTEM_CREATED_MO_TYPE = "EUtranCellFDD";
    private final static String SYSTEM_CREATED_MO_TYPE = "ENodeBFunction";

    @BeforeClass
    public static void init() throws DataModellerServiceException {
        MimDataStorage.loadMimVersionToMemory(CPP_MIM_VERSION);
    }

    @Mock
    private Class classMock;

    @Mock
    private MomParser momParserMock;

    @Test
    public void whenGettingRootMoClass_andNoParentExists_returnMoClass() {
        when(classMock.getName()).thenReturn(SYSTEM_CREATED_MO_TYPE);

        final Class resultClass = MimDataStorage.getClassByMoType(CPP_MIM_VERSION, SYSTEM_CREATED_MO_TYPE);

        assertEquals("Wrong class returned", classMock.getName(), resultClass.getName());
    }

    @Test(expected = IllegalStateException.class)
    public void whenMoTypeIsNotValidType_thenReturnsFalseForSystemCreated() {
        final boolean systemCreatedResult = MimDataStorage.isMoSystemCreated(CPP_MIM_VERSION, INVALID_MO_TYPE);

        assertFalse("Returned system created as true for an invalid MO type", systemCreatedResult);
    }

    @Test
    public void whenMoTypeSystemCreatedIsNull_thenReturnsFalseForSystemCreated() {
        final boolean systemCreatedResult = MimDataStorage.isMoSystemCreated(CPP_MIM_VERSION, NON_SYSTEM_CREATED_MO_TYPE);

        assertFalse("Returned system created as true for non-system-created MO type", systemCreatedResult);
    }

    @Test
    public void whenMoTypeHasSystemCreated_thenReturnsTrueForSystemCreated() {
        final boolean systemCreatedResult = MimDataStorage.isMoSystemCreated(CPP_MIM_VERSION, SYSTEM_CREATED_MO_TYPE);

        assertTrue("Returned system created as false for system-created MO type", systemCreatedResult);
    }

}
