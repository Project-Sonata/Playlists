package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.ImagesDto;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.service.CreatePlaylistInfo;
import com.odeyalo.sonata.playlists.service.PartialPlaylistDetailsUpdateInfo;
import com.odeyalo.sonata.playlists.service.PlaylistOperationsFacade;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.converter.ImagesDtoConverter;
import com.odeyalo.sonata.playlists.support.converter.PlaylistDtoConverter;
import com.odeyalo.sonata.playlists.support.web.HttpStatuses;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.odeyalo.sonata.playlists.support.web.HttpStatuses.*;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistOperationsFacade playlistOperationsFacade;
    private final PlaylistDtoConverter playlistDtoConverter;
    private final ImagesDtoConverter imagesDtoConverter;

    @GetMapping(value = "/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PlaylistDto>> findPlaylistById(@PathVariable @NotNull TargetPlaylist playlistId,
                                                              @NotNull final User user) {

        return playlistOperationsFacade.findById(playlistId, user)
                .map(playlistDtoConverter::toPlaylistDto)
                .map(HttpStatuses::defaultOkStatus)
                .onErrorResume(PlaylistNotFoundException.class, it -> Mono.just(HttpStatuses.default204Response()));
    }

    @GetMapping(value = "/{playlistId}/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ImagesDto>> fetchPlaylistCoverImage(@PathVariable @NotNull final TargetPlaylist playlistId,
                                                                   @NotNull final User user) {

        return playlistOperationsFacade.findById(playlistId, user)
                .map(playlist -> imagesDtoConverter.toImagesDto(playlist.getImages()))
                .map(HttpStatuses::defaultOkStatus)
                .onErrorResume(PlaylistNotFoundException.class, it -> Mono.just(defaultUnprocessableEntityStatus()));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> createPlaylist(@NotNull final CreatePlaylistInfo playlistInfo,
                                                  @NotNull final PlaylistOwner playlistOwner,
                                                  @NotNull final User user) {

        return playlistOperationsFacade.createPlaylist(playlistInfo, playlistOwner, user)
                .map(playlistDtoConverter::toPlaylistDto)
                .map(HttpStatuses::defaultCreatedStatus);
    }

    @PostMapping(value = "/{playlistId}/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> playlistImageUpload(@PathVariable @NotNull final TargetPlaylist playlistId,
                                                            @RequestPart("image") @NotNull final Mono<FilePart> file,
                                                            @NotNull final User user) {

        return playlistOperationsFacade.updatePlaylistCoverImage(playlistId, file, user)
                .map(playlist -> defaultAcceptedStatus())
                .onErrorResume(PlaylistNotFoundException.class, err -> Mono.just(defaultUnprocessableEntityStatus()));


    }

    @PatchMapping(value = "/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> updatePlaylistDetails(@PathVariable("playlistId") @NotNull final TargetPlaylist targetPlaylist,
                                                              @NotNull final PartialPlaylistDetailsUpdateInfo updateInfo,
                                                              @NotNull final User user) {

        return playlistOperationsFacade.updatePlaylistInfo(targetPlaylist, updateInfo, user)
                .map(playlist -> default204Response())
                .onErrorResume(PlaylistNotFoundException.class, err -> Mono.just(defaultUnprocessableEntityStatus()));
    }
}
