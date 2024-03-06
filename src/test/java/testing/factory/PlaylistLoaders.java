package testing.factory;

import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.RepositoryDelegatePlaylistLoader;

/**
 * Factory methods for {@link PlaylistLoader}
 */
public final class PlaylistLoaders {

    public static PlaylistLoader empty() {
        return new RepositoryDelegatePlaylistLoader(
                new InMemoryPlaylistRepository()
        );
    }
}
