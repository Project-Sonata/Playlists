package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Operations to work with playlist item
 */
public interface PlaylistItemsOperations {

    @NotNull
    Flux<PlaylistItem> loadPlaylistItems(@NotNull final TargetPlaylist targetPlaylist,
                                         @NotNull final Pagination pagination);

    @NotNull
    Mono<Void> addItems(@NotNull final Playlist existingPlaylist,
                        @NotNull final AddItemPayload addItemPayload, @NotNull PlaylistCollaborator collaborator);
}
