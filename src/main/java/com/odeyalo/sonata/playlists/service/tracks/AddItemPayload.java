package com.odeyalo.sonata.playlists.service.tracks;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddItemPayload {
    String[] uris;

    public static AddItemPayload withItemUri(String playableItemContextUri) {
        String[] uris = {playableItemContextUri};

        return AddItemPayload.builder()
                .uris(uris)
                .build();
    }
}
