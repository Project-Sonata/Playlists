package testing;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;

/**
 * Interface that provide ALL http operations for Playlist
 */
public interface SonataPlaylistHttpTestClient {
    /**
     * Fetch playlist by ID
     */
    PlaylistDto fetchPlaylist(String authorizationHeader, String playlistId);

    /**
     * Create playlist and return it
     */
    PlaylistDto createPlaylist(String authorizationHeader, String userId, CreatePlaylistRequest body);
}
