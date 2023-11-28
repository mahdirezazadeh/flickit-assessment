package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    UpdateSubjectPort {

    private final SubjectJpaRepository repository;

    @Override
    public void updateByCodeAndKitId(Param param) {
        repository.updateByCodeAndKitId(param.code(),
            param.title(),
            param.index(),
            param.description(),
            param.lastModificationTime(),
            param.kitId());
    }
}
