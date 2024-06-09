package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.ImagesDto;
import com.odeyalo.sonata.playlists.dto.PartialPlaylistDetailsUpdateRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.exception.PlaylistNotFoundException;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.service.*;
import com.odeyalo.sonata.playlists.support.converter.CreatePlaylistInfoConverter;
import com.odeyalo.sonata.playlists.support.converter.ImagesDtoConverter;
import com.odeyalo.sonata.playlists.support.converter.PartialPlaylistDetailsUpdateInfoConverter;
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

    private final PlaylistOperations playlistOperations;
    private final PlaylistDtoConverter playlistDtoConverter;
    private final PartialPlaylistDetailsUpdateInfoConverter playlistDetailsUpdateInfoConverter;
    private final ImagesDtoConverter imagesDtoConverter;
    private final CreatePlaylistInfoConverter createPlaylistInfoConverter;
    private final PlaylistOperationsFacade playlistOperationsFacade;

    @GetMapping(value = "/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PlaylistDto>> findPlaylistById(@PathVariable("playlistId") @NotNull TargetPlaylist playlistId,
                                                              @NotNull final User user) {

        return playlistOperationsFacade.findById(playlistId, user)
                .map(playlistDtoConverter::toPlaylistDto)
                .map(HttpStatuses::defaultOkStatus)
                .onErrorResume(PlaylistNotFoundException.class, it -> Mono.just(HttpStatuses.default204Response()));
    }

    @GetMapping(value = "/{playlistId}/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ImagesDto>> fetchPlaylistCoverImage(@PathVariable String playlistId) {

        return playlistOperations.findById(playlistId)
                .map(playlist -> imagesDtoConverter.toImagesDto(playlist.getImages()))
                .map(HttpStatuses::defaultOkStatus)
                .defaultIfEmpty(defaultUnprocessableEntityStatus());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> createPlaylist(@RequestBody CreatePlaylistRequest body, PlaylistOwner playlistOwner) {
        CreatePlaylistInfo playlistInfo = createPlaylistInfoConverter.toCreatePlaylistInfo(body);

        return playlistOperations.createPlaylist(playlistInfo, playlistOwner)
                .map(playlistDtoConverter::toPlaylistDto)
                .map(HttpStatuses::defaultCreatedStatus);
    }

    @PostMapping(value = "/{playlistId}/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> playlistImageUpload(@PathVariable String playlistId,
                                                            @RequestPart("image") Mono<FilePart> file) {

        return playlistOperations.updatePlaylistCoverImage(TargetPlaylist.just(playlistId), file)
                .map(playlist -> defaultAcceptedStatus())
                .defaultIfEmpty(defaultUnprocessableEntityStatus());
    }

    @PatchMapping(value = "/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> updatePlaylistDetails(@PathVariable String playlistId, @RequestBody PartialPlaylistDetailsUpdateRequest body) {
        PartialPlaylistDetailsUpdateInfo updateInfo = playlistDetailsUpdateInfoConverter.toPartialPlaylistDetailsUpdateInfo(body);

        TargetPlaylist targetPlaylist = TargetPlaylist.just(playlistId);

        return playlistOperations.updatePlaylistInfo(targetPlaylist, updateInfo)
                .map(playlist -> default204Response())
                .defaultIfEmpty(defaultUnprocessableEntityStatus());
    }
}
