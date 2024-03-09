package com.odeyalo.sonata.playlists.model.track;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Container for multiple artists
 */
@Value
@AllArgsConstructor(staticName = "multiple")
@Builder
public class ArtistContainer implements Iterable<Artist> {
    @Getter(value = AccessLevel.NONE)
    @NotNull
    @Singular
    List<Artist> artists;

    public static ArtistContainer empty() {
        return new ArtistContainer(Collections.emptyList());
    }

    public static ArtistContainer single(Artist artist) {
        Assert.notNull(artist, "ArtistDto cannot be null!");
        return new ArtistContainer(List.of(artist));
    }

    public int size() {
        return artists.size();
    }

    public boolean isEmpty() {
        return artists.isEmpty();
    }

    public Artist get(int index) {
        return artists.get(index);
    }

    @NotNull
    @Override
    public Iterator<Artist> iterator() {
        return artists.iterator();
    }
}
