package testing;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import org.springframework.http.client.MultipartBodyBuilder;

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

    /**
     * Upload the cover image for the playlist
     * @param authorizationHeader - access token
     * @param playlistId - playlist to add image to
     * @param builder - builder with images
     */
    void addCoverImage(String authorizationHeader, String playlistId, MultipartBodyBuilder builder);
}
