package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
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
    public Flux<PlaylistItem> loadPlaylistItems(@NotNull TargetPlaylist targetPlaylist, @NotNull Pagination pagination) {
        return loadPlaylist(targetPlaylist)
                .flatMapMany(playlist -> playlistItemsService.loadPlaylistItems(targetPlaylist, pagination));
    }

    @NotNull
    public Mono<Void> addItems(@NotNull TargetPlaylist targetPlaylist,
                               @NotNull AddItemPayload addItemPayload,
                               @NotNull PlaylistCollaborator collaborator) {

        return loadPlaylist(targetPlaylist)
                .flatMapMany(playlist -> doAddPlaylistItems(playlist, addItemPayload, collaborator))
                .then();
    }

    @NotNull
    private Flux<Void> doAddPlaylistItems(@NotNull final Playlist playlist,
                                          @NotNull final AddItemPayload addItemPayload,
                                          @NotNull final PlaylistCollaborator collaborator) {
        return playlistItemsService.getPlaylistSize(playlist.getId())
                .flatMapMany(playlistSize -> addPlaylistItems(playlist, addItemPayload, collaborator, playlistSize));
    }

    @NotNull
    private Flux<Void> addPlaylistItems(@NotNull final Playlist playlist,
                                        @NotNull final AddItemPayload addItemPayload,
                                        @NotNull final PlaylistCollaborator collaborator,
                                        final long playlistSize) {

        final AddItemPayload.Item[] items = addItemPayload.determineItemsPosition(playlistSize);

        return Flux.fromArray(items)
                .flatMap(item -> saveItem(playlist, collaborator, item, playlistSize));
    }

    @NotNull
    private Mono<Void> saveItem(@NotNull final Playlist playlist,
                                @NotNull final PlaylistCollaborator collaborator,
                                @NotNull final AddItemPayload.Item item,
                                final long playlistSize) {

        final ContextUri itemUri = item.contextUri();
        final PlaylistItemPosition itemPosition = item.position();

        final SimplePlaylistItem playlistItem = new SimplePlaylistItem(
                playlist.getId(),
                collaborator,
                itemUri,
                itemPosition
        );

        if ( itemPosition.isEndOfPlaylist(playlistSize) ) {
            return appendItemToTheEnd(playlistItem);
        }

        return insertItemAtSpecificPosition(playlistItem);
    }

    @NotNull
    private Mono<Playlist> loadPlaylist(@NotNull TargetPlaylist targetPlaylist) {
        return playlistLoader.loadPlaylist(targetPlaylist)
                .switchIfEmpty(Mono.defer(() -> onPlaylistNotFoundError(targetPlaylist)));
    }

    @NotNull
    private Mono<Void> insertItemAtSpecificPosition(@NotNull final SimplePlaylistItem playlistItem) {
        return playlistItemsService.insertItemAtSpecificPosition(playlistItem);
    }

    @NotNull
    public Mono<Void> appendItemToTheEnd(@NotNull final SimplePlaylistItem playlistItem) {
        return playlistItemsService.appendItemToTheEnd(playlistItem);
    }

    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull final TargetPlaylist targetPlaylist) {
        return Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()));

    }
}
