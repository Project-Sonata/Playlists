package com.odeyalo.sonata.playlists.controller;


import com.odeyalo.sonata.playlists.dto.PlaylistItemsDto;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.exception.PlaylistOperationNotAllowedException;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.service.tracks.AddItemPayload;
import com.odeyalo.sonata.playlists.service.tracks.PlaylistItemsOperations;
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

    private final PlaylistItemsOperations playlistItemsOperations;
    private final PlaylistItemsOperationsFacade playlistItemsOperationsFacade;
    private final PlaylistItemDtoConverter playlistItemDtoConverter;
    private final PlaylistLoader loader;

    @GetMapping(value = "/{playlistId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PlaylistItemsDto>> fetchPlaylistItems(@PathVariable("playlistId") @NotNull final TargetPlaylist playlist,
                                                                     @NotNull final Pagination pagination,
                                                                     @NotNull final User user) {
        Mono<ResponseEntity<PlaylistItemsDto>> playlistItems = playlistItemsOperations.loadPlaylistItems(playlist, pagination)
                .map(playlistItemDtoConverter::toPlaylistItemDto)
                .collectList()
                .map(items -> defaultOkStatus(new PlaylistItemsDto(items)));

        return loader.loadPlaylist(playlist).switchIfEmpty(onPlaylistNotFoundError(playlist))
                .flatMap(playlist1 -> {
                    if ( playlist1.getPlaylistOwner().getId().equals(user.getId()) ) {
                        return playlistItems;
                    }

                    return Mono.error(
                            PlaylistOperationNotAllowedException.defaultException(playlist.getPlaylistId())
                    );
                });
    }


    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull TargetPlaylist targetPlaylist) {
        return Mono.defer(
                () -> Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()))
        );
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
