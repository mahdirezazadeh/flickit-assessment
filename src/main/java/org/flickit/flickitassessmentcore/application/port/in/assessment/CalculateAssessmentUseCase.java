package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CALCULATE_ASSESSMENT_ID_NOT_NULL;

public interface CalculateAssessmentUseCase {

    Result calculateMaturityLevel(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CALCULATE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        public Param(UUID assessmentId) {
            this.assessmentId = assessmentId;
            this.validateSelf();
        }
    }

    record Result(MaturityLevel maturityLevel) {
    }
}