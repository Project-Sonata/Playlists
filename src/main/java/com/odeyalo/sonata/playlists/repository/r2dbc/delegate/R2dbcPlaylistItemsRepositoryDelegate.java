package com.odeyalo.sonata.playlists.repository.r2dbc.delegate;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
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

    /**
     * Increment the next playlist items index by one
     * @param playlistId - a playlist ID to update items from
     * @param start - a start position, exclusive
     * @return - empty {@link Mono} on completion
     */
    @Query("UPDATE playlist_items SET index = index + 1 WHERE index > :start AND playlist_id= :playlistId")
    Mono<Void> incrementNextPlaylistItems(@NotNull String playlistId, int start);

}
