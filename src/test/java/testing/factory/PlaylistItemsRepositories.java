package testing.factory;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistItemsRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;

import java.util.List;
import java.util.Set;

public final class PlaylistItemsRepositories {

    public static PlaylistItemsRepository withItems(PlaylistItemEntity... tracks) {
        return new InMemoryPlaylistItemsRepository(List.of(tracks));
    }

    public static PlaylistItemsRepository empty() {
        return new InMemoryPlaylistItemsRepository();
    }

    public static PlaylistItemsRepository withPlaylistIds(Set<String> playlistIds) {
        return new InMemoryPlaylistItemsRepository(playlistIds);
    }
}
