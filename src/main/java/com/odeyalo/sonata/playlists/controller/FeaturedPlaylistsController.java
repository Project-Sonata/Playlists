package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.PersonalizedPlaylistDto;
import com.odeyalo.sonata.playlists.model.PlaylistId;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.repository.GeneratedPlaylistRepository;
import com.odeyalo.sonata.playlists.service.PlaylistService;
import com.odeyalo.sonata.playlists.support.converter.PlaylistConverter;
import com.odeyalo.sonata.playlists.support.converter.PlaylistDtoConverter;
import com.odeyalo.sonata.playlists.support.web.HttpStatuses;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public final class FeaturedPlaylistsController {
    private final GeneratedPlaylistRepository generatedPlaylistRepository;
    private final PlaylistService playlistService;
    private final PlaylistDtoConverter playlistConverter;

    public FeaturedPlaylistsController(final GeneratedPlaylistRepository generatedPlaylistRepository,
                                       final PlaylistService playlistService,
                                       final PlaylistDtoConverter playlistConverter) {
        this.generatedPlaylistRepository = generatedPlaylistRepository;
        this.playlistService = playlistService;
        this.playlistConverter = playlistConverter;
    }

    @GetMapping("/featured")
    public Mono<ResponseEntity<List<PersonalizedPlaylistDto>>> getPersonalizedPlaylists(@NotNull final User user) {
        return getPlaylistsFor(user)
                .map(HttpStatuses::defaultOkStatus);
    }


    @NotNull
    private Mono<List<PersonalizedPlaylistDto>> getPlaylistsFor(@NotNull final User user) {
        return generatedPlaylistRepository.findByGeneratedFor(user.getId())
                .flatMap(generatedPlaylistEntity -> playlistService.loadPlaylist(PlaylistId.of(generatedPlaylistEntity.getPlaylistId()))
                        .map(playlistConverter::toPlaylistDto)
                        .map(playlistDto ->
                                new PersonalizedPlaylistDto(
                                        playlistDto, generatedPlaylistEntity.getGeneratedPlaylistType()
                                )
                        )
                )
                .collectList();
    }
}
