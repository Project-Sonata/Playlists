package com.odeyalo.sonata.playlists.config;

import com.odeyalo.sonata.common.context.ContextUriParser;
import com.odeyalo.sonata.common.context.HardcodedContextUriParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParsersConfiguration {

    @Bean
    public ContextUriParser contextUriParser() {
        return new HardcodedContextUriParser();
    }
}
