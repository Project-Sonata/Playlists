package com.odeyalo.sonata.playlists.config;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.repository.InMemoryPlaylistItemsRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.RepositoryDelegatePlaylistLoader;
import com.odeyalo.sonata.playlists.service.tracks.InMemoryPlayableItemLoader;
import com.odeyalo.sonata.playlists.service.tracks.PlayableItemLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlaylistItemsConfiguration {

    @Bean
    public PlayableItemLoader playableItemLoader() {
        return new InMemoryPlayableItemLoader();
    }

    @Bean
    public PlaylistLoader playlistLoader(PlaylistRepository playlistRepository) {
        return new RepositoryDelegatePlaylistLoader(playlistRepository);
    }

    @Bean
    public PlaylistItemsRepository playlistItemsRepository() {
        return new InMemoryPlaylistItemsRepository();
    }

}
