package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.exception.PlaylistOperationNotAllowedException;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Allows to add security policy rules
 */
@Component
public final class SecurityPolicyPlaylistOperationsFacade implements PlaylistOperationsFacade {
    private final PlaylistOperations delegate;

    public SecurityPolicyPlaylistOperationsFacade(final PlaylistOperations playlistOperations) {
        this.delegate = playlistOperations;
    }

    @Override
    @NotNull
    public Mono<Playlist> findById(@NotNull final TargetPlaylist playlistId,
                                   @NotNull final User user) {

        return loadPlaylist(playlistId)
                .flatMap(playlist -> {
                    if ( playlist.isReadPermissionGrantedFor(user) ) {
                        return Mono.just(playlist);
                    }
                    return notAllowedPlaylistOperationException(playlistId);
                });
    }

    @Override
    @NotNull
    public Mono<Playlist> createPlaylist(@NotNull final CreatePlaylistInfo playlistInfo,
                                         @NotNull final PlaylistOwner playlistOwner,
                                         @NotNull final User user) {
        return delegate.createPlaylist(playlistInfo, playlistOwner);
    }

    @Override
    public Mono<Playlist> updatePlaylistCoverImage(@NotNull final TargetPlaylist targetPlaylist,
                                                   @NotNull final Mono<FilePart> file,
                                                   @NotNull final User user) {

        return loadPlaylist(targetPlaylist)
                .flatMap(playlist -> {
                    if ( playlist.isWritePermissionGrantedFor(user) ) {
                        return delegate.updatePlaylistCoverImage(targetPlaylist, file);
                    }
                    return notAllowedPlaylistOperationException(targetPlaylist);
                });
    }

    @Override
    public Mono<Playlist> updatePlaylistInfo(@NotNull final TargetPlaylist targetPlaylist,
                                             @NotNull final PartialPlaylistDetailsUpdateInfo updateInfo,
                                             @NotNull final User user) {
        return loadPlaylist(targetPlaylist)
                .flatMap(playlist -> {
                    if ( playlist.isWritePermissionGrantedFor(user) ) {
                        return delegate.updatePlaylistInfo(targetPlaylist, updateInfo);
                    }
                    return notAllowedPlaylistOperationException(targetPlaylist);
                });
    }

    @NotNull
    private Mono<Playlist> loadPlaylist(@NotNull final TargetPlaylist playlistId) {
        return delegate.findById(playlistId.getPlaylistId())
                .switchIfEmpty(onPlaylistNotFoundError(playlistId));
    }

    @NotNull
    private static Mono<Playlist> onPlaylistNotFoundError(@NotNull final TargetPlaylist targetPlaylist) {
        return Mono.defer(
                () -> Mono.error(PlaylistNotFoundException.defaultException(targetPlaylist.getPlaylistId()))
        );
    }

    @NotNull
    private static <T> Mono<T> notAllowedPlaylistOperationException(@NotNull final TargetPlaylist playlist) {
        return Mono.defer(() -> Mono.error(
                PlaylistOperationNotAllowedException.defaultException(playlist.getPlaylistId())
        ));
    }
}
