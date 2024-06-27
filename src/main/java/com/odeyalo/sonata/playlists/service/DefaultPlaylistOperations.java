package com.odeyalo.sonata.playlists.service;

import com.odeyalo.sonata.playlists.model.Image;
import com.odeyalo.sonata.playlists.model.Images;
import com.odeyalo.sonata.playlists.model.Playlist;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import com.odeyalo.sonata.playlists.service.upload.ImageUploader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * Default {@link PlaylistOperations} implementation that just save playlist in repository
 */
@Service
public final class DefaultPlaylistOperations implements PlaylistOperations {
    private final PlaylistService playlistService;
    private final ImageUploader imageUploader;

    @Autowired
    public DefaultPlaylistOperations(final PlaylistService playlistService, final ImageUploader imageUploader) {
        this.playlistService = playlistService;
        this.imageUploader = imageUploader;
    }

    @Override
    @NotNull
    public Mono<Playlist> findById(@NotNull String playlistId) {
        return playlistService.loadPlaylist(playlistId);
    }

    @Override
    @NotNull
    public Mono<Playlist> createPlaylist(CreatePlaylistInfo playlistInfo, PlaylistOwner playlistOwner) {
        final Playlist playlist = toPlaylist(playlistInfo, playlistOwner);
        return playlistService.save(playlist);
    }

    @Override
    public Mono<Playlist> updatePlaylistCoverImage(@NotNull final TargetPlaylist targetPlaylist,
                                                   @NotNull final Mono<FilePart> file) {

        return playlistService.loadPlaylist(targetPlaylist)
                .zipWith(imageUploader.uploadImage(file))
                .flatMap(this::uploadImageAndSavePlaylist);
    }

    @Override
    public Mono<Playlist> updatePlaylistInfo(@NotNull final TargetPlaylist targetPlaylist,
                                             @NotNull final PartialPlaylistDetailsUpdateInfo updateInfo) {

        return playlistService.loadPlaylist(targetPlaylist)
                .map(playlist -> partialPlaylistUpdate(updateInfo, playlist))
                .flatMap(playlistService::save);
    }

    @NotNull
    private Mono<Playlist> uploadImageAndSavePlaylist(Tuple2<Playlist, Image> tuple) {
        Playlist playlist = tuple.getT1();
        Image image = tuple.getT2();

        Playlist updatedPlaylist = Playlist.from(playlist).images(Images.single(image)).build();
        return playlistService.save(updatedPlaylist);
    }

    private static Playlist partialPlaylistUpdate(PartialPlaylistDetailsUpdateInfo updateInfo, Playlist playlist) {
        Playlist.PlaylistBuilder builder = Playlist.from(playlist);

        if ( updateInfo.getName() != null ) {
            builder.name(updateInfo.getName());
        }

        if ( updateInfo.getDescription() != null ) {
            builder.description(updateInfo.getDescription());
        }

        if ( updateInfo.getPlaylistType() != null ) {
            builder.playlistType(updateInfo.getPlaylistType());
        }
        return builder.build();
    }

    private static Playlist toPlaylist(CreatePlaylistInfo playlistInfo, PlaylistOwner playlistOwner) {
        return Playlist.builder()
                .name(playlistInfo.getName())
                .description(playlistInfo.getDescription())
                .playlistType(playlistInfo.getPlaylistType())
                .playlistOwner(playlistOwner)
                .build();
    }
}