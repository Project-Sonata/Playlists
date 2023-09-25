package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.*;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.support.R2dbcPlaylistRepositoryDelegate;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;


/**
 * PlaylistRepository that save the data using R2DBC
 *
 * @see PlaylistRepository for furher information
 */
@Component
public class R2dbcPlaylistRepository implements PlaylistRepository {
    private final R2dbcPlaylistRepositoryDelegate r2dbcRepositoryDelegate;
    private final PlaylistImagesRepository playlistImagesRepository;
    private final R2dbcImageEntityRepository r2dbcImageEntityRepository;
    @Autowired
    R2dbcPlaylistOwnerRepository r2DbcPlaylistOwnerRepository;

    public R2dbcPlaylistRepository(R2dbcPlaylistRepositoryDelegate r2dbcRepositoryDelegate,
                                   PlaylistImagesRepository playlistImagesRepository,
                                   R2dbcImageEntityRepository r2dbcImageEntityRepository) {
        this.r2dbcRepositoryDelegate = r2dbcRepositoryDelegate;
        this.playlistImagesRepository = playlistImagesRepository;
        this.r2dbcImageEntityRepository = r2dbcImageEntityRepository;
    }

    @Override
    @NotNull
    public Mono<Playlist> save(Playlist playlist) {

        if (playlist.getId() == null) {
            return savePlaylist(playlist);
        }

        return updatePlaylist(playlist);
    }

    @Override
    @NotNull
    public Mono<Playlist> findById(String id) {
        return r2dbcRepositoryDelegate.findByPublicId(id)
                .flatMap(this::fulfillFoundEntity)
                .mapNotNull(tuple -> convertToPlaylist(tuple.getT1(), tuple.getT2()));
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return playlistImagesRepository.deleteAll()
                .thenEmpty(r2dbcImageEntityRepository.deleteAll())
                .thenEmpty(r2dbcRepositoryDelegate.deleteAll());
    }

    @NotNull
    private Mono<Tuple2<R2dbcPlaylistEntity, List<R2dbcImageEntity>>> fulfillFoundEntity(R2dbcPlaylistEntity playlist) {
        return r2DbcPlaylistOwnerRepository.findById(playlist.getPlaylistOwnerId())
                .map(owner -> {
                    playlist.setPlaylistOwner(owner);
                    return playlist;
                })
                .flatMap(this::findAndEnhancePlaylistImages);
    }

    @NotNull
    private Mono<Tuple2<R2dbcPlaylistEntity, List<R2dbcImageEntity>>> findAndEnhancePlaylistImages(R2dbcPlaylistEntity playlist) {
        return playlistImagesRepository.findAllByPlaylistId(playlist.getId())
                .flatMap(imageMetadata -> r2dbcImageEntityRepository.findById(imageMetadata.getImageId()))
                .collectList()
                .defaultIfEmpty(Collections.emptyList())
                .map(images -> Tuples.of(playlist, images));
    }

    @NotNull
    private Mono<Playlist> savePlaylist(Playlist playlist) {

        Mono<R2dbcPlaylistOwnerEntity> playlistOwner = r2DbcPlaylistOwnerRepository
                .findByPublicId(playlist.getPlaylistOwner().getId())
                .switchIfEmpty(Mono.defer(() -> {
                    R2dbcPlaylistOwnerEntity entity = R2dbcPlaylistOwnerEntity.builder()
                            .publicId(playlist.getPlaylistOwner().getId())
                            .displayName(playlist.getPlaylistOwner().getDisplayName())
                            .build();
                    return r2DbcPlaylistOwnerRepository.save(entity);
                }));

        return playlistOwner.flatMap(owner -> r2dbcRepositoryDelegate.save(createPlaylistEntity(playlist, owner.getId())))
                .flatMap(this::fulfillFoundEntity)
                .mapNotNull(tuple -> convertToPlaylist(tuple.getT1(), tuple.getT2()));
    }

    @NotNull
    private Mono<Playlist> updatePlaylist(Playlist playlist) {
        return r2dbcRepositoryDelegate.findByPublicId(playlist.getId())
                .flatMap(parent -> updatePlaylistEntity(playlist, parent))
                .map(R2dbcPlaylistRepository::convertEntityToPlaylist);
    }

    @NotNull
    private Mono<R2dbcPlaylistEntity> updatePlaylistEntity(Playlist playlist, R2dbcPlaylistEntity parent) {
        R2dbcPlaylistEntity entity = toPlaylistEntityBuilder(playlist)
                .playlistOwner(parent.getPlaylistOwner())
                .playlistOwnerId(parent.getPlaylistOwnerId())
                .id(parent.getId()).build();

        List<R2dbcImageEntity> entities = getImageEntities(playlist);

        Mono<List<PlaylistImage>> savedImages = saveImages(parent, entities);
        return savedImages.doOnNext(entity::setImages).flatMap(toSave -> r2dbcRepositoryDelegate.save(entity));
    }

    @NotNull
    private Mono<List<PlaylistImage>> saveImages(R2dbcPlaylistEntity parent, List<R2dbcImageEntity> entities) {
        return Flux.fromIterable(entities)
                .filterWhen(this::isImageNotExist)
                .flatMap(entity -> playlistImagesRepository.deleteAllByPlaylistId(parent.getId()).thenReturn(entity))
                .flatMap(r2dbcImageEntityRepository::save)
                .flatMap(imageEntity -> buildAndSave(parent, imageEntity))
                .collectList();
    }

    @NotNull
    private Mono<Boolean> isImageNotExist(R2dbcImageEntity entity) {
        return r2dbcImageEntityRepository.findByUrl(entity.getUrl())
                .map(e -> false)
                .defaultIfEmpty(true);
    }

    @NotNull
    private Mono<PlaylistImage> buildAndSave(R2dbcPlaylistEntity parent, R2dbcImageEntity imageEntity) {
        PlaylistImage imageToSave = PlaylistImage.builder().imageId(imageEntity.getId()).playlistId(parent.getId()).build();
        return playlistImagesRepository.save(imageToSave);
    }

    @NotNull
    private static R2dbcImageEntity convertToImageEntity(Image image) {
        return R2dbcImageEntity.builder().url(image.getUrl()).height(image.getHeight()).width(image.getWidth()).build();
    }

    @NotNull
    private static List<R2dbcImageEntity> getImageEntities(Playlist playlist) {
        return playlist.getImages().stream().map(R2dbcPlaylistRepository::convertToImageEntity).toList();
    }

    @NotNull
    private static R2dbcPlaylistEntity createPlaylistEntity(Playlist playlist, Long playlistOwnerId) {
        PlaylistType playlistType = playlist.getPlaylistType() != null ? playlist.getPlaylistType() : PlaylistType.PRIVATE;
        String playlistId = playlist.getId() != null ? playlist.getId() : RandomStringUtils.randomAlphanumeric(22);

        R2dbcPlaylistEntity.R2dbcPlaylistEntityBuilder builder = toPlaylistEntityBuilder(playlist);


        return builder.publicId(playlistId)
                .playlistOwnerId(playlistOwnerId)
                .playlistType(playlistType)
                .build();
    }

    @NotNull
    private Playlist convertToPlaylist(R2dbcPlaylistEntity entity, List<R2dbcImageEntity> imageEntities) {
        List<Image> images = imageEntities.stream().map(R2dbcPlaylistRepository::toImage).toList();

        PlaylistOwner owner = buildPlaylistOwner(entity);

        return toPlaylistBuilder(entity).playlistOwner(owner).images(Images.of(images)).build();
    }

    @NotNull
    private static Image toImage(ImageEntity image) {
        return Image.of(image.getUrl(), image.getWidth(), image.getHeight());
    }

    @NotNull
    private static Playlist convertEntityToPlaylist(R2dbcPlaylistEntity entity) {
        return toPlaylistBuilder(entity).build();
    }

    @NotNull
    private static R2dbcPlaylistEntity.R2dbcPlaylistEntityBuilder toPlaylistEntityBuilder(Playlist playlist) {
        return R2dbcPlaylistEntity.builder()
                .publicId(playlist.getId())
                .playlistName(playlist.getName())
                .playlistDescription(playlist.getDescription())
                .playlistType(playlist.getPlaylistType());
    }

    @NotNull
    private static Playlist.PlaylistBuilder toPlaylistBuilder(R2dbcPlaylistEntity entity) {
        return Playlist.builder()
                .id(entity.getPublicId())
                .name(entity.getPlaylistName())
                .description(entity.getPlaylistDescription())
                .type(EntityType.PLAYLIST)
                .images(Images.empty())
                .playlistOwner(createPlaylistOwnerOrNull(entity))
                .playlistType(entity.getPlaylistType());
    }

    private static PlaylistOwner createPlaylistOwnerOrNull(R2dbcPlaylistEntity entity) {
        if (entity.getPlaylistOwner() == null) {
            return null;
        }
        return buildPlaylistOwner(entity);
    }

    private static PlaylistOwner buildPlaylistOwner(R2dbcPlaylistEntity entity) {
        return PlaylistOwner.builder()
                .id(entity.getPlaylistOwner().getPublicId())
                .displayName(entity.getPlaylistOwner().getDisplayName()).build();
    }
}
