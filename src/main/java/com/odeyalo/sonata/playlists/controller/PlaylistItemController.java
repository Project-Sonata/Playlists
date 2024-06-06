package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.dto.PlaylistItemsDto;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.service.tracks.AddItemPayload;
import com.odeyalo.sonata.playlists.service.tracks.PlaylistItemsOperations;
import com.odeyalo.sonata.playlists.support.converter.PlaylistItemDtoConverter;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.odeyalo.sonata.playlists.support.web.HttpStatuses.defaultCreatedStatus;
import static com.odeyalo.sonata.playlists.support.web.HttpStatuses.defaultOkStatus;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public final class PlaylistItemController {

    private final PlaylistItemsOperations playlistItemsOperations;
    private final PlaylistItemDtoConverter playlistItemDtoConverter;
    private final PlaylistLoader playlistLoader;

    @GetMapping(value = "/{playlistId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PlaylistItemsDto>> fetchPlaylistItems(@PathVariable String playlistId,
                                                                     Pagination pagination) {
        return playlistItemsOperations.loadPlaylistItems(TargetPlaylist.just(playlistId), pagination)
                .map(playlistItemDtoConverter::toPlaylistItemDto)
                .collectList()
                .map(items -> defaultOkStatus(new PlaylistItemsDto(items)));
    }

    @PostMapping(value = "/{playlistId}/items")
    public Mono<ResponseEntity<Object>> addPlaylistItems(@PathVariable final String playlistId,
                                                         @NotNull final AddItemPayload addItemPayload,
                                                         @NotNull final PlaylistCollaborator playlistCollaborator,
                                                         AuthenticatedUser user) {

        Mono<ResponseEntity<Object>> addItems = playlistItemsOperations.addItems(TargetPlaylist.just(playlistId), addItemPayload, playlistCollaborator)
                .thenReturn(defaultCreatedStatus());

        return playlistLoader.loadPlaylist(TargetPlaylist.just(playlistId))
                .switchIfEmpty(
                        onPlaylistNotFoundError(TargetPlaylist.just(playlistId))
                )
                .flatMap(it -> {
                    if ( Objects.equals(it.getPlaylistOwner().getId(), user.getDetails().getId()) ) {
                        return addItems;
                    }

                    return Mono.just(
                            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                    );
                });
    }

    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull TargetPlaylist targetPlaylist) {
        return Mono.defer(
                () -> Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()))
        );
    }


}
