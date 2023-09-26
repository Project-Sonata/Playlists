package com.odeyalo.sonata.playlists.model;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

/**
 * Represent the collection of the images of the playlist, entity, whatever
 */
@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class Images implements Iterable<Image> {
    @Getter(value = AccessLevel.NONE)
    List<Image> imageHolder;

    public static Images empty() {
        return of(emptyList());
    }

    public static Images single(Image image) {
        return of(image);
    }

    public static Images of(Image... images) {
        return builder().imageHolder(List.of(images)).build();
    }

    public int size() {
        return imageHolder.size();
    }

    public boolean isEmpty() {
        return imageHolder.isEmpty();
    }

    public boolean contains(Object o) {
        return imageHolder.contains(o);
    }

    public Image get(int index) {
        return imageHolder.get(index);
    }

    public Stream<Image> stream() {
        return imageHolder.stream();
    }

    @NotNull
    @Override
    public Iterator<Image> iterator() {
        return imageHolder.iterator();
    }
}
