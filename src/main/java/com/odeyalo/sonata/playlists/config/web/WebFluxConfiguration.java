package com.odeyalo.sonata.playlists.config.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.Decoder;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.json.Jackson2JsonDecoder;

@Configuration
public class WebFluxConfiguration {

    @Bean
    public Decoder<?> jackson2JsonDecoder(ObjectMapper mapper) {
        return new Jackson2JsonDecoder(mapper);
    }

    @Bean
    public HttpMessageReader<?> httpMessageReader(Decoder<?> decoder) {
        return new DecoderHttpMessageReader<>(decoder);
    }
}
