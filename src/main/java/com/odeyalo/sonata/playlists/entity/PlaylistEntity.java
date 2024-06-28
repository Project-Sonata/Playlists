package com.odeyalo.sonata.playlists.entity;

import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "playlists")
public class PlaylistEntity implements Persistable<Long> {
    @Id
    @Column("playlist_id")
    @With
    Long id;
    String publicId;
    String playlistName;
    String playlistDescription;
    @Column("context_uri")
    String contextUri;
    PlaylistType playlistType = PlaylistType.PRIVATE;
    @Column("owner_id")
    Long playlistOwnerId;
    @Transient
    @Builder.Default
    List<ImageEntity> images = new ArrayList<>();
    @Transient
    PlaylistOwnerEntity playlistOwner;

    @NotNull
    public static PlaylistEntityBuilder from(@NotNull final PlaylistEntity copyFrom) {
        return copyFrom.toBuilder();
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    public void setPlaylistOwner(PlaylistOwnerEntity playlistOwner) {
        this.playlistOwner = playlistOwner;
        this.playlistOwnerId = playlistOwner.getId();
    }
}
