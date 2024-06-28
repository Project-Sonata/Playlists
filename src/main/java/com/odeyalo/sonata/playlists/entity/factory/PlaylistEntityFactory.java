package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.Playlist;
import org.jetbrains.annotations.NotNull;

public interface PlaylistEntityFactory {

    @NotNull
    PlaylistEntity create(@NotNull Playlist playlist);

}
