package org.flickit.flickitassessmentcore.test.fixture.application;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AssessmentResultMother {

    public static AssessmentResult invalidResultWithSubjectValues(List<SubjectValue> subjectValues) {
        return new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessment(), subjectValues);
    }

    public static AssessmentResult validResultWithSubjectValuesAndMaturityLevel(List<SubjectValue> subjectValues, MaturityLevel maturityLevel) {
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessment(), subjectValues);
        assessmentResult.setValid(true);
        assessmentResult.setMaturityLevel(maturityLevel);
        return assessmentResult;
    }

    public static AssessmentResult validResultWithJustAnId() {
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), null, new ArrayList<>());
        assessmentResult.setValid(true);
        return assessmentResult;
    }
}