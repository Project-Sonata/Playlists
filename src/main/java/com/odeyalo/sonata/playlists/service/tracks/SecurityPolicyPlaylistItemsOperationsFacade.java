package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.exception.PlaylistOperationNotAllowedException;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Simple facade that used to add security rules to {@link PlaylistItemsOperations}
 */
@Component
public final class SecurityPolicyPlaylistItemsOperationsFacade implements PlaylistItemsOperationsFacade {
    private final PlaylistItemsOperations delegate;
    private final PlaylistLoader playlistLoader;

    public SecurityPolicyPlaylistItemsOperationsFacade(final PlaylistItemsOperations delegate,
                                                       final PlaylistLoader playlistLoader) {
        this.delegate = delegate;
        this.playlistLoader = playlistLoader;
    }

    @Override
    @NotNull
    public Flux<PlaylistItem> loadPlaylistItems(@NotNull final TargetPlaylist targetPlaylist,
                                                @NotNull final Pagination pagination,
                                                @NotNull final User user) {

        return loadPlaylist(targetPlaylist)
                .flatMapMany(playlist -> {
                    if ( playlist.isReadPermissionGrantedFor(user) ) {
                        return delegate.loadPlaylistItems(targetPlaylist, pagination);
                    }
                    return notAllowedPlaylistOperationException(targetPlaylist);
                });
    }

    @Override
    @NotNull
    public Mono<Void> addItems(@NotNull final TargetPlaylist targetPlaylist,
                               @NotNull final AddItemPayload addItemPayload,
                               @NotNull final PlaylistCollaborator collaborator,
                               @NotNull final User user) {

        return loadPlaylist(targetPlaylist)
                .flatMap(playlist -> {
                    if ( playlist.isWritePermissionGrantedFor(user) ) {
                        return delegate.addItems(targetPlaylist, addItemPayload, collaborator);
                    }

                    return notAllowedPlaylistOperationException(targetPlaylist);
                });
    }

    @NotNull
    private Mono<Playlist> loadPlaylist(@NotNull final TargetPlaylist targetPlaylist) {
        return playlistLoader.loadPlaylist(targetPlaylist)
                .switchIfEmpty(onPlaylistNotFoundError(targetPlaylist));
    }

    @NotNull
    private static <T> Mono<T> notAllowedPlaylistOperationException(@NotNull final TargetPlaylist playlist) {
        return Mono.defer(() -> Mono.error(
                PlaylistOperationNotAllowedException.defaultException(playlist.getPlaylistId())
        ));
    }

    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull final TargetPlaylist targetPlaylist) {
        return Mono.defer(
                () -> Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()))
        );
    }
}
