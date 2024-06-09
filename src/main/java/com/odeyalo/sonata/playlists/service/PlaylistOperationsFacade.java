package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * Provide the same methods as {@link PlaylistOperations} but with access to current user represented as {@link User}
 */
public interface PlaylistOperationsFacade {
    /**
     * Search for entity by its id
     * @param playlistId - playlist to search
     * @param user - current user
     * @return - {@link Mono} emitting found {@link Playlist} or empty {@link Mono} if not found
     */
    @NotNull
    Mono<Playlist> findById(@NotNull TargetPlaylist playlistId, @NotNull User user);

    /**
     * Create the playlist with the given playlist owner
     * @param playlistInfo - info that used to create the playlist
     * @param playlistOwner - owner of the playlist(any user)
     * @param user - current user, can differ from {@link PlaylistOwner}
     * @return - created playlist
     */
    @NotNull
    Mono<Playlist> createPlaylist(@NotNull CreatePlaylistInfo playlistInfo,
                                  @NotNull PlaylistOwner playlistOwner,
                                  @NotNull User user);

    /**
     * Update the cover image of the playlist, removes the old one
     * @param targetPlaylist - playlist to update
     * @param file - image file
     * @param user - current user
     * @return - updated playlist
     */
    Mono<Playlist> updatePlaylistCoverImage(@NotNull TargetPlaylist targetPlaylist,
                                            @NotNull Mono<FilePart> file,
                                            @NotNull User user);

    /**
     * Update the given playlist with the given info
     * @param targetPlaylist - target playlist to update
     * @param updateInfo - info that used to update playlist
     * @param user - current user
     * @return - updated playlist
     */
    Mono<Playlist> updatePlaylistInfo(@NotNull TargetPlaylist targetPlaylist,
                                      @NotNull PartialPlaylistDetailsUpdateInfo updateInfo,
                                      @NotNull User user);
}