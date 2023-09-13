package com.odeyalo.sonata.playlists.model;

import com.odeyalo.sonata.playlists.support.Asserts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represent the image of the entity(playlist, or anything)
 */
@Value
@AllArgsConstructor
@Builder
public class Image {
    @NotNull
    String url;
    @Nullable
    Integer width;
    @Nullable
    Integer height;

    public static Image urlOnly(String url) {
        Asserts.validUrl(url);
        return builder().url(url).build();
    }

    public static Image of(@NotNull String url, @Nullable Integer width, @Nullable Integer height) {
        Asserts.validUrl(url);
        Asserts.positive(width);
        Asserts.positive(height);
        return new Image(url, width, height);
    }
}
