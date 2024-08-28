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
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.de.onetwork.dms.exceptions.ChildNotFoundException;
import com.ericsson.de.onetwork.dms.exceptions.InvalidChildException;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;

/**
 * Unit tests for {@link CppMo}
 *
 * @author edalrey
 * @since 1Network_15.13
 */
public class CppMoTest {

    private final static String REQUESTED_MO_TYPE = "requestedMoType";
    private final static String REQUESTED_MO_NAME = "requestedMoName";

    @Mock
    private Class classMock;

    @Mock
    private Mo moMock;

    @InjectMocks
    private CppMo cppMock;

    private List<String> childTypes;

    @BeforeTest
    public void setUp() {
        childTypes = new ArrayList<>();
        childTypes.add(REQUESTED_MO_TYPE);
    }

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = InvalidChildException.class)
    public void whenGettingChildByTypeByName_withInvalidMoType_thenInvalidChildExceptionIsThrown() throws InvalidChildException,
            ChildNotFoundException {
        cppMock.setChildTypes(new ArrayList<String>());

        cppMock.getChildByTypeByName(REQUESTED_MO_TYPE, "");
    }

    @Test(expectedExceptions = ChildNotFoundException.class)
    public void whenGettingChildByTypeByName_withValidMoTypeEmpty_thenChildNotFoundExceptionIsThrown() throws InvalidChildException,
            ChildNotFoundException {
        cppMock.setChildTypes(childTypes);

        cppMock.getChildByTypeByName(REQUESTED_MO_TYPE, "");
    }

    @Test
    public void whenGettingChildByTypeByName_withValidMoTypeAndValidName_thenTheReturnedMoIsNotNull() throws InvalidChildException,
            ChildNotFoundException {
        cppMock.setChildTypes(childTypes);
        when(moMock.getType()).thenReturn(REQUESTED_MO_TYPE);
        when(moMock.getName()).thenReturn(REQUESTED_MO_NAME);
        cppMock.addChild(moMock);

        final Mo requestedMo = cppMock.getChildByTypeByName(REQUESTED_MO_TYPE, REQUESTED_MO_NAME);

        assertNotNull(requestedMo, "Null child has been added to Mo. This is an exceptional case.");
    }
}
