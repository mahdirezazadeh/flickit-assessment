package org.flickit.assessment.core.adapter.out.persistence.subjectvalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectValueMapper {

    public static SubjectValueJpaEntity mapToJpaEntity(Long subjectId){
        return new SubjectValueJpaEntity(
            null,
            null,
            subjectId,
            null,
            null
        );
    }
}
