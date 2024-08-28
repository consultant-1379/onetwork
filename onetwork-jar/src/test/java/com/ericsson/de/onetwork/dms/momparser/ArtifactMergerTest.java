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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.testng.annotations.BeforeMethod;

import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Class;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.DerivedDataType;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Enum;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Exception;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.InterMim;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Mim;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Preliminary;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Relationship;
import com.ericsson.de.onetwork.dms.momparser.schema.mpdtd.Struct;
import com.ericsson.de.onetwork.dms.util.MoFormatter;

/**
 * Unit tests for {@link ArtifactMerger}
 *
 * @author edalrey
 * @since 1Network_15.13
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(MoFormatter.class)
public class ArtifactMergerTest {

    private final static String NON_EMPTY_COLLECTION = "The input Mim Collection is not empty.";
    private final static String CLASS_NAME = "className";
    private final static String RELATIONSHIP_NAME = "Namespace:ParentMoType_to_Namespace:ChildMoType";

    @Mock
    private Mim mimMock;

    @Mock
    private InterMim interMimMock;

    @Mock
    private Class classMock;

    @Mock
    private Struct structMock;

    @Mock
    private Enum enumMock;

    @Mock
    private DerivedDataType derivedDataTypeMock;

    @Mock
    private Exception exceptionMock;

    @Mock
    private Relationship relationshipMock;

    @Mock
    private Deprecated deprecatedMock;

    @Mock
    private Preliminary preliminaryMock;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenMergeClassArtifacts_withEmptyMimCollection_thenReturnedMapIsEmpty() {
        final Collection<Mim> mims = new ArrayList<>();

        final Map<String, Class> returnedClasses = ArtifactMerger.mergeAllClassArtifacts(mims);

        verify(mimMock, never()).getClazz();
        assertTrue(NON_EMPTY_COLLECTION, returnedClasses.isEmpty());
    }

    @Test
    public void whenMergeClassArtifacts_withMimsContainingNoClasses_thenReturnedMapIsEmpty() {
        final Collection<Mim> mims = new ArrayList<>();
        final List<Class> classes = new ArrayList<>();
        mims.add(mimMock);
        when(mimMock.getClazz()).thenReturn(classes);

        final Map<String, Class> returnedClasses = ArtifactMerger.mergeAllClassArtifacts(mims);

        verify(mimMock, times(1)).getClazz();
        assertTrue("At least one Mim, from the input Mim Collection, has at least one Class artifact.", returnedClasses.isEmpty());
    }

    @Test
    public void whenMergeClassArtifacts_withAnyMimContainingAtLeastOneClass_thenReturnedMapContainsClasses() {
        final Collection<Mim> mims = new ArrayList<>();
        mims.add(mimMock);
        final List<Class> classes = new ArrayList<>();
        classes.add(classMock);
        when(mimMock.getClazz()).thenReturn(classes);
        when(classMock.getName()).thenReturn(CLASS_NAME);

        final Map<String, Class> returnedClasses = ArtifactMerger.mergeAllClassArtifacts(mims);

        verify(mimMock, times(1)).getClazz();
        assertTrue("The Mim does not contain the expected Class instance.", returnedClasses.keySet().contains(CLASS_NAME));
    }

    @Test
    public void whenMergeAllAttributeSpecificationArtifacts_withEmptyMimCollection_thenReturnedCollectionIsEmpty() {
        final Collection<Mim> mims = new ArrayList<>();

        final Collection<Object> mergedMimAttributeSpecifications = ArtifactMerger.mergeAllAttributeSpecificationArtifacts(mims);

        verify(mimMock, never()).getClazz();
        assertTrue(NON_EMPTY_COLLECTION, mergedMimAttributeSpecifications.isEmpty());
    }

    @Test
    public void whenMergeAllAttributeSpecificationArtifacts_withMimsContainingNoAttributeSpecification_thenReturnedCollectionIsEmpty() {
        final Collection<Mim> mims = new ArrayList<>();
        final List<Object> attributeSpecifications = new ArrayList<>();
        mims.add(mimMock);
        attributeSpecifications.add(exceptionMock);
        when(mimMock.getStructOrEnumOrExceptionOrDerivedDataType()).thenReturn(attributeSpecifications);

        final Collection<Object> mergedMimAttributeSpecifications = ArtifactMerger.mergeAllAttributeSpecificationArtifacts(mims);

        verify(mimMock, times(1)).getStructOrEnumOrExceptionOrDerivedDataType();
        assertTrue("At least one Mim, from the input Mim Collection, has at least one Enum, Struct or DerivedDataType artifact.",
                mergedMimAttributeSpecifications.isEmpty());
    }

    // AttrSpec = AttributeSpecification
    @Test
    public void whenMergeAllAttributeSpecificationArtifacts_withAnyMimContainingAtLeastOneAttrSpec_thenReturnedMapContainsAttrSpecArtifacts() {
        final Collection<Mim> mims = new ArrayList<>();
        final List<Object> possibleAttributeSpecification = new ArrayList<>();
        mims.add(mimMock);
        possibleAttributeSpecification.add(structMock);
        possibleAttributeSpecification.add(enumMock);
        when(mimMock.getStructOrEnumOrExceptionOrDerivedDataType()).thenReturn(possibleAttributeSpecification);

        final Collection<Object> mergedAttributeSpecifications = ArtifactMerger.mergeAllAttributeSpecificationArtifacts(mims);

        verify(mimMock, times(1)).getStructOrEnumOrExceptionOrDerivedDataType();
        assertEquals("The Mim does not contain the expected Enum, Struct and DerivedDataType instances.", 2, mergedAttributeSpecifications.size());
    }

    @Test
    public void whenMergeRelationshipArtifacts_withEmptyMimAndInterMimCollections_thenReturnedMapIsEmpty() {
        final Collection<Mim> mims = new ArrayList<>();
        final Collection<InterMim> interMims = new ArrayList<>();

        final Map<String, Relationship> returnedRelationships = ArtifactMerger.mergeAllRelationshipArtifacts(mims, interMims);

        verify(mimMock, never()).getRelationship();
        verify(interMimMock, never()).getRelationship();
        assertTrue(NON_EMPTY_COLLECTION, returnedRelationships.isEmpty());
    }

    @Test
    public void whenMergeRelationshipArtifacts_withMimsContainingNoRelationships_thenReturnedMapIsEmpty() {
        final Collection<Mim> mims = new ArrayList<>();
        final Collection<InterMim> interMims = new ArrayList<>();
        final List<Relationship> relationships = new ArrayList<>();
        mims.add(mimMock);
        when(mimMock.getRelationship()).thenReturn(relationships);

        final Map<String, Relationship> returnedRelationships = ArtifactMerger.mergeAllRelationshipArtifacts(mims, interMims);

        verify(mimMock, times(1)).getRelationship();
        verify(interMimMock, never()).getRelationship();
        assertTrue("At least one Mim, from the input Mim Collection, has at least one Relationship artifact.", returnedRelationships.isEmpty());
    }

    @Test
    public void whenMergeRelationshipArtifacts_withMimsContainingOnlyDeprecatedRelationships_thenReturnedMapIsEmpty() {
        final Collection<Mim> mims = new ArrayList<>();
        final Collection<InterMim> interMims = new ArrayList<>();
        final List<Relationship> relationships = new ArrayList<>();
        final List<Object> preliminaryOrDeprecatedOrObsolete = new ArrayList<>();
        mims.add(mimMock);
        relationships.add(relationshipMock);
        preliminaryOrDeprecatedOrObsolete.add(deprecatedMock);
        when(mimMock.getRelationship()).thenReturn(relationships);
        when(relationshipMock.getPreliminaryOrDeprecatedOrObsolete()).thenReturn(preliminaryOrDeprecatedOrObsolete);

        final Map<String, Relationship> returnedRelationships = ArtifactMerger.mergeAllRelationshipArtifacts(mims, interMims);

        verify(mimMock, times(1)).getRelationship();
        verify(interMimMock, never()).getRelationship();
        verify(relationshipMock, times(1)).getPreliminaryOrDeprecatedOrObsolete();
        assertTrue("At least one Mim contains a non-deprecated Relationship instance.", returnedRelationships.isEmpty());
    }

    @Test
    public void whenMergeRelationshipArtifacts_withMimsContainingAtLeastOneNonDeprecatedRelationships_thenReturnedMapContainsRelationships() {
        final Collection<Mim> mims = new ArrayList<>();
        final Collection<InterMim> interMims = new ArrayList<>();
        final List<Relationship> relationships = new ArrayList<>();
        final List<Object> preliminaryOrDeprecatedOrObsolete = new ArrayList<>();
        mims.add(mimMock);
        relationships.add(relationshipMock);
        preliminaryOrDeprecatedOrObsolete.add(preliminaryMock);
        when(mimMock.getRelationship()).thenReturn(relationships);
        when(relationshipMock.getName()).thenReturn(RELATIONSHIP_NAME);
        when(relationshipMock.getPreliminaryOrDeprecatedOrObsolete()).thenReturn(preliminaryOrDeprecatedOrObsolete);
        mockStatic(MoFormatter.class);
        org.powermock.api.mockito.PowerMockito.when(MoFormatter.removeComEcimNamespaceFrom(RELATIONSHIP_NAME)).thenReturn(RELATIONSHIP_NAME);

        final Map<String, Relationship> returnedRelationships = ArtifactMerger.mergeAllRelationshipArtifacts(mims, interMims);

        verify(mimMock, times(1)).getRelationship();
        verify(interMimMock, never()).getRelationship();
        verify(relationshipMock, times(1)).getPreliminaryOrDeprecatedOrObsolete();
        assertEquals("The Mims contain only deprecated Relationship instances, or no Relationship at all.", 1, returnedRelationships.size());
    }

    @Test
    public void whenMergeRelationshipArtifacts_withInterMimsContainingAtLeastOneNonDeprecatedRelationship_thenReturnedMapContainsRelationships() {
        final Collection<Mim> mims = new ArrayList<>();
        final Collection<InterMim> interMims = new ArrayList<>();
        final List<Relationship> relationships = new ArrayList<>();
        final List<Object> preliminaryOrDeprecatedOrObsolete = new ArrayList<>();
        interMims.add(interMimMock);
        relationships.add(relationshipMock);
        when(interMimMock.getRelationship()).thenReturn(relationships);
        when(relationshipMock.getName()).thenReturn(RELATIONSHIP_NAME);
        when(relationshipMock.getPreliminaryOrDeprecatedOrObsolete()).thenReturn(preliminaryOrDeprecatedOrObsolete);

        final Map<String, Relationship> returnedRelationships = ArtifactMerger.mergeAllRelationshipArtifacts(mims, interMims);

        verify(mimMock, never()).getRelationship();
        verify(interMimMock, times(1)).getRelationship();
        verify(relationshipMock, times(1)).getPreliminaryOrDeprecatedOrObsolete();
        assertEquals("The InterMims contain only deprecated Relationship instances, or no Relationship at all.", 1, returnedRelationships.size());
    }
}
