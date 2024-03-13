package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.common.context.ContextUriParser;
import com.odeyalo.sonata.common.context.MalformedContextUriException;
import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.pagination.OffsetBasedPageRequest;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AllArgsConstructor
public final class DefaultPlaylistItemsOperations implements PlaylistItemsOperations {
    private final PlaylistLoader playlistLoader;
    private final PlayableItemLoader playableItemLoader;
    private final PlaylistItemsRepository itemsRepository;
    private final ContextUriParser contextUriParser;
    private Clock clock = new JavaClock();

    @Override
    @NotNull
    public Flux<PlaylistItem> loadPlaylistItems(@NotNull TargetPlaylist targetPlaylist, @NotNull Pagination pagination) {
        return isPlaylistExist(targetPlaylist)
                .flatMapMany(playlist -> getPlaylistItems(targetPlaylist, pagination))
                .flatMap(this::loadPlaylistItem);
    }

    @Override
    @NotNull
    public Mono<Void> addItems(@NotNull Playlist existingPlaylist,
                               @NotNull AddItemPayload addItemPayload,
                               @NotNull PlaylistCollaborator collaborator) {

        String firstContextUriStr = addItemPayload.getUris()[0];

        return tryParse(firstContextUriStr)
                .flatMap(contextUri -> {
                    ItemEntity item = ItemEntity.builder()
                            .publicId(contextUri.getEntityId())
                            .contextUri(firstContextUriStr)
                            .build();

                    PlaylistItemEntity playlistItemEntity = PlaylistItemEntity.builder()
                            .playlistId(existingPlaylist.getId())
                            .addedAt(clock.now())
                            .item(item)
                            .addedBy(PlaylistCollaboratorEntity.builder()
                                    .id("123")
                                    .displayName(collaborator.getDisplayName())
                                    .type(EntityType.USER)
                                    .build())
                            .build();

                    return itemsRepository.save(playlistItemEntity);
                })
                .then();
    }

    private Mono<ContextUri> tryParse(String contextUriStr) {
        try {
            ContextUri contextUri = contextUriParser.parse(contextUriStr);
            return Mono.just(contextUri);
        } catch (MalformedContextUriException e) {
            return Mono.error(e);
        }
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
                .id(addedBy.getId())
                .displayName(addedBy.getDisplayName())
                .type(addedBy.getType())
                .contextUri(addedBy.getContextUri())
                .build();

        return PlaylistItem.builder()
                .addedAt(playlistItemEntity.getAddedAt())
                .addedBy(collaborator)
                .item(item)
                .build();

    }
}
