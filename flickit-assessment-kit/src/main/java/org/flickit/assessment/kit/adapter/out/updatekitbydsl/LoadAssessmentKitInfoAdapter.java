package org.flickit.assessment.kit.adapter.out.updatekitbydsl;

import lombok.AllArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.levelcompetence.MaturityLevelCompetenceMapper;
import org.flickit.assessment.kit.adapter.out.persistence.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.kit.adapter.out.persistence.questionnaire.QuestionnaireMapper;
import org.flickit.assessment.kit.adapter.out.persistence.subject.SubjectMapper;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.FIND_KIT_ID_NOT_FOUND;

@Component
@AllArgsConstructor
public class LoadAssessmentKitInfoAdapter implements LoadAssessmentKitInfoPort {

    private final AssessmentKitJpaRepository repository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final LevelCompetenceJpaRepository levelCompetenceRepository;
    private final SubjectJpaRepository subjectRepository;
    private final QuestionnaireJpaRepository questionnaireRepository;

    @Override
    public AssessmentKit load(Long kitId) {
        AssessmentKitJpaEntity entity = repository.findById(kitId).orElseThrow(
            () -> new ResourceNotFoundException(FIND_KIT_ID_NOT_FOUND));

        List<Subject> subjects = subjectRepository.findAllByAssessmentKitId(kitId).stream()
            .map(SubjectMapper::mapToDomainModel)
            .toList();

        List<MaturityLevel> levels = maturityLevelRepository.findAllByAssessmentKitId(kitId).stream()
            .map(MaturityLevelMapper::mapToDomainModel)
            .toList();
        setLevelCompetences(levels);

        List<Questionnaire> questionnaires = questionnaireRepository.findAllByAssessmentKitId(kitId).stream()
            .map(QuestionnaireMapper::mapToDomainModel)
            .toList();

        return new AssessmentKit(
            kitId,
            entity.getCode(),
            entity.getTitle(),
            entity.getSummary(),
            entity.getAbout(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getIsActive(),
            entity.getExpertGroupId(),
            subjects,
            levels,
            questionnaires
        );
    }

    private void setLevelCompetences(List<MaturityLevel> levels) {
        levels.forEach(level -> level.setCompetences(
            levelCompetenceRepository.findByAffectedLevelId(level.getId()).stream()
                .map(MaturityLevelCompetenceMapper::mapToDomainModel)
                .toList()));
    }

}
