package org.flickit.assessment.kit.application.port.out.subjectquestionnaire;

import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;

import java.util.List;

public interface LoadSubjectQuestionnairePort {

    List<SubjectQuestionnaire> loadByKitId(long assessmentKitId);
}
