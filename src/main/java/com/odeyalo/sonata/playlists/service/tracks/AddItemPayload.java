package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.model.PlaylistItemPosition;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

@Value
@Builder
public class AddItemPayload {
    @NotNull
    ContextUri[] contextUris;
    @NotNull
    @Builder.Default
    PlaylistItemPosition startPosition = PlaylistItemPosition.atEnd();

    @NotNull
    public static AddItemPayload withItemUri(@NotNull final ContextUri playableItemContextUri) {
        ContextUri[] uris = {playableItemContextUri};

        return AddItemPayload.builder()
                .contextUris(uris)
                .build();
    }

    @NotNull
    public static AddItemPayload fromPosition(@NotNull final PlaylistItemPosition startPosition,
                                              @NotNull final ContextUri playableItem) {
        final ContextUri[] uris = {playableItem};

        return AddItemPayload.builder()
                .contextUris(uris)
                .startPosition(startPosition)
                .build();
    }

    @NotNull
    public static AddItemPayload fromPosition(@NotNull final PlaylistItemPosition position,
                                              @NotNull final ContextUri[] itemUris) {
        return builder()
                .startPosition(position)
                .contextUris(itemUris)
                .build();
    }

    @NotNull
    public static AddItemPayload withItemUris(@NotNull final ContextUri... contextUris) {
        return AddItemPayload.builder()
                .contextUris(contextUris)
                .build();
    }

    @NotNull
    public Item[] determineItemsPosition(long playlistSize) {
        return IntStream.range(0, contextUris.length)
                .mapToObj(index -> calculateItemPosition(playlistSize, index))
                .toArray(Item[]::new);
    }

    @NotNull
    private Item calculateItemPosition(final long playlistSize, final int index) {
        final ContextUri contextUri = contextUris[index];

        if ( startPosition.isEndOfPlaylist(playlistSize) ) {
            return Item.of(contextUri, (int) (playlistSize + index));
        }

        return Item.of(contextUri, startPosition.incrementBy(index));
    }

    public record Item(@NotNull ContextUri contextUri, @NotNull PlaylistItemPosition position) {
        @NotNull
        public static Item of(ContextUri itemUri, int pos) {
            return new Item(itemUri, PlaylistItemPosition.at(pos));
        }
        @NotNull
        public static Item of(ContextUri itemUri, PlaylistItemPosition pos) {
            return new Item(itemUri, pos);
        }
    }
}
