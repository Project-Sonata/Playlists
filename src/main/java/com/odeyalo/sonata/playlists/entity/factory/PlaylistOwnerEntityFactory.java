package com.odeyalo.sonata.playlists.entity.factory;

import com.odeyalo.sonata.playlists.entity.PlaylistOwnerEntity;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.jetbrains.annotations.NotNull;

public final class PlaylistOwnerEntityFactory {

    @NotNull
    public PlaylistOwnerEntity create(@NotNull PlaylistOwner owner) {
        return PlaylistOwnerEntity.builder()
                .publicId(owner.getId())
                .displayName(owner.getDisplayName())
                .entityType(owner.getEntityType())
                .build();
    }
}
