package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.ReactiveContextUriParser;
import com.odeyalo.sonata.playlists.support.converter.PlaylistItemEntityConverter;
import com.odeyalo.sonata.playlists.support.pagination.OffsetBasedPageRequest;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

    @Override
    @NotNull
    public Flux<PlaylistItem> loadPlaylistItems(@NotNull TargetPlaylist targetPlaylist, @NotNull Pagination pagination) {
        return isPlaylistExist(targetPlaylist)
                .flatMapMany(playlist -> getPlaylistItems(targetPlaylist, pagination))
                .flatMap(this::loadPlaylistItem);
    }

    @NotNull
    public Mono<Void> addItems(@NotNull TargetPlaylist targetPlaylist,
                               @NotNull AddItemPayload addItemPayload,
                               @NotNull PlaylistCollaborator collaborator) {

        return isPlaylistExist(targetPlaylist)
                .flatMapMany(it -> doAddPlaylistItems(it, addItemPayload, collaborator))
                .then();
    }

    @NotNull
    private Flux<PlaylistItemEntity> doAddPlaylistItems(@NotNull Playlist playlist, @NotNull AddItemPayload addItemPayload, @NotNull PlaylistCollaborator collaborator) {
        return itemsRepository.getPlaylistSize(playlist.getId())
                .flatMapMany(playlistSize ->
                        Flux.fromArray(addItemPayload.getUris())
                                .flatMap(contextUriParser::parse)
                                .index()
                                .flatMap(tuple -> {
                                    Long index = tuple.getT1();
                                    ContextUri contextUri = tuple.getT2();
                                    return saveItem(playlist.getId(), collaborator, contextUri, (int) (playlistSize + index));
                                })
                );
    }

    @NotNull
    private Mono<PlaylistItemEntity> saveItem(@NotNull String playlistId, @NotNull PlaylistCollaborator collaborator,
                                              ContextUri contextUri,
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
    private Mono<Playlist> isPlaylistExist(@NotNull TargetPlaylist targetPlaylist) {
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
