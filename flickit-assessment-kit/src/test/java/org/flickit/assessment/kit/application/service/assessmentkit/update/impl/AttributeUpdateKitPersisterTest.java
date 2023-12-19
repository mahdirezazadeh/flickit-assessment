package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;
import org.flickit.assessment.kit.application.port.out.attribute.UpdateAttributePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.AttributeMother;
import org.flickit.assessment.kit.test.fixture.application.SubjectMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.AttributeDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.SubjectDslModelMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_ATTRIBUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AttributeUpdateKitPersisterTest {

    @InjectMocks
    private AttributeUpdateKitPersister persister;

    @Captor
    ArgumentCaptor<UpdateAttributePort.Param> captor;

    @Mock
    private UpdateAttributePort updateAttributePort;

    @Test
    void testOrder() {
        Assertions.assertEquals(4, persister.order());
    }

    @Test
    void testPersist_TwoAttributesWithoutAnyChange_NoUpdate() {
        Attribute attrOne = AttributeMother.attributeWithTitle("attr1");
        Attribute attrTwo = AttributeMother.attributeWithTitle("attr2");
        Subject subject = SubjectMother.subjectWithAttributes("subject1", Arrays.asList(attrOne, attrTwo));
        AssessmentKit savedKit = AssessmentKitMother.kitWithSubjects(List.of(subject));

        SubjectDslModel subjectDslModel = SubjectDslModelMother.domainToDslModel(subject);
        AttributeDslModel dslAttrOne =
            AttributeDslModelMother.domainToDslModel(attrOne, e -> {
                e.subjectCode(subject.getCode());
            });
        AttributeDslModel dslAttrTwo =
            AttributeDslModelMother.domainToDslModel(attrTwo, e -> {
                e.subjectCode(subject.getCode());
            });
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(subjectDslModel))
            .attributes(List.of(dslAttrOne, dslAttrTwo))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit);

        Mockito.verify(updateAttributePort, Mockito.times(0)).update(captor.capture());
        assertFalse(result.shouldInvalidateCalcResult());

        Map<String, Long> codeToIdMap = ctx.get(KEY_ATTRIBUTES);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());
    }

    @Test
    void testPersist_TwoAttributesWithModifiedDescriptionAndTitle_UpdateWithoutRecalculateAssessments() {
        Attribute attrOne = AttributeMother.attributeWithTitle("attr1");
        Attribute attrTwo = AttributeMother.attributeWithTitle("attr2");
        Subject subject = SubjectMother.subjectWithAttributes("subject1", Arrays.asList(attrOne, attrTwo));
        AssessmentKit savedKit = AssessmentKitMother.kitWithSubjects(List.of(subject));

        SubjectDslModel subjectDslModel = SubjectDslModelMother.domainToDslModel(subject);
        AttributeDslModel dslAttrOne =
            AttributeDslModelMother.domainToDslModel(attrOne, e -> {
                e.description("new description1");
                e.title("new title1");
                e.subjectCode(subject.getCode());
            });
        AttributeDslModel dslAttrTwo =
            AttributeDslModelMother.domainToDslModel(attrTwo, e -> {
                e.description("new description2");
                e.title("new title2");
                e.subjectCode(subject.getCode());
            });
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(subjectDslModel))
            .attributes(List.of(dslAttrOne, dslAttrTwo))
            .build();

        Mockito.doNothing().when(updateAttributePort).update(Mockito.any());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit);

        Mockito.verify(updateAttributePort, Mockito.times(2)).update(captor.capture());
        List<UpdateAttributePort.Param> params = captor.getAllValues();
        UpdateAttributePort.Param firstAttr = params.get(0);
        UpdateAttributePort.Param secondAttr = params.get(1);

        assertEquals(attrOne.getId(), firstAttr.id());
        assertEquals(dslAttrOne.getTitle(), firstAttr.title());
        assertEquals(attrOne.getIndex(), firstAttr.index());
        assertEquals(dslAttrOne.getDescription(), firstAttr.description());
        assertEquals(attrOne.getWeight(), firstAttr.weight());
        assertThat(firstAttr.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertEquals(subject.getId(), firstAttr.subjectId());

        assertEquals(attrTwo.getId(), secondAttr.id());
        assertEquals(dslAttrTwo.getTitle(), secondAttr.title());
        assertEquals(attrTwo.getIndex(), secondAttr.index());
        assertEquals(dslAttrTwo.getDescription(), secondAttr.description());
        assertEquals(attrTwo.getWeight(), secondAttr.weight());
        assertThat(secondAttr.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertEquals(subject.getId(), secondAttr.subjectId());
        assertFalse(result.shouldInvalidateCalcResult());

        Map<String, Long> codeToIdMap = ctx.get(KEY_ATTRIBUTES);
        assertNotNull(codeToIdMap);
        assertEquals(2, codeToIdMap.keySet().size());
    }

    @Test
    void testPersist_TwoAttributeWithModifiedWeightAndSubjectId_UpdateWithRecalculateAssessments() {
        Attribute attrOne = AttributeMother.attributeWithTitle("attr1");
        Attribute attrTwo = AttributeMother.attributeWithTitle("attr2");
        Attribute attrThree = AttributeMother.attributeWithTitle("attr3");
        Subject subjectOne = SubjectMother.subjectWithAttributes("subject1", Arrays.asList(attrOne, attrTwo));
        Subject subjectTwo = SubjectMother.subjectWithAttributes("subject2", List.of(attrThree));
        AssessmentKit savedKit = AssessmentKitMother.kitWithSubjects(List.of(subjectOne, subjectTwo));

        SubjectDslModel subjectDslModel = SubjectDslModelMother.domainToDslModel(subjectTwo);
        AttributeDslModel dslAttrOne =
            AttributeDslModelMother.domainToDslModel(attrOne, e -> {
                e.weight(2);
                e.subjectCode(subjectTwo.getCode());
            });
        AttributeDslModel dslAttrTwo =
            AttributeDslModelMother.domainToDslModel(attrTwo, e -> {
                e.weight(3);
                e.subjectCode(subjectOne.getCode());
            });
        AttributeDslModel dslAttrThree =
            AttributeDslModelMother.domainToDslModel(attrThree, e -> {
                e.subjectCode(subjectTwo.getCode());
            });

        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .subjects(List.of(subjectDslModel))
            .attributes(List.of(dslAttrOne, dslAttrTwo, dslAttrThree))
            .build();

        Mockito.doNothing().when(updateAttributePort).update(Mockito.any());

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        UpdateKitPersisterResult result = persister.persist(ctx, savedKit, dslKit);

        Mockito.verify(updateAttributePort, Mockito.times(2)).update(captor.capture());
        List<UpdateAttributePort.Param> params = captor.getAllValues();
        UpdateAttributePort.Param firstAttr = params.get(0);
        UpdateAttributePort.Param secondAttr = params.get(1);

        assertEquals(attrOne.getId(), firstAttr.id());
        assertEquals(attrOne.getTitle(), firstAttr.title());
        assertEquals(attrOne.getIndex(), firstAttr.index());
        assertEquals(attrOne.getDescription(), firstAttr.description());
        assertEquals(dslAttrOne.getWeight(), firstAttr.weight());
        assertThat(firstAttr.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertEquals(subjectTwo.getId(), firstAttr.subjectId());

        assertEquals(attrTwo.getId(), secondAttr.id());
        assertEquals(attrTwo.getTitle(), secondAttr.title());
        assertEquals(attrTwo.getIndex(), secondAttr.index());
        assertEquals(attrTwo.getDescription(), secondAttr.description());
        assertEquals(dslAttrTwo.getWeight(), secondAttr.weight());
        assertThat(secondAttr.lastModificationTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertEquals(subjectOne.getId(), secondAttr.subjectId());
        assertTrue(result.shouldInvalidateCalcResult());

        Map<String, Long> codeToIdMap = ctx.get(KEY_ATTRIBUTES);
        assertNotNull(codeToIdMap);
        assertEquals(3, codeToIdMap.keySet().size());
    }
}

