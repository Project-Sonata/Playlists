package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.odeyalo.sonata.playlists.model.EntityType.PLAYLIST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {

    private final PlaylistRepository playlistRepository;

    @Autowired
    public PlaylistController(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @GetMapping(value = "/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PlaylistDto>> findPlaylistById(@PathVariable String playlistId) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> ok().body(convertToDto(playlist)))
                .defaultIfEmpty(default204Response());
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> createPlaylist(@RequestBody CreatePlaylistRequest body) {
        Playlist playlist = convertToPlaylist(body);

        return playlistRepository.save(playlist)
                .map(PlaylistController::convertToDto)
                .map(PlaylistController::defaultCreatedStatus);
    }

    private static Playlist convertToPlaylist(CreatePlaylistRequest body) {
        return Playlist.builder()
                .name(body.getName())
                .description(body.getDescription())
                .playlistType(body.getType())
                .build();
    }

    @NotNull
    private static ResponseEntity<PlaylistDto> defaultCreatedStatus(PlaylistDto responseBody) {
        return status(CREATED).body(responseBody);
    }

    private static PlaylistDto convertToDto(Playlist playlist) {
        return PlaylistDto.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .playlistType(playlist.getPlaylistType())
                .type(PLAYLIST)
                .build();
    }

    private static ResponseEntity<PlaylistDto> default204Response() {
        return noContent().build();
    }
}
