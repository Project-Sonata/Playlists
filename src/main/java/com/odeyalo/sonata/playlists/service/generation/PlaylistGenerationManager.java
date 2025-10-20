package com.odeyalo.sonata.playlists.service.generation;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.entity.GeneratedPlaylistEntity;
import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.playlists.repository.GeneratedPlaylistRepository;
import com.odeyalo.sonata.playlists.service.PlaylistService;
import com.odeyalo.sonata.playlists.service.tracks.PlaylistItemsService;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.PlaylistImagesGeneratedEvent;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistTracksGeneratedPayload;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
public class PlaylistGenerationManager {
    private final PlaylistGenerationService generationService;
    private final PlaylistItemsService playlistItemsService;
    private final PlaylistService playlistService;
    private final GeneratedPlaylistRepository generatedPlaylistRepository;

    private final Logger logger = LoggerFactory.getLogger(PlaylistGenerationManager.class);

    private static final PlaylistCollaborator SONATA_COLLABORATOR = PlaylistCollaborator.builder()
            .id("sonata")
            .displayName("Sonata")
            .contextUri("sonata:user:sonata")
            .type(EntityType.USER)
            .build();

    public PlaylistGenerationManager(final PlaylistGenerationService generationService,
                                     final PlaylistService playlistService,
                                     final PlaylistItemsService playlistItemsService, final GeneratedPlaylistRepository generatedPlaylistRepository) {
        this.generationService = generationService;
        this.playlistItemsService = playlistItemsService;
        this.playlistService = playlistService;
        this.generatedPlaylistRepository = generatedPlaylistRepository;
    }

    @NotNull
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Mono<Void> handle(@NotNull final PlaylistImagesGeneratedEvent event) {
        final PlaylistTracksGeneratedPayload parentEvent = event.getBody().getParent().getParent();
        logger.info("Starting playlist generation for user {} with type: {}", parentEvent.getUserId(), event.getType());

        return generationService.generate(event)
                .flatMap(generatedPlaylist -> {
                    return playlistService.save(generatedPlaylist.meta())
                            .flatMap(playlist -> {
                                final List<SimplePlaylistItem> items = getTracks(generatedPlaylist, playlist);

                                return playlistItemsService.insertAll(items)
                                        .then(Mono.defer(() -> generatedPlaylistRepository.save(
                                                GeneratedPlaylistEntity.builder()
                                                        .userId(event.getBody().getParent().getParent().getUserId())
                                                        .playlistId(playlist.getId().value())
                                                        .generatedAt(Instant.now())
                                                        .generatedPlaylistType(event.getType())
                                                        .build()
                                        )))
                                        .then();
                            });
                });
    }

    @NotNull
    private static List<SimplePlaylistItem> getTracks(final GeneratedPlaylist generatedPlaylist,
                                                      final Playlist playlist) {
        List<GeneratedPlaylist.Item> tracks = generatedPlaylist.tracks();

        return tracks.stream().map(item -> new SimplePlaylistItem(
                playlist.getId(),
                SONATA_COLLABORATOR,
                ContextUri.forTrack(item.id()),
                PlaylistItemPosition.at(item.index())
        )).toList();
    }
}
