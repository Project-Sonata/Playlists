package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

/**
 * Simple repository to work with items saved in playlist
 */
public interface PlaylistItemsRepository {

    @NotNull
    Flux<PlaylistItemEntity> findAllByPlaylistId(@NotNull Long playlistId,
                                                 @NotNull Pageable pageable);

}
