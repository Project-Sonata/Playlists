package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.*;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.service.CreatePlaylistInfo;
import com.odeyalo.sonata.playlists.service.PartialPlaylistDetailsUpdateInfo;
import com.odeyalo.sonata.playlists.service.PlaylistOperations;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.converter.CreatePlaylistInfoConverter;
import com.odeyalo.sonata.playlists.support.converter.ImagesDtoConverter;
import com.odeyalo.sonata.playlists.support.converter.PartialPlaylistDetailsUpdateInfoConverter;
import com.odeyalo.sonata.playlists.support.converter.PlaylistDtoConverter;
import com.odeyalo.sonata.playlists.support.web.HttpStatuses;
import com.odeyalo.suite.security.auth.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @GetMapping(value = "/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PlaylistDto>> findPlaylistById(@PathVariable String playlistId) {

        return playlistOperations.findById(playlistId)
                .map(playlistDtoConverter::toPlaylistDto)
                .map(HttpStatuses::defaultOkStatus)
                .defaultIfEmpty(default204Response());
    }

    @GetMapping(value = "/{playlistId}/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ImagesDto>> fetchPlaylistCoverImage(@PathVariable String playlistId) {

        return playlistOperations.findById(playlistId)
                .map(playlist -> imagesDtoConverter.toImagesDto(playlist.getImages()))
                .map(HttpStatuses::defaultOkStatus)
                .defaultIfEmpty(defaultUnprocessableEntityStatus());
    }

    @GetMapping(value = "/{playlistId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PlaylistItemsDto>> fetchPlaylistItems(@PathVariable String playlistId,
                                                                     @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                     @RequestParam(value = "limit", defaultValue = "50") int limit) {
        List<PlaylistItemDto> items = List.of(
                new PlaylistItemDto("1"),
                new PlaylistItemDto("2"),
                new PlaylistItemDto("3")
        );

        if (offset == limit) {
            return Mono.just(
                    HttpStatuses.defaultOkStatus(
                            new PlaylistItemsDto(items.subList(offset, limit + 1))
                    )
            );
        }

        return Mono.just(
                HttpStatuses.defaultOkStatus(new PlaylistItemsDto(
                        items.subList(offset > items.size() ? items.size() : offset,
                                limit > items.size() ? items.size() : limit)
                ))
        );
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> createPlaylist(@RequestBody CreatePlaylistRequest body, AuthenticatedUser authenticatedUser) {
        CreatePlaylistInfo playlistInfo = createPlaylistInfoConverter.toCreatePlaylistInfo(body);

        return playlistOperations.createPlaylist(playlistInfo, resolveOwner(authenticatedUser))
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

    @NotNull
    public static PlaylistOwner resolveOwner(AuthenticatedUser authenticatedUser) {
        return PlaylistOwner.builder()
                .id(authenticatedUser.getDetails().getId())
                .build();
    }
}
