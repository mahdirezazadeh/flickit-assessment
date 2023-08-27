package org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;

public record AnswerOptionImpactDto(Long id,
                                    Double value,
                                    @JsonProperty("question_impact")
                                    QuestionImpactDto questionImpact) {

    public AnswerOptionImpact dtoToDomain() {
        return new AnswerOptionImpact(id, value, questionImpact.dtoToDomain());
    }
}