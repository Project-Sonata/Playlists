package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.model.PlaylistId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Simple repository to work with items saved in playlist
 */
public interface PlaylistItemsRepository {
    /**
     * Search for the all items in playlist with pagination support
     * @param playlistId - id of the public. NOTE: ID should be public ID that shown for user! Not the internal one
     * @param pageable - pagination info
     * @return - {@link  Flux} with {@link PlaylistItemEntity} with items from this playlist, with pagination support(if was used)
     */
    @NotNull
    Flux<PlaylistItemEntity> findAllByPlaylistId(@NotNull String playlistId,
                                                 @NotNull Pageable pageable);

    @NotNull
    Mono<PlaylistItemEntity> save(@NotNull PlaylistItemEntity entity);

    @NotNull
    Mono<Void> clear();

    @NotNull
    Mono<Long> getPlaylistSize(@NotNull String playlistId);

    /**
     * Increment the next playlist items index by one.
     * This method exists for performance reasons, to invoke a single UPDATE(for sql) instead of N+1 problem
     * @param id - the playlist ID to update
     * @param position - a position(exclusive) to increment items from
     * @return - a empty {@link Mono} on successful completion
     */
    @NotNull
    Mono<Void> incrementNextItemsPositionFrom(@NotNull PlaylistId id, int position);
}
