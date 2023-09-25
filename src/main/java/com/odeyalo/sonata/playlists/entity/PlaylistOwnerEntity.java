package com.odeyalo.sonata.playlists.entity;

import com.odeyalo.sonata.playlists.model.EntityType;

public interface PlaylistOwnerEntity {
    Long getId();

    String getPublicId();

    String getDisplayName();

    EntityType getEntityType();
}
