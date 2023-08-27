package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;

import java.util.UUID;

public interface LoadCalculateInfoPort {

    AssessmentResult load(UUID assessmentId);
}