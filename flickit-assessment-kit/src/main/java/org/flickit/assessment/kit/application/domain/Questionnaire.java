package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Questionnaire {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    @Setter
    private List<Question> questions;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
}
