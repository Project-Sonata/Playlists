package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.dto.PlaylistItemsDto;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.service.tracks.AddItemPayload;
import com.odeyalo.sonata.playlists.service.tracks.PlaylistItemsOperationsFacade;
import com.odeyalo.sonata.playlists.support.converter.PlaylistItemDtoConverter;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import com.odeyalo.sonata.playlists.support.web.HttpStatuses;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.odeyalo.sonata.playlists.support.web.HttpStatuses.defaultOkStatus;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public final class PlaylistItemController {
    private final PlaylistItemsOperationsFacade playlistItemsOperationsFacade;
    private final PlaylistItemDtoConverter playlistItemDtoConverter;

    @GetMapping(value = "/{playlistId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PlaylistItemsDto>> fetchPlaylistItems(@PathVariable("playlistId") @NotNull final TargetPlaylist playlist,
                                                                     @NotNull final Pagination pagination,
                                                                     @NotNull final User user) {
        return playlistItemsOperationsFacade.loadPlaylistItems(playlist, pagination, user)
                .map(playlistItemDtoConverter::toPlaylistItemDto)
                .collectList()
                .map(items -> defaultOkStatus(new PlaylistItemsDto(items)));
    }

    @PostMapping(value = "/{playlistId}/items")
    public Mono<ResponseEntity<Object>> addPlaylistItems(@PathVariable("playlistId") @NotNull final TargetPlaylist playlist,
                                                         @NotNull final AddItemPayload addItemPayload,
                                                         @NotNull final PlaylistCollaborator playlistCollaborator,
                                                         @NotNull final User user) {

        return playlistItemsOperationsFacade.addItems(playlist, addItemPayload, playlistCollaborator, user)
                .thenReturn(HttpStatuses.defaultCreatedStatus());
    }
}
