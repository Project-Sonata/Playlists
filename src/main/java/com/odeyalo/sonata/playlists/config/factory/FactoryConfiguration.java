package com.odeyalo.sonata.playlists.config.factory;

import com.odeyalo.sonata.playlists.model.Playlist;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfiguration {

    @Bean
    public Playlist.Factory playlistFactory() {
        return new Playlist.Factory();
    }
}
