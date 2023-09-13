package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controller for QA environment
 */
@RestController
@RequestMapping("/qa")
@Profile("test")
public class QaController {
    private final PlaylistRepository playlistRepository;

    @Autowired
    public QaController(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @DeleteMapping(value = "/playlist/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> clearPlaylists() {
        return playlistRepository.clear();
    }
}
