package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.factory.PlaylistItemEntityFactory;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.ReactiveContextUriParser;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public final class DefaultPlaylistItemsOperations implements PlaylistItemsOperations {
    private final PlaylistLoader playlistLoader;
    private final PlaylistItemsRepository itemsRepository;
    private final ReactiveContextUriParser contextUriParser;
    private final Logger logger = LoggerFactory.getLogger(DefaultPlaylistItemsOperations.class);
    private final PlaylistItemsService playlistItemsService;

    public DefaultPlaylistItemsOperations(final PlaylistLoader playlistLoader,
                                          final PlayableItemLoader playableItemLoader,
                                          final PlaylistItemsRepository itemsRepository,
                                          final ReactiveContextUriParser contextUriParser,
                                          final PlaylistItemEntityFactory playlistItemEntityFactory) {
        this.playlistLoader = playlistLoader;
        this.itemsRepository = itemsRepository;
        this.contextUriParser = contextUriParser;
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
                .flatMapMany(playlistSize ->
                        Flux.fromArray(addItemPayload.getUris())
                                .flatMap(contextUriParser::parse)
                                .index()
                                .flatMap(tuple -> {
                                    final Long currentIndex = tuple.getT1();
                                    final ContextUri contextUri = tuple.getT2();
                                    // we are appending items to the end
                                    if ( addItemPayload.getPosition().isEndOfPlaylist(playlistSize) ) {
                                        return appendItemToTheEnd(playlist, collaborator, playlistSize, currentIndex, contextUri);
                                    }

                                    return insertItemAtSpecificPosition(playlist, addItemPayload, collaborator, contextUri);
                                })
                );
    }

    @NotNull
    private Mono<Void> insertItemAtSpecificPosition(final @NotNull Playlist playlist, final @NotNull AddItemPayload addItemPayload, final @NotNull PlaylistCollaborator collaborator, final ContextUri contextUri) {
        final int position = addItemPayload.getPosition().value();

        return itemsRepository.incrementNextItemsPositionFrom(playlist.getId(), position)
                .then(saveItem(playlist.getId().value(), collaborator, contextUri, position));
    }

    @NotNull
    private Mono<Void> appendItemToTheEnd(@NotNull final Playlist playlist,
                                          @NotNull final PlaylistCollaborator collaborator,
                                          final long playlistSize,
                                          final long currentIndex,
                                          @NotNull final ContextUri contextUri) {
        final int position = (int) (playlistSize + currentIndex);
        logger.info("Saving the  track: {} at {} position", contextUri, position);
        return saveItem(playlist.getId().value(), collaborator, contextUri, position);
    }

    @NotNull
    private Mono<Void> saveItem(@NotNull String playlistId,
                                @NotNull PlaylistCollaborator collaborator,
                                @NotNull ContextUri contextUri,
                                int index) {

        final SimplePlaylistItem playlistItem = new SimplePlaylistItem(
                PlaylistId.of(playlistId),
                collaborator,
                contextUri,
                PlaylistItemPosition.at(index)
        );

        return playlistItemsService.saveItem(playlistItem);
    }

    @NotNull
    private Mono<Playlist> loadPlaylist(@NotNull TargetPlaylist targetPlaylist) {
        return playlistLoader.loadPlaylist(targetPlaylist)
                .switchIfEmpty(
                        onPlaylistNotFoundError(targetPlaylist)
                );
    }

    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull TargetPlaylist targetPlaylist) {
        return Mono.defer(
                () -> Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()))
        );
    }
}
