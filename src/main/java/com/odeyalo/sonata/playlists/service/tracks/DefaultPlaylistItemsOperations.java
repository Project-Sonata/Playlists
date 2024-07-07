package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.ReactiveContextUriParser;
import com.odeyalo.sonata.playlists.support.converter.PlaylistItemEntityConverter;
import com.odeyalo.sonata.playlists.support.pagination.OffsetBasedPageRequest;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class DefaultPlaylistItemsOperations implements PlaylistItemsOperations {
    private final PlaylistLoader playlistLoader;
    private final PlayableItemLoader playableItemLoader;
    private final PlaylistItemsRepository itemsRepository;
    private final ReactiveContextUriParser contextUriParser;
    private final PlaylistItemEntityConverter playlistItemEntityConverter;
    private final Logger logger = LoggerFactory.getLogger(DefaultPlaylistItemsOperations.class);

    @Override
    @NotNull
    public Flux<PlaylistItem> loadPlaylistItems(@NotNull TargetPlaylist targetPlaylist, @NotNull Pagination pagination) {
        return loadPlaylist(targetPlaylist)
                .flatMapMany(playlist -> getPlaylistItems(targetPlaylist, pagination))
                .flatMap(this::loadPlaylistItem);
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
    private Flux<PlaylistItemEntity> doAddPlaylistItems(@NotNull final Playlist playlist,
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
    private Mono<PlaylistItemEntity> insertItemAtSpecificPosition(final @NotNull Playlist playlist, final @NotNull AddItemPayload addItemPayload, final @NotNull PlaylistCollaborator collaborator, final ContextUri contextUri) {
        final int position = addItemPayload.getPosition().value();

        return itemsRepository.incrementNextItemsPositionFrom(playlist.getId(), position)
                .then(saveItem(playlist.getId().value(), collaborator, contextUri, position));
    }

    @NotNull
    private Mono<PlaylistItemEntity> appendItemToTheEnd(final @NotNull Playlist playlist, final @NotNull PlaylistCollaborator collaborator, final Long playlistSize, final Long currentIndex, final ContextUri contextUri) {
        final int position = (int) (playlistSize + currentIndex);
        logger.info("Saving the  track: {} at {} position", contextUri, position);
        return saveItem(playlist.getId().value(), collaborator, contextUri, position);
    }

    @NotNull
    private Mono<PlaylistItemEntity> saveItem(@NotNull String playlistId,
                                              @NotNull PlaylistCollaborator collaborator,
                                              @NotNull ContextUri contextUri,
                                              int index) {
        PlaylistItemEntity playlistItemEntity = createPlaylistItemEntity(playlistId, collaborator, contextUri);
        playlistItemEntity.setIndex(index);
        return itemsRepository.save(playlistItemEntity);
    }

    private PlaylistItemEntity createPlaylistItemEntity(@NotNull String playlistId,
                                                        @NotNull PlaylistCollaborator collaborator,
                                                        @NotNull ContextUri contextUri) {
        return playlistItemEntityConverter.createPlaylistItemEntity(playlistId, collaborator, contextUri);
    }


    @NotNull
    private Mono<Playlist> loadPlaylist(@NotNull TargetPlaylist targetPlaylist) {
        return playlistLoader.loadPlaylist(targetPlaylist)
                .switchIfEmpty(
                        onPlaylistNotFoundError(targetPlaylist)
                );
    }

    @NotNull
    private Flux<PlaylistItemEntity> getPlaylistItems(@NotNull TargetPlaylist targetPlaylist,
                                                      @NotNull Pagination pagination) {
        return itemsRepository.findAllByPlaylistId(targetPlaylist.getPlaylistId(),
                OffsetBasedPageRequest.of(pagination.getOffset(), pagination.getLimit())
        );
    }

    @NotNull
    private Mono<PlaylistItem> loadPlaylistItem(PlaylistItemEntity playlistItemEntity) {
        return playableItemLoader.loadItem(playlistItemEntity.getItem().getContextUri())
                .map(item -> convertToPlaylistItem(playlistItemEntity, item));
    }

    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull TargetPlaylist targetPlaylist) {
        return Mono.defer(
                () -> Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()))
        );
    }

    @NotNull
    private static PlaylistItem convertToPlaylistItem(@NotNull PlaylistItemEntity playlistItemEntity,
                                                      @NotNull PlayableItem item) {

        PlaylistCollaboratorEntity addedBy = playlistItemEntity.getAddedBy();

        PlaylistCollaborator collaborator = PlaylistCollaborator.builder()
                .id(addedBy.getPublicId())
                .displayName(addedBy.getDisplayName())
                .type(addedBy.getType())
                .contextUri(addedBy.getContextUri())
                .build();

        return PlaylistItem.builder()
                .addedAt(playlistItemEntity.getAddedAt())
                .addedBy(collaborator)
                .item(item)
                .index(playlistItemEntity.getIndex())
                .build();

    }
}
