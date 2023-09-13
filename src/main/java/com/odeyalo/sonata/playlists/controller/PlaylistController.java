package com.odeyalo.sonata.playlists.controller;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.ImageDto;
import com.odeyalo.sonata.playlists.dto.ImagesDto;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import com.odeyalo.sonata.playlists.model.Image;
import com.odeyalo.sonata.playlists.model.Images;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.service.upload.ImageUploader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.odeyalo.sonata.playlists.model.EntityType.PLAYLIST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {

    private final PlaylistRepository playlistRepository;
    private final ImageUploader imageUploader;

    @Autowired
    public PlaylistController(PlaylistRepository playlistRepository, ImageUploader imageUploader) {
        this.playlistRepository = playlistRepository;
        this.imageUploader = imageUploader;
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

    @PostMapping(value = "/{playlistId}/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> playlistImageUpload(@PathVariable String playlistId,
                                                       @RequestPart("image") Mono<FilePart> file) {

        return playlistRepository.findById(playlistId)
                .zipWith(imageUploader.uploadImage(file))
                .flatMap(tuple -> {
                    Playlist playlist = tuple.getT1();
                    Image image = tuple.getT2();

                    Playlist updatedPlaylist = Playlist.from(playlist).images(Images.of(image)).build();
                    return playlistRepository.save(updatedPlaylist);
                })
                .map(playlist -> accepted().build())
                .defaultIfEmpty(unprocessableEntity().build());
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
        List<ImageDto> images = getOrEmptyImages(playlist).stream().map(image -> ImageDto.builder()
                .url(image.getUrl())
                .width(image.getWidth())
                .height(image.getHeight())
                .build()).toList();

        return PlaylistDto.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .playlistType(playlist.getPlaylistType())
                .type(PLAYLIST)
                .images(ImagesDto.of(images)).build();
    }

    private static Images getOrEmptyImages(Playlist playlist) {
        return playlist.getImages() != null ? playlist.getImages() : Images.empty();
    }

    private static ResponseEntity<PlaylistDto> default204Response() {
        return noContent().build();
    }
}
