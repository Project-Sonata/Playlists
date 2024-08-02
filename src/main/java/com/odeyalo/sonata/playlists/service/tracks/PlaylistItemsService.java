package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.entity.factory.PlaylistItemEntityFactory;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.pagination.OffsetBasedPageRequest;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Used to work with playlist items, middleware between repository but returns a domain models instead of entities
 */
public final class PlaylistItemsService {
    private final PlaylistItemsRepository itemsRepository;
    private final PlayableItemLoader playableItemLoader;
    private final PlaylistItemEntityFactory playlistItemEntityFactory;

    public PlaylistItemsService(final PlaylistItemsRepository itemsRepository,
                                final PlayableItemLoader playableItemLoader,
                                final PlaylistItemEntityFactory playlistItemEntityFactory) {
        this.itemsRepository = itemsRepository;
        this.playableItemLoader = playableItemLoader;
        this.playlistItemEntityFactory = playlistItemEntityFactory;
    }

    @NotNull
    public Flux<PlaylistItem> loadPlaylistItems(@NotNull final TargetPlaylist targetPlaylist,
                                                @NotNull final Pagination pagination) {
        return getPlaylistItems(targetPlaylist, pagination)
                .flatMap(this::loadPlaylistItem);
    }

    @NotNull
    public Mono<Void> saveItem(@NotNull final SimplePlaylistItem playlistItem) {

        final PlaylistItemEntity playlistItemEntity = playlistItemEntityFactory.create(playlistItem);

        return itemsRepository.save(playlistItemEntity)
                .then();
    }

    @NotNull
    private Flux<PlaylistItemEntity> getPlaylistItems(@NotNull final TargetPlaylist targetPlaylist,
                                                      @NotNull final Pagination pagination) {
        return itemsRepository.findAllByPlaylistId(targetPlaylist.getPlaylistId(),
                new OffsetBasedPageRequest(pagination.getOffset(), pagination.getLimit(), Sort.by("index"))
        );
    }

    @NotNull
    private Mono<PlaylistItem> loadPlaylistItem(@NotNull final PlaylistItemEntity playlistItemEntity) {
        return playableItemLoader.loadItem(playlistItemEntity.getItem().getContextUri())
                .map(item -> convertToPlaylistItem(playlistItemEntity, item));
    }

    @NotNull
    private static PlaylistItem convertToPlaylistItem(@NotNull final PlaylistItemEntity playlistItemEntity,
                                                      @NotNull final PlayableItem item) {

        final PlaylistCollaboratorEntity addedBy = playlistItemEntity.getAddedBy();

        final PlaylistCollaborator collaborator = PlaylistCollaborator.builder()
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
