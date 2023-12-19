package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class GetEvidenceListRestController {

    private final GetEvidenceListUseCase useCase;

    @GetMapping("/evidences")
    public ResponseEntity<PaginatedResponse<EvidenceListItem>> getEvidenceList(
        @RequestParam(value = "questionId", required = false) // validated in the use-case param
        Long questionId,
        @RequestParam(value = "assessmentId", required = false) // validated in the use-case param
        UUID assessmentId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        PaginatedResponse<EvidenceListItem> result = useCase.getEvidenceList(toParam(questionId, assessmentId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetEvidenceListUseCase.Param toParam(Long questionId, UUID assessmentId, int size, int page) {
        return new GetEvidenceListUseCase.Param(questionId, assessmentId, size, page);
    }
}
