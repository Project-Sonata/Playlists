package testing.factory;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.DefaultPlaylistService;

/**
 * Factory methods for {@link PlaylistLoader}
 */
public final class PlaylistLoaders {

    public static PlaylistLoader empty() {
        return withPlaylists();
    }

    public static PlaylistLoader withPlaylists(Playlist... playlists) {
        return new DefaultPlaylistService(
                new InMemoryPlaylistRepository(playlists)
        );
    }
}
