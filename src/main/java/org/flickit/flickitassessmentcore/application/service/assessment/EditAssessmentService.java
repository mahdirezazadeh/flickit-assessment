package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.EditAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.SaveAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EditAssessmentService implements EditAssessmentUseCase {

    private final LoadAssessmentPort loadAssessmentPort;
    private final SaveAssessmentPort saveAssessmentPort;

    @Override
    public Result editAssessment(Param param) {
        Assessment assessment = loadAssessmentPort.loadAssessment(param.getId()).assessment();
        Assessment newAssessment = new Assessment(
            assessment.getId(),
            assessment.getCode(),
            param.getTitle(),
            assessment.getCreationTime(),
            LocalDateTime.now(),
            param.getAssessmentKitId(),
            param.getColorId(),
            assessment.getSpaceId()
        );
        UUID id = saveAssessmentPort.saveAssessment(new SaveAssessmentPort.Param(newAssessment)).id();
        return new Result(id);
    }
}
