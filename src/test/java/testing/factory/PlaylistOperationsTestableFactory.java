package testing.factory;

import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import com.odeyalo.sonata.playlists.service.DefaultPlaylistOperations;
import com.odeyalo.sonata.playlists.service.PlaylistOperations;
import com.odeyalo.sonata.playlists.service.DefaultPlaylistService;
import com.odeyalo.sonata.playlists.service.upload.MockImageUploader;

import java.util.List;

public class PlaylistOperationsTestableFactory {

    public static PlaylistOperations withPlaylists(List<Playlist> playlists) {
        final InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository(playlists);
        return new DefaultPlaylistOperations(new DefaultPlaylistService(repository), new MockImageUploader());
    }

    public static DefaultPlaylistOperations create() {
        final InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository();
        return new DefaultPlaylistOperations(new DefaultPlaylistService(repository), new MockImageUploader());
    }
}
