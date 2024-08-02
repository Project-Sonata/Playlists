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
    ContextUri[] uris;
    @NotNull
    @Builder.Default
    PlaylistItemPosition position = PlaylistItemPosition.atEnd();

    @NotNull
    public static AddItemPayload withItemUri(@NotNull final ContextUri playableItemContextUri) {
        ContextUri[] uris = {playableItemContextUri};

        return AddItemPayload.builder()
                .uris(uris)
                .build();
    }

    @NotNull
    public static AddItemPayload atPosition(@NotNull final PlaylistItemPosition position,
                                            @NotNull final ContextUri playableItem) {
        final ContextUri[] uris = {playableItem};

        return AddItemPayload.builder()
                .uris(uris)
                .position(position)
                .build();
    }

    @NotNull
    public static AddItemPayload atPosition(@NotNull final PlaylistItemPosition position,
                                            @NotNull final ContextUri[] itemUris) {
        return builder()
                .position(position)
                .uris(itemUris)
                .build();
    }

    @NotNull
    public static AddItemPayload withItemUris(@NotNull final ContextUri... contextUris) {
        return AddItemPayload.builder()
                .uris(contextUris)
                .build();
    }

    @NotNull
    public Item[] determineItemsPosition(long playlistSize) {
        final Item[] items = new Item[uris.length];

        for (int currentIndex = 0; currentIndex < uris.length; currentIndex++) {
            final ContextUri contextUri = uris[currentIndex];

            if ( position.isEndOfPlaylist(playlistSize) ) {
                final int position = (int) (playlistSize + currentIndex);
                items[currentIndex] = new Item(contextUri, PlaylistItemPosition.at(position));
            } else {
                items[currentIndex] = new Item(contextUri, PlaylistItemPosition.at(position.value() + currentIndex));
            }
        }

        return items;
    }

    public record Item(@NotNull ContextUri contextUri, @NotNull PlaylistItemPosition position) {}
}
