package com.odeyalo.sonata.playlists.repository.r2dbc.delegate;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface R2dbcPlaylistItemsRepositoryDelegate extends R2dbcRepository<PlaylistItemEntity, Long> {
    /**
     * Search for the all items in playlist with pagination support
     *
     * @param playlistId - id of the public. NOTE: ID should be public ID that shown for user! Not the internal one
     * @param pageable   - pagination info
     * @return - {@link  Flux} with {@link PlaylistItemEntity} with items from this playlist, with pagination support(if was used)
     */
    @NotNull
    Flux<PlaylistItemEntity> findAllByPlaylistId(@NotNull String playlistId, @NotNull Pageable pageable);

    /**
     * Retrieves the count of items associated with a specific playlist.
     * <p>
     * NOTE: possibly leads for slow performance because of COUNT(*) query.
     * Maybe should be changed for something better
     *
     * @param playlistId The ID of the playlist to retrieve the count for.
     * @return A {@link Mono} emitting the count of items associated with the specified playlist.
     */
    @NotNull
    Mono<Long> countAllByPlaylistId(@NotNull String playlistId);

}
