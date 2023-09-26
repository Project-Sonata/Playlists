package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * Operations that can be performed with Playlist
 */
public interface PlaylistOperations {
    /**
     * Search for entity by its id
     * @param id - entity id
     * @return - found playlist or empty mono
     */
    Mono<Playlist> findById(String id);

    /**
     * Create the playlist with the given playlist owner
     * @param playlistInfo - info that used to create the playlist
     * @param playlistOwner - owner of the playlist(any user)
     * @return - created playlist
     */
    Mono<Playlist> createPlaylist(CreatePlaylistInfo playlistInfo, PlaylistOwner playlistOwner);

    /**
     * Update the cover image of the playlist, removes the old one
     * @param targetPlaylist - playlist to update
     * @param file - image file
     * @return - updated playlist
     */
    Mono<Playlist> updatePlaylistCoverImage(TargetPlaylist targetPlaylist, Mono<FilePart> file);

    /**
     * Update the given playlist with the given info
     * @param targetPlaylist - target playlist to update
     * @param updateInfo - info that used to update playlist
     * @return - updated playlist
     */
    Mono<Playlist> updatePlaylistInfo(TargetPlaylist targetPlaylist, PartialPlaylistDetailsUpdateInfo updateInfo);
}