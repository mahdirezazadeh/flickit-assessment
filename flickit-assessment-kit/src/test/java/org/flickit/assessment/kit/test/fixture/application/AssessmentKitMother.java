package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.time.LocalDateTime;
import java.util.List;

public class AssessmentKitMother {

    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String ABOUT = "about";
    public static final long EXPERT_GROUP_ID = 1L;
    private static long id = 134L;


    public static AssessmentKit simpleKitWithMaturityLevels(List<MaturityLevel> maturityLevels) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            null,
            maturityLevels,
            null);
    }

    public static AssessmentKit kitWithFiveLevels(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            null,
            MaturityLevelMother.fiveLevels(),
            null);
    }

    public static AssessmentKit kitWithQuestionnaire(List<Questionnaire> questionnaires, Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            null,
            null,
            questionnaires
        );
    }

    public static AssessmentKit kitWithTwoSubject(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            SubjectMother.twoSubject(),
            null,
            null);
    }

    public static AssessmentKit kitWithThreeSubject(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            SubjectMother.threeSubjects(),
            null,
            null);
    }

    public static AssessmentKit kitWithOneSubject(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            SubjectMother.oneSubjects(),
            null,
            null);
    }

}
