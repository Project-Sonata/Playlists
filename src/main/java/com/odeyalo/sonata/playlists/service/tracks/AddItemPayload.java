package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.PlaylistItemPosition;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@Builder
public class AddItemPayload {
    @NotNull
    String[] uris;
    @NotNull
    @Builder.Default
    PlaylistItemPosition position = PlaylistItemPosition.atEnd();

    public static AddItemPayload withItemUri(String playableItemContextUri) {
        String[] uris = {playableItemContextUri};

        return AddItemPayload.builder()
                .uris(uris)
                .build();
    }

    public static AddItemPayload atPosition(@NotNull PlaylistItemPosition position,
                                            @NotNull String playableItemContextUri) {
        String[] uris = {playableItemContextUri};

        return AddItemPayload.builder()
                .uris(uris)
                .position(position)
                .build();
    }

    public static AddItemPayload withItemUris(String... contextUris) {
        String[] uris = new String[contextUris.length];

        for (int i = 0; i < contextUris.length; i++) {
            String uri = contextUris[i];
            uris[i] = uri;
        }

        return AddItemPayload.builder()
                .uris(uris)
                .build();
    }
}
