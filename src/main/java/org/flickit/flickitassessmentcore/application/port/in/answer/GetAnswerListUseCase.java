package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface GetAnswerListUseCase {

    PaginatedResponse<AnswerListItem> getAnswerList(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ANSWER_LIST_ASSESSMENT_ID_NOTNULL)
        UUID assessmentId;

        @NotNull(message = GET_ANSWER_LIST_QUESTIONNAIRE_ID_NOTNULL)
        Long questionnaireId;

        @Min(value = 1, message = GET_ANSWER_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ANSWER_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ANSWER_LIST_PAGE_MIN)
        int page;

        public Param(UUID assessmentId, Long questionnaireId, int size, int page) {
            this.assessmentId = assessmentId;
            this.questionnaireId = questionnaireId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record AnswerListItem(
        UUID id,
        Long questionId,
        Long answerOptionId,
        Boolean isNotApplicable){
    }
}