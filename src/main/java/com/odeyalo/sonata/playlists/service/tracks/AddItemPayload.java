package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
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

    @NotNull
    public Item[] determineItemsPosition(long playlistSize) {
        Item[] items = new Item[uris.length];

        for (int currentIndex = 0; currentIndex < uris.length; currentIndex++) {
            final ContextUri contextUri = ContextUri.fromString(uris[currentIndex]);

            if ( position.isEndOfPlaylist(playlistSize) ) {
                final int position = (int) (playlistSize + currentIndex);
                items[currentIndex] = new Item(contextUri, PlaylistItemPosition.at(position));
            } else {
                items[currentIndex] = new Item(contextUri, PlaylistItemPosition.at(position.value() + currentIndex));
            }
        }

        return items;
    }

    record Item(ContextUri contextUri, PlaylistItemPosition position) {

    }

    @NotNull
    public static AddItemPayload atPosition(@NotNull final PlaylistItemPosition position,
                                            @NotNull final String[] itemUris) {
        return builder()
                .position(position)
                .uris(itemUris)
                .build();
    }
}
