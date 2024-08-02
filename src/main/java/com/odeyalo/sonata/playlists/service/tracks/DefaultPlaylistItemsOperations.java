package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.factory.PlaylistItemEntityFactory;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
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
    private final PlaylistItemsRepository itemsRepository;
    private final PlaylistItemsService playlistItemsService;

    public DefaultPlaylistItemsOperations(final PlaylistLoader playlistLoader,
                                          final PlayableItemLoader playableItemLoader,
                                          final PlaylistItemsRepository itemsRepository,
                                          final PlaylistItemEntityFactory playlistItemEntityFactory) {
        this.playlistLoader = playlistLoader;
        this.itemsRepository = itemsRepository;
        this.playlistItemsService = new PlaylistItemsService(itemsRepository, playableItemLoader, playlistItemEntityFactory);
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
                .flatMapMany(it -> doAddPlaylistItems(it, addItemPayload, collaborator))
                .then();
    }

    @NotNull
    private Flux<Void> doAddPlaylistItems(@NotNull final Playlist playlist,
                                          @NotNull final AddItemPayload addItemPayload,
                                          @NotNull final PlaylistCollaborator collaborator) {
        return itemsRepository.getPlaylistSize(playlist.getId().value())
                .flatMapMany(playlistSize -> addPlaylistItems(playlist, addItemPayload, collaborator, playlistSize));
    }

    @NotNull
    private Flux<Void> addPlaylistItems(@NotNull final Playlist playlist,
                                        @NotNull final AddItemPayload addItemPayload,
                                        @NotNull final PlaylistCollaborator collaborator,
                                        final long playlistSize) {

        final AddItemPayload.Item[] items = addItemPayload.determineItemsPosition(playlistSize);

        return Flux.fromArray(items)
                .flatMap(item -> {
                    final ContextUri uri = item.contextUri();
                    final PlaylistItemPosition itemPosition = item.position();

                    if ( itemPosition.isEndOfPlaylist(playlistSize) ) {
                        return appendItemToTheEnd(playlist, collaborator, itemPosition, uri);
                    }

                    return insertItemAtSpecificPosition(playlist.getId(), itemPosition, collaborator, uri);
                });
    }


    @NotNull
    private Mono<Playlist> loadPlaylist(@NotNull TargetPlaylist targetPlaylist) {
        return playlistLoader.loadPlaylist(targetPlaylist)
                .switchIfEmpty(
                        onPlaylistNotFoundError(targetPlaylist)
                );
    }

    @NotNull
    private Mono<Void> insertItemAtSpecificPosition(@NotNull final PlaylistId playlistId,
                                                    @NotNull final PlaylistItemPosition playlistItemPosition,
                                                    @NotNull final PlaylistCollaborator collaborator,
                                                    @NotNull final ContextUri contextUri) {
        final int position = playlistItemPosition.asInt();

        return itemsRepository.incrementNextItemsPositionFrom(playlistId, position)
                .then(saveItem(playlistId, collaborator, contextUri, playlistItemPosition));
    }

    @NotNull
    private Mono<Void> appendItemToTheEnd(@NotNull final Playlist playlist,
                                          @NotNull final PlaylistCollaborator collaborator,
                                          @NotNull final PlaylistItemPosition position,
                                          @NotNull final ContextUri contextUri) {
        return saveItem(playlist.getId(), collaborator, contextUri, position);
    }

    @NotNull
    private Mono<Void> saveItem(@NotNull PlaylistId playlistId,
                                @NotNull PlaylistCollaborator collaborator,
                                @NotNull ContextUri contextUri,
                                @NotNull PlaylistItemPosition position) {

        final SimplePlaylistItem playlistItem = new SimplePlaylistItem(
                playlistId,
                collaborator,
                contextUri,
                position
        );

        return playlistItemsService.saveItem(playlistItem);
    }

    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull TargetPlaylist targetPlaylist) {
        return Mono.defer(
                () -> Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()))
        );
    }
}
