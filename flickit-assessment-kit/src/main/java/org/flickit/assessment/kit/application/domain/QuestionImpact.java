package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class QuestionImpact {

    private final long attributeId;
    private final long maturityLevelId;
    private final int weight;
    private final List<AnswerOptionImpact> optionImpacts;
}