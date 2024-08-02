package com.odeyalo.sonata.playlists.config.factory;

import com.odeyalo.sonata.playlists.entity.factory.*;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.support.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfiguration {

    @Bean
    public Playlist.Factory playlistFactory() {
        return new Playlist.Factory();
    }


    @Bean
    public PlaylistEntityFactory playlistEntityFactory() {
        return new DefaultPlaylistEntityFactory(imagesEntityFactory(), playlistOwnerEntityFactory());
    }

    @Bean
    public PlaylistItemEntityFactory playlistItemEntityFactory(PlaylistCollaboratorEntityFactory collaboratorFactory,
                                                               Clock clock) {
        return new DefaultPlaylistItemEntityFactory(collaboratorFactory, clock);
    }

    @Bean
    public PlaylistCollaboratorEntityFactory playlistCollaboratorEntityFactory() {
        return new DefaultPlaylistCollaboratorEntityFactory();
    }

    @Bean
    public ImagesEntityFactory imagesEntityFactory() {
        return new DefaultImagesEntityFactory(imageEntityFactory());
    }

    @Bean
    public PlaylistOwnerEntityFactory playlistOwnerEntityFactory() {
        return new PlaylistOwnerEntityFactory();
    }

    @Bean
    public ImageEntityFactory imageEntityFactory() {
        return new ImageEntityFactory();
    }
}
