package com.odeyalo.sonata.playlists.service.tracks;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddItemPayload {
    String[] uris;
    int position;

    public static AddItemPayload withItemUri(String playableItemContextUri) {
        String[] uris = {playableItemContextUri};

        return AddItemPayload.builder()
                .uris(uris)
                .build();
    }
    public static AddItemPayload atPosition(int pos, String playableItemContextUri) {
        String[] uris = {playableItemContextUri};

        return AddItemPayload.builder()
                .uris(uris)
                .position(pos)
                .build();
    }

    public static AddItemPayload withItemUris(String... contextUris) {
        String[] uris = new String[contextUris.length];

        for (int i = 0; i < contextUris.length; i++) {
            String uri = contextUris[i];
            uris[i] = uri;
        }

        return new AddItemPayload(uris, Integer.MAX_VALUE);
    }
}
