package org.flickit.assessment.core.application.port.out.qualityattributevalue;

import java.util.List;
import java.util.UUID;

public interface CreateQualityAttributeValuePort {

    void persistAll(List<Long> qualityAttributeIds, UUID resultId);
}
