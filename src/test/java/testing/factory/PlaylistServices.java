package testing.factory;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.service.InMemoryPlaylistService;
import com.odeyalo.sonata.playlists.service.PlaylistService;

import java.util.List;

public final class PlaylistServices {

    public static PlaylistService withPlaylists(final Playlist... playlists) {
        return withPlaylists(List.of(playlists));
    }

    public static PlaylistService withPlaylists(final List<Playlist> playlists) {
        return new InMemoryPlaylistService(playlists);
    }

    public static PlaylistService empty() {
        return withPlaylists();
    }
}
