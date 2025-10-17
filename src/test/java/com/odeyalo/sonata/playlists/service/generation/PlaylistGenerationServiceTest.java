package com.odeyalo.sonata.playlists.service.generation;

import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.GeneratedPlaylistType;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.PlaylistImagesGeneratedEvent;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.GeneratedTrack;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistImagesGeneratedPayload;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistMetaGeneratedPayload;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistTracksGeneratedPayload;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Instant;
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
        GeneratedPlaylist generatedPlaylist = testable.generate(event).block();
        assertThat(generatedPlaylist).isNotNull();

        final Playlist playlistInfo = generatedPlaylist.meta();

        assertThat(playlistInfo.getName()).isEqualTo("On Repeat");
        assertThat(playlistInfo.getDescription()).isEqualTo("Songs you love the most");
        assertThat(playlistInfo.getPlaylistType()).isEqualTo(PlaylistType.PUBLIC);
        assertThat(playlistInfo.getContextUri().asString()).isEqualTo("sonata:playlist:" + playlistInfo.getId().value());
    }

    @Test
    void shouldSetThePlaylistImages() {
        // given
        final PlaylistImagesGeneratedEvent event = new PlaylistImagesGeneratedEvent(
                new PlaylistImagesGeneratedPayload(new PlaylistMetaGeneratedPayload(
                        new PlaylistTracksGeneratedPayload("123", List.of(
                                new GeneratedTrack("1", 0),
                                new GeneratedTrack("2", 1),
                                new GeneratedTrack("3", 2)
                        )),
                        new PlaylistMetaGeneratedPayload.Meta("On Repeat", "Songs you love the most")
                ), List.of(
                        new PlaylistImagesGeneratedPayload.Image("https://cdn.sonata.com/i/c/abc123", 50, 50),
                        new PlaylistImagesGeneratedPayload.Image("https://cdn.sonata.com/i/c/abc124", 300, 350),
                        new PlaylistImagesGeneratedPayload.Image("https://cdn.sonata.com/i/c/abc125", 600, 600)
                )), GeneratedPlaylistType.ON_REPEAT);

        final PlaylistGenerationService testable = new PlaylistGenerationService();

        // when
        GeneratedPlaylist generatedPlaylist = testable.generate(event).block();
        assertThat(generatedPlaylist).isNotNull();

        assertThat(generatedPlaylist.meta().getImages()).containsExactlyInAnyOrder(
                Image.of("https://cdn.sonata.com/i/c/abc123", 50, 50),
                Image.of("https://cdn.sonata.com/i/c/abc124", 350, 300),
                Image.of("https://cdn.sonata.com/i/c/abc125", 600, 600)
        );
    }

    @Test
    void shouldSetThePlaylistOwner() {
        // given
        final PlaylistImagesGeneratedEvent event = new PlaylistImagesGeneratedEvent(
                new PlaylistImagesGeneratedPayload(new PlaylistMetaGeneratedPayload(
                        new PlaylistTracksGeneratedPayload("123", List.of(
                                new GeneratedTrack("1", 0),
                                new GeneratedTrack("2", 1),
                                new GeneratedTrack("3", 2)
                        )),
                        new PlaylistMetaGeneratedPayload.Meta("On Repeat", "Songs you love the most")
                ), List.of(
                        new PlaylistImagesGeneratedPayload.Image("https://cdn.sonata.com/i/c/abc123", 50, 50)
                )), GeneratedPlaylistType.ON_REPEAT);

        final PlaylistGenerationService testable = new PlaylistGenerationService();

        // when
        GeneratedPlaylist generatedPlaylist = testable.generate(event).block();
        assertThat(generatedPlaylist).isNotNull();

        final PlaylistOwner playlistOwner = generatedPlaylist.meta().getPlaylistOwner();

        assertThat(playlistOwner.getId()).isEqualTo("sonata");
        assertThat(playlistOwner.getDisplayName()).isEqualTo("Sonata");
        assertThat(playlistOwner.getEntityType()).isEqualTo(EntityType.USER);
    }

    @Test
    void shouldSetTheTracks() {
        // given
        final PlaylistImagesGeneratedEvent event = new PlaylistImagesGeneratedEvent(
                new PlaylistImagesGeneratedPayload(new PlaylistMetaGeneratedPayload(
                        new PlaylistTracksGeneratedPayload("123", List.of(
                                new GeneratedTrack("1", 0),
                                new GeneratedTrack("2", 1),
                                new GeneratedTrack("3", 2)
                        )),
                        new PlaylistMetaGeneratedPayload.Meta("On Repeat", "Songs you love the most")
                ), List.of(
                        new PlaylistImagesGeneratedPayload.Image("https://cdn.sonata.com/i/c/abc123", 50, 50)
                )), GeneratedPlaylistType.ON_REPEAT);

        final PlaylistGenerationService testable = new PlaylistGenerationService();

        // when
        GeneratedPlaylist generatedPlaylist = testable.generate(event).block();

        // then
        assertThat(generatedPlaylist).isNotNull();

        final List<GeneratedPlaylist.Item> tracks = generatedPlaylist.tracks();
        assertThat(tracks).containsExactlyInAnyOrder(
                new GeneratedPlaylist.Item("1", PlayableItemType.TRACK, 0),
                new GeneratedPlaylist.Item("2", PlayableItemType.TRACK, 1),
                new GeneratedPlaylist.Item("3", PlayableItemType.TRACK, 2)
        );
    }

    @Test
    void shouldSetUserForWhichTrackWasGenerated() {
        // given
        final PlaylistImagesGeneratedEvent event = new PlaylistImagesGeneratedEvent(
                new PlaylistImagesGeneratedPayload(new PlaylistMetaGeneratedPayload(
                        new PlaylistTracksGeneratedPayload("123", List.of(
                                new GeneratedTrack("1", 0),
                                new GeneratedTrack("2", 1),
                                new GeneratedTrack("3", 2)
                        )),
                        new PlaylistMetaGeneratedPayload.Meta("On Repeat", "Songs you love the most")
                ), List.of(
                        new PlaylistImagesGeneratedPayload.Image("https://cdn.sonata.com/i/c/abc123", 50, 50)
                )), GeneratedPlaylistType.ON_REPEAT);

        final PlaylistGenerationService testable = new PlaylistGenerationService();

        // when
        GeneratedPlaylist generatedPlaylist = testable.generate(event).block();

        // then
        assertThat(generatedPlaylist).isNotNull();

        assertThat(generatedPlaylist.generatedFor()).isEqualTo("123");
    }
}