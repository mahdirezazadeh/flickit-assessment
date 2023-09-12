package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentProgressPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentProgressService implements GetAssessmentProgressUseCase {

    private final GetAssessmentProgressPort getAssessmentProgressPort;

    @Override
    public Result getAssessmentProgress(Param param) {
        var result = getAssessmentProgressPort.getAssessmentProgressById(param.getAssessmentId());
        return new Result(result.id(), result.allAnswersCount());
    }
}