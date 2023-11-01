package org.flickit.assessment.core.test.fixture.adapter.dto;

import org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.core.adapter.out.rest.subject.SubjectDto;

import java.util.List;

public class SubjectDtoMother {

    public static SubjectDto createSubjectDto(Long subjectId, List<QualityAttributeValueJpaEntity> qualityAttributes) {
        return new SubjectDto(
            subjectId,
            qualityAttributes.stream()
                .map(qav -> AttributeDtoMother.createAttributeDto(qav.getQualityAttributeId()))
                .toList()
        );
    }
}