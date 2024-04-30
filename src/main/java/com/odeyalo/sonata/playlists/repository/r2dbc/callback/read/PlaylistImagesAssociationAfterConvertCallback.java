package com.odeyalo.sonata.playlists.repository.r2dbc.callback.read;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.repository.PlaylistImagesRepository;
import com.odeyalo.sonata.playlists.repository.r2dbc.R2dbcImageRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Associate the playlist images with the found playlist. If no images associated with this playlist, empty List is returned
 */
@Component
public final class PlaylistImagesAssociationAfterConvertCallback implements AfterConvertCallback<PlaylistEntity> {
    private final PlaylistImagesRepository playlistImagesRepository;
    private final R2dbcImageRepository r2DbcImageRepository;

    public PlaylistImagesAssociationAfterConvertCallback(@Lazy PlaylistImagesRepository playlistImagesRepository,
                                                         @Lazy R2dbcImageRepository r2DbcImageRepository) {
        this.playlistImagesRepository = playlistImagesRepository;
        this.r2DbcImageRepository = r2DbcImageRepository;
    }

    @Override
    @NotNull
    public Publisher<PlaylistEntity> onAfterConvert(@NotNull final PlaylistEntity playlistEntity,
                                                    @NotNull final SqlIdentifier table) {
        return findPlaylistImages(playlistEntity)
                .doOnNext(playlistEntity::setImages)
                .thenReturn(playlistEntity);
    }

    private Mono<List<ImageEntity>> findPlaylistImages(PlaylistEntity playlist) {
        return playlistImagesRepository.findAllByPlaylistId(playlist.getId())
                .flatMap(imageMetadata -> r2DbcImageRepository.findById(imageMetadata.getImageId()))
                .collectList();
    }
}
