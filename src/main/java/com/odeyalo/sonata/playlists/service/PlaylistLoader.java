package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

public interface PlaylistLoader {
    /**
     * Load playlist from some kind of store
     * @param targetPlaylist - playlist to load
     * @return {@link Mono} with {@link Playlist} with found playlist, or
     * empty {@link Mono} if playlist with this ID does not exist
     */
    @NotNull
    Mono<Playlist> loadPlaylist(@NotNull final TargetPlaylist targetPlaylist);

}
