package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.application.domain.crud.KitUserPaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitUserListServiceTest {

    @InjectMocks
    private GetKitUserListService service;

    @Mock
    private LoadKitUsersPort loadKitUsersPort;

    @Mock
    private LoadExpertGroupIdPort loadExpertGroupIdPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Test
    void testGetKitUserList_ValidInputs_ValidResults() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        int page = 0;
        int size = 10;
        UUID currentUserId = UUID.randomUUID();

        List<KitUserPaginatedResponse.UserListItem> kitUserListItems = List.of(
            new KitUserPaginatedResponse.UserListItem("UserName1", "UserEmail1@email.com"),
            new KitUserPaginatedResponse.UserListItem("UserName2", "UserEmail2@email.com")
        );
        PaginatedResponse<KitUserPaginatedResponse.UserListItem> paginatedResponse = new PaginatedResponse<>(
            kitUserListItems,
            page,
            size,
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            kitUserListItems.size());
        KitUserPaginatedResponse kitUserPaginatedResponse = new KitUserPaginatedResponse(
            paginatedResponse,
            new KitUserPaginatedResponse.Kit(kitId, "kit title"),
            new KitUserPaginatedResponse.ExpertGroup(expertGroupId, "expert group title"));
        when(loadExpertGroupIdPort.loadExpertGroupId(kitId)).thenReturn(Optional.of(expertGroupId));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(loadKitUsersPort.load(any(LoadKitUsersPort.Param.class))).thenReturn(kitUserPaginatedResponse);

        var param = new GetKitUserListUseCase.Param(kitId, page, size, currentUserId);
        var result = service.getKitUserList(param);

        var LoadExpertGroupIdParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(LoadExpertGroupIdParam.capture());
        assertEquals(param.getKitId(), LoadExpertGroupIdParam.getValue());

        var LoadExpertGroupOwnerParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(LoadExpertGroupOwnerParam.capture());
        assertEquals(expertGroupId, LoadExpertGroupOwnerParam.getValue());

        ArgumentCaptor<LoadKitUsersPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadKitUsersPort.Param.class);
        verify(loadKitUsersPort).load(loadPortParam.capture());

        assertEquals(kitId, loadPortParam.getValue().kitId());
        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(result.getResult().getItems());
        assertEquals(kitUserListItems, result.getResult().getItems());
        verify(loadKitUsersPort, times(1)).load(any(LoadKitUsersPort.Param.class));
    }

    @Test
    void testGetKitUserList_ValidInputs_EmptyResult() {
        Long kitId = 1L;
        Long expertGroupId = 1L;
        int page = 0;
        int size = 10;
        UUID currentUserId = UUID.randomUUID();

        PaginatedResponse<KitUserPaginatedResponse.UserListItem> paginatedResponse = new PaginatedResponse<>(
            Collections.emptyList(),
            page,
            size,
            UserJpaEntity.Fields.NAME,
            Sort.Direction.ASC.name().toLowerCase(),
            0);
        KitUserPaginatedResponse kitUserPaginatedResponse = new KitUserPaginatedResponse(
            paginatedResponse,
            new KitUserPaginatedResponse.Kit(kitId, "kit title"),
            new KitUserPaginatedResponse.ExpertGroup(expertGroupId, "expert group title"));
        when(loadExpertGroupIdPort.loadExpertGroupId(kitId)).thenReturn(Optional.of(expertGroupId));
        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(currentUserId));
        when(loadKitUsersPort.load(any(LoadKitUsersPort.Param.class))).thenReturn(kitUserPaginatedResponse);

        var param = new GetKitUserListUseCase.Param(kitId, page, size, currentUserId);
        var result = service.getKitUserList(param);

        var LoadExpertGroupIdParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupIdPort, times(1)).loadExpertGroupId(LoadExpertGroupIdParam.capture());
        assertEquals(param.getKitId(), LoadExpertGroupIdParam.getValue());

        var LoadExpertGroupOwnerParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(LoadExpertGroupOwnerParam.capture());
        assertEquals(expertGroupId, LoadExpertGroupOwnerParam.getValue());

        ArgumentCaptor<LoadKitUsersPort.Param> loadPortParam = ArgumentCaptor.forClass(LoadKitUsersPort.Param.class);
        verify(loadKitUsersPort).load(loadPortParam.capture());

        assertEquals(kitId, loadPortParam.getValue().kitId());
        assertEquals(page, loadPortParam.getValue().page());
        assertEquals(size, loadPortParam.getValue().size());
        assertNotNull(result.getResult().getItems());
        assertEquals(0, result.getResult().getItems().size());
        verify(loadKitUsersPort, times(1)).load(any(LoadKitUsersPort.Param.class));
    }
}