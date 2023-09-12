package org.flickit.flickitassessmentcore.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AssessmentKit {

    private final long id;
    private final List<MaturityLevel> maturityLevels;
}