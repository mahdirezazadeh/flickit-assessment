package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.common.exception.api.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.kit.application.service.assessmentkit.validate.impl.DslFieldNames.ANSWER_OPTION;
import static org.flickit.assessment.kit.application.service.assessmentkit.validate.impl.DslFieldNames.QUESTION;

@Service
@RequiredArgsConstructor
public class QuestionUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        Map<String, Map<String, Question>> savedQuestionnaireToQuestionsMap = savedKit.getQuestionnaires().stream()
            .collect(toMap(Questionnaire::getCode, q -> {
                if (q.getQuestions() == null)
                    return Map.of();
                return q.getQuestions().stream()
                    .collect(toMap(Question::getCode, s -> s));
            }));
        Map<String, Map<String, QuestionDslModel>> dslQuestionnaireToQuestionsMap = dslKit.getQuestions().stream()
            .collect(groupingBy(QuestionDslModel::getQuestionnaireCode,
                toMap(QuestionDslModel::getCode, model -> model)
            ));

        for (Map.Entry<String, Map<String, Question>> questionnaireEntry : savedQuestionnaireToQuestionsMap.entrySet()) {
            Map<String, Question> codeToQuestion = questionnaireEntry.getValue();
            Map<String, QuestionDslModel> codeToDslQuestion = dslQuestionnaireToQuestionsMap.get(questionnaireEntry.getKey());

            if (codeToDslQuestion == null)
                continue;

            var deletedQuestions = codeToQuestion.keySet().stream()
                .filter(s -> !codeToDslQuestion.containsKey(s))
                .collect(toSet());

            var newQuestions = codeToDslQuestion.keySet().stream()
                .filter(s -> !codeToQuestion.containsKey(s))
                .collect(toSet());

            if (!deletedQuestions.isEmpty())
                notification.add(new InvalidDeletionError(QUESTION, deletedQuestions));

            if (!newQuestions.isEmpty())
                notification.add(new InvalidAdditionError(QUESTION, newQuestions));

            validateAnswerOptions(codeToQuestion, codeToDslQuestion, notification);
        }

        return notification;
    }

    private void validateAnswerOptions(Map<String, Question> codeToQuestion, Map<String, QuestionDslModel> codeToDslQuestion, Notification notification) {
        for (Map.Entry<String, Question> questionEntry : codeToQuestion.entrySet()) {
            Map<Integer, AnswerOption> savedOptionIndexMap = questionEntry.getValue()
                .getOptions().stream()
                .collect(toMap(AnswerOption::getIndex, a -> a));

            QuestionDslModel dslQuestion = codeToDslQuestion.get(questionEntry.getKey());
            if (dslQuestion == null)
                continue;

            Map<Integer, AnswerOptionDslModel> dslOptionIndexMap = dslQuestion.getAnswerOptions().stream()
                .collect(toMap(AnswerOptionDslModel::getIndex, a -> a));

            var deletedOptions = savedOptionIndexMap.entrySet().stream()
                .filter(savedOption -> !dslOptionIndexMap.containsKey(savedOption.getKey()))
                .map(answerOption -> answerOption.getValue().getTitle())
                .collect(toSet());

            var newOptions = dslOptionIndexMap.entrySet().stream()
                .filter(savedOption -> !savedOptionIndexMap.containsKey(savedOption.getKey()))
                .map(answerOption -> answerOption.getValue().getCaption())
                .collect(toSet());

            if (!deletedOptions.isEmpty())
                notification.add(new InvalidDeletionError(ANSWER_OPTION, deletedOptions));

            if (!newOptions.isEmpty())
                notification.add(new InvalidAdditionError(ANSWER_OPTION, newOptions));
        }
    }
}
