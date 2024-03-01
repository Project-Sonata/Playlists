package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.ImageEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistOwnerEntity;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.support.R2dbcPlaylistRepositoryDelegate;
import com.odeyalo.sonata.playlists.support.converter.ImageEntityConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * PlaylistRepository that save the data using R2DBC
 *
 * @see PlaylistRepository for furher information
 */
@Component
public class R2dbcPlaylistRepository implements PlaylistRepository {
    private final R2dbcPlaylistRepositoryDelegate playlistRepositoryDelegate;
    private final PlaylistImagesRepository playlistImagesRepository;
    private final R2dbcImageRepository r2DbcImageRepository;
    @Autowired
    R2dbcPlaylistOwnerRepository r2DbcPlaylistOwnerRepository;
    private final ImageEntityConverter imageEntityConverter;

    public R2dbcPlaylistRepository(R2dbcPlaylistRepositoryDelegate playlistRepositoryDelegate,
                                   PlaylistImagesRepository playlistImagesRepository,
                                   R2dbcImageRepository r2DbcImageRepository,
                                   ImageEntityConverter imageEntityConverter) {
        this.playlistRepositoryDelegate = playlistRepositoryDelegate;
        this.playlistImagesRepository = playlistImagesRepository;
        this.r2DbcImageRepository = r2DbcImageRepository;
        this.imageEntityConverter = imageEntityConverter;
    }

    @Override
    @NotNull
    public Mono<Playlist> save(Playlist playlist) {

        if ( playlist.getId() == null ) {
            return savePlaylist(playlist);
        }

        return updatePlaylist(playlist);
    }

    @Override
    @NotNull
    public Mono<Playlist> findById(String id) {
        return playlistRepositoryDelegate.findByPublicId(id)
                .mapNotNull(playlist -> convertToPlaylist(playlist));
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return playlistImagesRepository.deleteAll()
                .thenEmpty(r2DbcImageRepository.deleteAll())
                .thenEmpty(playlistRepositoryDelegate.deleteAll());
    }

    @NotNull
    private Mono<Playlist> savePlaylist(Playlist playlist) {
        PlaylistOwnerEntity playlistOwner = PlaylistOwnerEntity.builder()
                .publicId(playlist.getPlaylistOwner().getId())
                .displayName(playlist.getPlaylistOwner().getDisplayName())
                .build();

        return playlistRepositoryDelegate.save(createPlaylistEntity(playlist, playlistOwner))
                .mapNotNull(playlistEntity -> convertToPlaylist(playlistEntity));
    }

    @NotNull
    private Mono<Playlist> updatePlaylist(Playlist playlist) {
        return playlistRepositoryDelegate.findByPublicId(playlist.getId())
                .flatMap(parent -> updatePlaylistEntity(playlist, parent))
                .map(R2dbcPlaylistRepository::convertEntityToPlaylist);
    }

    @NotNull
    private Mono<PlaylistEntity> updatePlaylistEntity(Playlist playlist, PlaylistEntity parent) {
        List<ImageEntity> images = getImageEntities(playlist);

        PlaylistEntity entity = toPlaylistEntityBuilder(playlist)
                .playlistOwner(parent.getPlaylistOwner())
                .playlistOwnerId(parent.getPlaylistOwnerId())
                .images(images)
                .id(parent.getId()).build();

        return playlistRepositoryDelegate.save(entity);
    }

    @NotNull
    private ImageEntity convertToImageEntity(Image image) {
        return imageEntityConverter.toImageEntity(image);
    }

    @NotNull
    private List<ImageEntity> getImageEntities(Playlist playlist) {
        return playlist.getImages().stream().map(image -> convertToImageEntity(image)).toList();
    }

    @NotNull
    private static PlaylistEntity createPlaylistEntity(Playlist playlist, PlaylistOwnerEntity playlistOwner) {
        String playlistId = playlist.getId() != null ? playlist.getId() : RandomStringUtils.randomAlphanumeric(22);

        PlaylistEntity.PlaylistEntityBuilder builder = toPlaylistEntityBuilder(playlist);


        return builder.publicId(playlistId)
                .playlistType(playlist.getPlaylistType())
                .playlistOwner(playlistOwner)
                .build();
    }

    @NotNull
    private Playlist convertToPlaylist(PlaylistEntity playlist) {
        List<ImageEntity> imageEntities = playlist.getImages();
        List<Image> images = imageEntities.stream().map(imageEntityConverter::toImage).toList();

        PlaylistOwner owner = buildPlaylistOwner(playlist);

        return toPlaylistBuilder(playlist).playlistOwner(owner).images(Images.of(images)).build();
    }

    @NotNull
    private static Playlist convertEntityToPlaylist(PlaylistEntity entity) {
        return toPlaylistBuilder(entity).build();
    }

    @NotNull
    private static PlaylistEntity.PlaylistEntityBuilder toPlaylistEntityBuilder(Playlist playlist) {
        return PlaylistEntity.builder()
                .publicId(playlist.getId())
                .playlistName(playlist.getName())
                .playlistDescription(playlist.getDescription())
                .playlistType(playlist.getPlaylistType());
    }

    @NotNull
    private static Playlist.PlaylistBuilder toPlaylistBuilder(PlaylistEntity entity) {
        return Playlist.builder()
                .id(entity.getPublicId())
                .name(entity.getPlaylistName())
                .description(entity.getPlaylistDescription())
                .playlistOwner(createPlaylistOwnerOrNull(entity))
                .playlistType(entity.getPlaylistType());
    }

    private static PlaylistOwner createPlaylistOwnerOrNull(PlaylistEntity entity) {
        if ( entity.getPlaylistOwner() == null ) {
            return null;
        }
        return buildPlaylistOwner(entity);
    }

    private static PlaylistOwner buildPlaylistOwner(PlaylistEntity entity) {
        return PlaylistOwner.builder()
                .id(entity.getPlaylistOwner().getPublicId())
                .displayName(entity.getPlaylistOwner().getDisplayName()).build();
    }
}
