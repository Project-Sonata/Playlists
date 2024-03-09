package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.PlayableItem;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import testing.MockPlayableItem;

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
}