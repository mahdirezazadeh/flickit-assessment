package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionImpactDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionnaireDslModelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.*;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionImpactMother.createAnswerOptionImpact;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.completeKit;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.createAttribute;
import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.flickit.assessment.kit.test.fixture.application.SubjectMother.subjectWithAttributes;
import static org.flickit.assessment.kit.test.fixture.application.dsl.AnswerOptionDslModelMother.answerOptionDslModel;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionUpdateKitPersisterTest {

    @InjectMocks
    private QuestionUpdateKitPersister persister;

    @Mock
    private UpdateQuestionPort updateQuestionPort;

    @Mock
    private CreateQuestionPort createQuestionPort;

    @Mock
    private CreateQuestionImpactPort createQuestionImpactPort;

    @Mock
    private DeleteQuestionImpactPort deleteQuestionImpactPort;

    @Mock
    private UpdateQuestionImpactPort updateQuestionImpactPort;

    @Mock
    private CreateAnswerOptionImpactPort createAnswerOptionImpactPort;

    @Mock
    private UpdateAnswerOptionImpactPort updateAnswerOptionImpactPort;

    @Mock
    private UpdateAnswerOptionPort updateAnswerOptionPort;

    @Mock
    private LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;

    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;

    @Test
    void testQuestionUpdateKitPersister_SameInputsAsDatabaseData_NoChange() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, Boolean.FALSE, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo), List.of(savedQuestionnaire));


        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit);

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionAdded_AddToDatabase() {
        var savedQuestionnaire1 = questionnaireWithTitle(QUESTIONNAIRE_TITLE2);
        savedQuestionnaire1.setQuestions(List.of());
        AssessmentKit savedKit = completeKit(List.of(), List.of(), List.of(savedQuestionnaire1));

        var levelTwo = levelTwo();
        var savedQuestionnaire2 = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_OLD_TITLE1, 1, null, false, savedQuestionnaire2.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire2.setQuestions(List.of(savedQuestion));

        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any())).thenReturn(List.of(answerOption1, answerOption2));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(savedQuestionnaire2);
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire1, savedQuestionnaire2).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit);

        verifyNoInteractions(
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionUpdated_UpdateInDatabase() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_OLD_TITLE1, 1, null, false, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo), List.of(savedQuestionnaire));

        doNothing().when(updateQuestionPort).update(any(UpdateQuestionPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit);

        verifyNoInteractions(
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactAdded_AddToDatabase() {
        var levelTwo = levelTwo();
        var levelThree = levelThree();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo, levelThree), List.of(savedQuestionnaire));

        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(1L);
        when(createAnswerOptionImpactPort.persist(any(CreateAnswerOptionImpactPort.Param.class))).thenReturn(1L);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(any())).thenReturn(List.of(answerOption1, answerOption2));


        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslMaturityLevelThree = MaturityLevelDslModelMother.domainToDslModel(levelThree());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact1 = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslImpact2 = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelThree, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact1, dslImpact2), dslAnswerOptionList, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo, levelThree).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit);

        verifyNoInteractions(
            updateQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactDeleted_DeleteFromDatabase() {
        var levelTwo = levelTwo();
        var levelThree = levelThree();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact1 = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var savedImpact2 = createQuestionImpact(attribute.getId(), levelThree.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        var savedOptionImpact3 = createAnswerOptionImpact(answerOption1.getId(), 0.5);
        var savedOptionImpact4 = createAnswerOptionImpact(answerOption2.getId(), 0.75);
        savedImpact1.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedImpact2.setOptionImpacts(List.of(savedOptionImpact3, savedOptionImpact4));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact1, savedImpact2));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo, levelThree), List.of(savedQuestionnaire));

        doNothing().when(deleteQuestionImpactPort).delete(savedImpact2.getId());

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo, levelThree).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit);

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_QuestionImpactUpdated_UpdateInDatabase() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, savedQuestionnaire.getId());
        var attribute1 = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute1.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute1))), List.of(levelTwo), List.of(savedQuestionnaire));


        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 2);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute1).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit);

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_AnswerOptionImpactUpdated_UpdateInDatabase() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo), List.of(savedQuestionnaire));

        doNothing().when(updateAnswerOptionImpactPort).update(any(UpdateAnswerOptionImpactPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 0.75D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit);

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

    @Test
    void testQuestionUpdateKitPersister_AnswerOptionUpdated_UpdateInDatabase() {
        var levelTwo = levelTwo();
        var savedQuestionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var savedQuestion = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, false, savedQuestionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var savedImpact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, savedQuestion.getId());
        var answerOption1 = createAnswerOption(savedQuestion.getId(), OLD_OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(savedQuestion.getId(), OPTION_TITLE, OPTION_INDEX2);
        var savedOptionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var savedOptionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        savedImpact.setOptionImpacts(List.of(savedOptionImpact1, savedOptionImpact2));
        savedQuestion.setOptions(List.of(answerOption1, answerOption2));
        savedQuestion.setImpacts(List.of(savedImpact));
        savedQuestionnaire.setQuestions(List.of(savedQuestion));
        AssessmentKit savedKit = completeKit(List.of(subjectWithAttributes("subject", List.of(attribute))), List.of(levelTwo), List.of(savedQuestionnaire));


        doNothing().when(updateAnswerOptionPort).update(any(UpdateAnswerOptionPort.Param.class));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslQuestionnaire = QuestionnaireDslModelMother.domainToDslModel(questionnaireWithTitle(QUESTIONNAIRE_TITLE1));
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE);
        AssessmentKitDslModel dslKit = AssessmentKitDslModel.builder()
            .questionnaires(List.of(dslQuestionnaire))
            .questions(List.of(dslQuestion))
            .build();

        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        ctx.put(KEY_MATURITY_LEVELS, Stream.of(levelTwo).collect(toMap(MaturityLevel::getCode, MaturityLevel::getId)));
        ctx.put(KEY_QUESTIONNAIRES, Stream.of(savedQuestionnaire).collect(toMap(Questionnaire::getCode, Questionnaire::getId)));
        ctx.put(KEY_ATTRIBUTES, Stream.of(attribute).collect(toMap(Attribute::getCode, Attribute::getId)));
        persister.persist(ctx, savedKit, dslKit);

        verifyNoInteractions(
            updateQuestionPort,
            createQuestionImpactPort,
            deleteQuestionImpactPort,
            updateQuestionImpactPort,
            createAnswerOptionImpactPort,
            updateAnswerOptionImpactPort,
            createQuestionPort,
            loadAnswerOptionsByQuestionPort,
            createAnswerOptionPort
        );
    }

}
