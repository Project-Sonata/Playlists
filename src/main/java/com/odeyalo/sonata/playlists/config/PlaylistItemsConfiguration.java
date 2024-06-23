package com.odeyalo.sonata.playlists.config;

import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.service.PlaylistLoader;
import com.odeyalo.sonata.playlists.service.PlaylistService;
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
        return new PlaylistService(playlistRepository);
    }
}
