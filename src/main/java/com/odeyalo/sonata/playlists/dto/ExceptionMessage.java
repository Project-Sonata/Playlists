package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ExceptionMessage {
    @JsonProperty("description")
    String description;

    public static ExceptionMessage withDescription(String description) {
        return builder().description(description).build();
    }

    public static ExceptionMessage withDescription(@NotNull final String description, final Object... args) {
        return builder().description(String.format(description, args)).build();
    }
}