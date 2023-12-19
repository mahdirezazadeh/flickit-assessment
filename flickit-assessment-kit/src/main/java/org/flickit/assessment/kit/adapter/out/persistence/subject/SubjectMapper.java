package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectMapper {

    public static Subject mapToDomainModel(SubjectJpaEntity entity, List<Attribute> attributes) {
        return new Subject(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            attributes,
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }
}

