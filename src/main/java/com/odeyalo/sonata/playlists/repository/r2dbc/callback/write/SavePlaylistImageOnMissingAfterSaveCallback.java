package com.odeyalo.sonata.playlists.repository.r2dbc.callback.write;

import com.odeyalo.sonata.playlists.entity.PlaylistImage;
import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.repository.PlaylistImagesRepository;
import com.odeyalo.sonata.playlists.repository.R2dbcImageRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public final class SavePlaylistImageOnMissingAfterSaveCallback implements AfterSaveCallback<PlaylistEntity> {
    private final PlaylistImagesRepository playlistImagesRepository;
    private final R2dbcImageRepository r2DbcImageRepository;

    public SavePlaylistImageOnMissingAfterSaveCallback(@Lazy PlaylistImagesRepository playlistImagesRepository,
                                                       @Lazy R2dbcImageRepository r2DbcImageRepository) {
        this.playlistImagesRepository = playlistImagesRepository;
        this.r2DbcImageRepository = r2DbcImageRepository;
    }

    @Override
    @NotNull
    public Publisher<PlaylistEntity> onAfterSave(@NotNull final PlaylistEntity entity,
                                                 @NotNull final OutboundRow outboundRow,
                                                 @NotNull final SqlIdentifier table) {
        return saveImages(entity).thenReturn(entity);
    }

    @NotNull
    private Mono<List<PlaylistImage>> saveImages(PlaylistEntity parent) {
        List<ImageEntity> images = parent.getImages();
        return Flux.fromIterable(images)
                .filterWhen(this::isImageNotExist)
                .flatMap(entity -> playlistImagesRepository.deleteAllByPlaylistId(parent.getId()).thenReturn(entity))
                .flatMap(r2DbcImageRepository::save)
                .flatMap(imageEntity -> buildAndSave(parent, imageEntity))
                .collectList();
    }

    @NotNull
    private Mono<PlaylistImage> buildAndSave(PlaylistEntity parent, ImageEntity imageEntity) {
        PlaylistImage imageToSave = PlaylistImage.builder().imageId(imageEntity.getId()).playlistId(parent.getId()).build();
        return playlistImagesRepository.save(imageToSave);
    }

    @NotNull
    private Mono<Boolean> isImageNotExist(ImageEntity entity) {
        return r2DbcImageRepository.findByUrl(entity.getUrl())
                .map(e -> false)
                .defaultIfEmpty(true);
    }


}
