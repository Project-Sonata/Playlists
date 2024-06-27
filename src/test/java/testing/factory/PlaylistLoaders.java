package testing.factory;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;

/**
 * Factory methods for {@link PlaylistLoader}
 */
public final class PlaylistLoaders {

    public static PlaylistLoader empty() {
        return withPlaylists();
    }

    public static PlaylistLoader withPlaylists(Playlist... playlists) {
        return PlaylistServices.withPlaylists(playlists);
    }
}
