package com.odeyalo.sonata.playlists.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Dto to send the image in response body
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageDto {
    @NotNull
    String url;
    @Nullable
    Integer width;
    @Nullable
    Integer height;

    public static ImageDto urlOnly(String url) {
        return builder().url(url).build();
    }
}