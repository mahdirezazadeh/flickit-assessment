package org.flickit.assessment.core.application.domain.report;

import org.flickit.assessment.core.application.domain.MaturityScore;

import java.util.List;
import java.util.Set;

public record SubjectReport(SubjectReportItem subject,
                            List<TopAttribute> topStrengths,
                            List<TopAttribute> topWeaknesses,
                            List<AttributeReportItem> attributes) {

    public record SubjectReportItem(Long id,
                                    Long maturityLevelId,
                                    boolean isCalculateValid) {
    }

    public record AttributeReportItem(Long id, Long maturityLevelId, Set<MaturityScore> maturityScores) {
    }
}