package com.odeyalo.sonata.playlists.entity;

import com.odeyalo.sonata.playlists.model.PlaylistType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "playlists")
public class R2dbcPlaylistEntity implements PlaylistEntity, Persistable<Long> {
    @Id
    @Column("playlist_id")
    Long id;
    String publicId;
    String playlistName;
    String playlistDescription;
    PlaylistType playlistType = PlaylistType.PRIVATE;
    @Transient
    List<PlaylistImage> images;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
