package com.odeyalo.sonata.playlists.service.generation;

import com.odeyalo.sonata.suite.brokers.events.playlist.gen.GeneratedPlaylistType;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.PlaylistImagesGeneratedEvent;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.GeneratedTrack;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistImagesGeneratedPayload;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistMetaGeneratedPayload;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistTracksGeneratedPayload;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlaylistGenerationServiceTest {

    @Test
    void shouldSetThePlaylistMetadata() {
        // given
        final PlaylistImagesGeneratedEvent event = new PlaylistImagesGeneratedEvent(
                new PlaylistImagesGeneratedPayload(new PlaylistMetaGeneratedPayload(
                        new PlaylistTracksGeneratedPayload("123", List.of(
                                new GeneratedTrack("1", 0),
                                new GeneratedTrack("2", 1),
                                new GeneratedTrack("3", 2)
                        )),
                        new PlaylistMetaGeneratedPayload.Meta("On Repeat", "Songs you love the most")
                ), List.of(new PlaylistImagesGeneratedPayload.Image("https://cdn.sonata.com/i/c/abc123"))),
                GeneratedPlaylistType.ON_REPEAT);

        final PlaylistGenerationService testable = new PlaylistGenerationService();

        // when
        testable.generate(event)
                .as(StepVerifier::create)
                .assertNext(playlist -> {
                    assertThat(playlist.getName()).isEqualTo("On Repeat");
                    assertThat(playlist.getDescription()).isEqualTo("Songs you love the most");
                })
                .verifyComplete();
    }
}