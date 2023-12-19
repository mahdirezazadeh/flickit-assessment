package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface CreateAssessmentUseCase {

    Result createAssessment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotBlank(message = CREATE_ASSESSMENT_TITLE_NOT_BLANK)
        @Size(min = 3, message = CREATE_ASSESSMENT_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_ASSESSMENT_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_ASSESSMENT_SPACE_ID_NOT_NULL)
        Long spaceId;

        @NotNull(message = CREATE_ASSESSMENT_ASSESSMENT_KIT_ID_NOT_NULL)
        Long assessmentKitId;

        @NotNull(message = CREATE_ASSESSMENT_COLOR_ID_NOT_NULL)
        Integer colorId;

        public Param(Long spaceId, String title, Long assessmentKitId, Integer colorId) {
            this.title = title != null ? title.strip() : null;
            this.spaceId = spaceId;
            this.assessmentKitId = assessmentKitId;
            this.colorId = colorId;
            this.validateSelf();
        }
    }

    record Result(UUID id){
    }
}
