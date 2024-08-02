package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public final class DefaultPlaylistItemsOperations implements PlaylistItemsOperations {
    private final PlaylistLoader playlistLoader;
    private final PlaylistItemsService playlistItemsService;

    public DefaultPlaylistItemsOperations(final PlaylistLoader playlistLoader,
                                          final PlaylistItemsService playlistItemsService) {
        this.playlistLoader = playlistLoader;
        this.playlistItemsService = playlistItemsService;
    }

    @Override
    @NotNull
    public Flux<PlaylistItem> loadPlaylistItems(@NotNull final TargetPlaylist targetPlaylist,
                                                @NotNull final Pagination pagination) {
        return loadPlaylist(targetPlaylist)
                .flatMapMany(playlist -> playlistItemsService.loadPlaylistItems(targetPlaylist, pagination));
    }

    @NotNull
    public Mono<Void> addItems(@NotNull final TargetPlaylist targetPlaylist,
                               @NotNull final AddItemPayload addItemPayload,
                               @NotNull final PlaylistCollaborator collaborator) {

        return loadPlaylist(targetPlaylist)
                .flatMapMany(playlist -> addPlaylistItems(playlist, addItemPayload, collaborator))
                .then();
    }

    @NotNull
    private Mono<Playlist> loadPlaylist(@NotNull final TargetPlaylist targetPlaylist) {
        return playlistLoader.loadPlaylist(targetPlaylist)
                .switchIfEmpty(Mono.defer(() -> onPlaylistNotFoundError(targetPlaylist)));
    }

    @NotNull
    private Flux<Void> addPlaylistItems(@NotNull final Playlist playlist,
                                        @NotNull final AddItemPayload addItemPayload,
                                        @NotNull final PlaylistCollaborator collaborator) {

        return playlistItemsService.getPlaylistSize(playlist.getId())
                .flatMapMany(playlistSize -> doAddPlaylistItems(playlist, addItemPayload, collaborator, playlistSize));
    }

    @NotNull
    private Flux<Void> doAddPlaylistItems(@NotNull final Playlist playlist,
                                          @NotNull final AddItemPayload addItemPayload,
                                          @NotNull final PlaylistCollaborator collaborator,
                                          final long playlistSize) {

        final AddItemPayload.Item[] items = addItemPayload.determineItemsPosition(playlistSize);

        return Flux.fromArray(items)
                .map(item -> createSimplePlaylistItem(playlist, collaborator, item))
                .flatMap(item -> saveItem(item, playlistSize));
    }

    @NotNull
    private static SimplePlaylistItem createSimplePlaylistItem(@NotNull final Playlist playlist,
                                                               @NotNull final PlaylistCollaborator collaborator,
                                                               @NotNull final AddItemPayload.Item item) {
        return new SimplePlaylistItem(
                playlist.getId(),
                collaborator,
                item.contextUri(),
                item.position()
        );
    }

    @NotNull
    private Mono<Void> saveItem(@NotNull final SimplePlaylistItem playlistItem,
                                final long playlistSize) {

        final PlaylistItemPosition itemPosition = playlistItem.getPosition();

        if ( itemPosition.isEndOfPlaylist(playlistSize) ) {
            return appendItemToTheEnd(playlistItem);
        }

        return insertItemAtSpecificPosition(playlistItem);
    }

    @NotNull
    public Mono<Void> appendItemToTheEnd(@NotNull final SimplePlaylistItem playlistItem) {
        return playlistItemsService.appendItemToTheEnd(playlistItem);
    }

    @NotNull
    private Mono<Void> insertItemAtSpecificPosition(@NotNull final SimplePlaylistItem playlistItem) {
        return playlistItemsService.insertItemAtSpecificPosition(playlistItem);
    }

    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull final TargetPlaylist targetPlaylist) {
        return Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()));
    }
}
