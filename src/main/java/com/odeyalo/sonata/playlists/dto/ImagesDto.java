package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Dto to transfer collection of ImageDto
 */
@AllArgsConstructor(staticName = "multiple")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImagesDto implements Iterable<ImageDto> {
    @JsonUnwrapped
    @Singular
    List<ImageDto> images;

    public static ImagesDto empty() {
        return new ImagesDto(emptyList());
    }

    public static ImagesDto single(ImageDto imageDto) {
        return of(imageDto);
    }

    public static ImagesDto of(Collection<ImageDto> images) {
        return builder().images(images).build();
    }

    public static ImagesDto of(ImageDto... images) {
        return builder().images(List.of(images)).build();
    }

    public int size() {
        return images.size();
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    public boolean contains(Object o) {
        return images.contains(o);
    }

    public ImageDto get(int index) {
        return images.get(index);
    }

    @NotNull
    @Override
    public Iterator<ImageDto> iterator() {
        return images.iterator();
    }
}
