package org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.DeleteAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.UpdateAnswerOptionImpactPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerOptionImpactPersistenceJpaAdapter implements
    CreateAnswerOptionImpactPort,
    DeleteAnswerOptionImpactPort,
    UpdateAnswerOptionImpactPort {

    private final AnswerOptionImpactJpaRepository repository;
    private final QuestionImpactJpaRepository questionImpactRepository;

    @Override
    public Long persist(CreateAnswerOptionImpactPort.Param param) {
        return repository.save(AnswerOptionImpactMapper.mapToJpaEntity(
            param,
            questionImpactRepository.findById(param.questionImpactId())
            )).getId();
    }

    @Override
    public void delete(Long impactId, Long optionId) {
        repository.deleteByQuestionImpactIdAndOptionId(impactId, optionId);
    }

    @Override
    public void update(UpdateAnswerOptionImpactPort.Param param) {
        repository.update(param.impactId(), param.optionId(), param.value());
    }
}
