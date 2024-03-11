package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Serves the playable items in memory using {@link ConcurrentMap}, primary used in tests and dev environment.
 */
public final class InMemoryPlayableItemLoader implements PlayableItemLoader {
    private final Map<String, PlayableItem> cache;

    public InMemoryPlayableItemLoader(PlayableItem... items) {
        this.cache = Arrays.stream(items).collect(
                Collectors.toConcurrentMap(PlayableItem::getContextUri, Function.identity())
        );
    }

    public InMemoryPlayableItemLoader(List<PlayableItem> items) {
        this.cache = items.stream().collect(
                Collectors.toConcurrentMap(PlayableItem::getContextUri, Function.identity())
        );
    }

    @Override
    @NotNull
    public Mono<PlayableItem> loadItem(@NotNull final String contextUri) {
        return Mono.justOrEmpty(cache.get(contextUri));
    }
}
