package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity.PlaylistEntityBuilder;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.jetbrains.annotations.NotNull;

public final class DefaultPlaylistEntityFactory implements PlaylistEntityFactory {
    private final ImagesEntityFactory imagesFactory;
    private final PlaylistOwnerEntityFactory playlistOwnerFactory;

    public DefaultPlaylistEntityFactory(final ImagesEntityFactory imagesFactory,
                                        final PlaylistOwnerEntityFactory playlistOwnerFactory) {
        this.imagesFactory = imagesFactory;
        this.playlistOwnerFactory = playlistOwnerFactory;
    }

    @Override
    @NotNull
    public PlaylistEntity create(@NotNull final Playlist playlist) {
        final PlaylistEntityBuilder builder = PlaylistEntity.builder();

        return builder
                .publicId(playlist.getId())
                .playlistName(playlist.getName())
                .playlistDescription(playlist.getDescription())
                .playlistType(playlist.getPlaylistType())
                .images(imagesFactory.create(playlist.getImages()).getImages())
                .playlistOwner(playlistOwnerFactory.create(playlist.getPlaylistOwner()))
                .contextUri(playlist.getContextUri().asString())
                .build();
    }
}
