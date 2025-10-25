package com.odeyalo.sonata.playlists.service.generation;

import com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.model.PlaylistId;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import com.odeyalo.sonata.playlists.repository.GeneratedPlaylistRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistItemsRepository;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.support.pagination.OffsetBasedPageRequest;
import com.odeyalo.sonata.suite.brokers.events.SonataEvent;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.GeneratedPlaylistType;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.PlaylistImagesGeneratedEvent;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.GeneratedTrack;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistImagesGeneratedPayload;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistMetaGeneratedPayload;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistTracksGeneratedPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public final class PlaylistGenerationManagerTest {

    @Autowired
    PlaylistGenerationManager testable;

    @Autowired
    GeneratedPlaylistRepository generatedPlaylistRepository;

    @Autowired
    PlaylistItemsRepository playlistItemsRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    @Test
    void shouldCreatePlaylist() {

        final var event = new PlaylistImagesGeneratedEvent(new PlaylistImagesGeneratedPayload(
                new PlaylistMetaGeneratedPayload(
                        new PlaylistTracksGeneratedPayload("u123", List.of(
                                new GeneratedTrack("1", 0),
                                new GeneratedTrack("2", 1),
                                new GeneratedTrack("3", 2)
                        )), new PlaylistMetaGeneratedPayload.Meta("Cool name", "Cool desc")),
                List.of(
                        new PlaylistImagesGeneratedPayload.Image("https://cdn.sonata.com/i/c/123")
                )
        ), GeneratedPlaylistType.ON_REPEAT);

        testable.handle(event).block();

        GeneratedPlaylistEntity generatedPlaylist = generatedPlaylistRepository.findByGeneratedFor("u123").blockFirst();

        assertThat(generatedPlaylist).isNotNull();
        assertThat(generatedPlaylist.getGeneratedPlaylistType()).isEqualTo(GeneratedPlaylistType.ON_REPEAT);

        String playlistId = generatedPlaylist.getPlaylistId();

        PlaylistEntity playlist = playlistRepository.findByPublicId(PlaylistId.of(playlistId)).block();

        assertThat(playlist).isNotNull();
        assertThat(playlist.getPlaylistName()).isEqualTo("Cool name");
        assertThat(playlist.getPlaylistDescription()).isEqualTo("Cool desc");
        assertThat(playlist.getPlaylistType()).isEqualTo(PlaylistType.PUBLIC);

        List<PlaylistItemEntity> items = playlistItemsRepository.findAllByPlaylistId(playlistId, new OffsetBasedPageRequest(0, 100))
                .sort(Comparator.comparingInt(PlaylistItemEntity::getIndex))
                .collectList()
                .block();

        assertThat(items).isNotEmpty();

        assertThat(items).map(e -> e.getItem().getPublicId()).containsExactly(
                "1", "2", "3"
        );
    }

}
