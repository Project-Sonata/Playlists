package com.odeyalo.sonata.playlists.repository.r2dbc.delegate;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface R2dbcPlaylistItemsRepositoryDelegate extends R2dbcRepository<PlaylistItemEntity, Long> {
    /**
     * Search for the all items in playlist with pagination support
     * @param playlistId - id of the public. NOTE: ID should be public ID that shown for user! Not the internal one
     * @param pageable - pagination info
     * @return - {@link  Flux} with {@link PlaylistItemEntity} with items from this playlist, with pagination support(if was used)
     */
    Flux<PlaylistItemEntity> findAllByPlaylistId(@NotNull String playlistId, @NotNull Pageable pageable);

}
