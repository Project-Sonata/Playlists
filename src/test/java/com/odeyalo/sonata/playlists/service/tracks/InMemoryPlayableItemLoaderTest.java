package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class InMemoryPlayableItemLoaderTest {

    @Test
    void shouldLoadExistingItemByContextUri() {
        PlayableItem item = MockPlayableItem.create("test");
        InMemoryPlayableItemLoader testable = new InMemoryPlayableItemLoader(item);

        testable.loadItem("test")
                .as(StepVerifier::create)
                .expectNext(item)
                .verifyComplete();
    }


    @Test
    void shouldReturnEmptyIfNotExist() {
        InMemoryPlayableItemLoader testable = new InMemoryPlayableItemLoader();

        testable.loadItem("test")
                .as(StepVerifier::create)
                .verifyComplete();
    }


    private record MockPlayableItem(String contextUri) implements PlayableItem {

        public static PlayableItem create(@NotNull String contextUri) {
            return new MockPlayableItem(contextUri);
        }

        @Override
        @NotNull
        public String getContextUri() {
            return contextUri;
        }
    }
}