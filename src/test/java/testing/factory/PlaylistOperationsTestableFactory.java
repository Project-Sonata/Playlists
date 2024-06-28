package testing.factory;

import com.odeyalo.sonata.playlists.config.factory.FactoryConfiguration;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistRepository;
import com.odeyalo.sonata.playlists.service.DefaultPlaylistOperations;
import com.odeyalo.sonata.playlists.service.PlaylistOperations;
import com.odeyalo.sonata.playlists.service.DefaultPlaylistService;
import com.odeyalo.sonata.playlists.service.upload.MockImageUploader;
import com.odeyalo.sonata.playlists.support.converter.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaylistOperationsTestableFactory {

    public static PlaylistOperations withPlaylists(List<Playlist> playlists) {
        return new DefaultPlaylistOperations(PlaylistServices.withPlaylists(playlists), new MockImageUploader());
    }

    public static DefaultPlaylistOperations create() {
        final InMemoryPlaylistRepository repository = new InMemoryPlaylistRepository();
        return new DefaultPlaylistOperations(new DefaultPlaylistService(repository, createPlaylistConverter(), new Playlist.Factory(), new FactoryConfiguration().playlistEntityFactory()), new MockImageUploader());
    }

    @NotNull
    private static PlaylistConverter createPlaylistConverter() {
        ImagesEntityConverterImpl imagesEntityConverter = new ImagesEntityConverterImpl();
        imagesEntityConverter.setImageConverter(new ImageEntityConverterImpl());
        return new PlaylistConverterImpl(imagesEntityConverter, new PlaylistOwnerConverterImpl());
    }
}
