package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AnswerOption;

public class AnswerOptionMother {

    private static Long id = 1L;

    public static AnswerOption createAnswerOption(Long questionId, String title, int index) {
        return new AnswerOption(
            id++,
            questionId,
            title,
            index
        );
    }
}
