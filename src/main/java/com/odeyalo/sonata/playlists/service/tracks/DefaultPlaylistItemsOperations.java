package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

public final class DefaultPlaylistItemsOperations implements PlaylistItemsOperations {
    private final PlaylistLoader playlistLoader;

    public DefaultPlaylistItemsOperations(PlaylistLoader playlistLoader) {
        this.playlistLoader = playlistLoader;
    }

    @Override
    @NotNull
    public Mono<List<PlaylistItem>> loadPlaylistItems(@NotNull TargetPlaylist targetPlaylist) {
        return playlistLoader.loadPlaylist(targetPlaylist)
                .switchIfEmpty(
                        Mono.defer(
                                () -> Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()))
                        )
                )
                .thenReturn(Collections.emptyList());
    }
}
