package com.odeyalo.sonata.playlists.entity;

import com.odeyalo.sonata.playlists.model.PlaylistType;

public interface PlaylistEntity {
    Long getId();

    String getPublicId();

    String getPlaylistName();

    String getPlaylistDescription();

    PlaylistType getPlaylistType();

}
