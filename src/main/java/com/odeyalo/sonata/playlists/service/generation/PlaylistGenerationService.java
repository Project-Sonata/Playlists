package com.odeyalo.sonata.playlists.service.generation;

import com.odeyalo.sonata.playlists.model.*;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.PlaylistImagesGeneratedEvent;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.GeneratedTrack;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistImagesGeneratedPayload;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.PlaylistMetaGeneratedPayload;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public final class PlaylistGenerationService {

    private static final PlaylistOwner SONATA_ACCOUNT = PlaylistOwner.builder()
            .id("sonata")
            .displayName("Sonata")
            .entityType(EntityType.USER)
            .build();

    @NotNull
    public Mono<GeneratedPlaylist> generate(@NotNull final PlaylistImagesGeneratedEvent event) {

        final PlaylistImagesGeneratedPayload body = event.getBody();


        return Mono.just(
                new GeneratedPlaylist(
                        baseInfoPlaylist(body),
                        getPlaylistItems(body)
                ));
    }

    @NotNull
    private static Playlist baseInfoPlaylist(@NotNull final PlaylistImagesGeneratedPayload body) {
        final Playlist.PlaylistBuilder playlistBuilder = Playlist.builder();
        final PlaylistMetaGeneratedPayload meta = body.getParent();

        final PlaylistId playlistId = PlaylistId.random();

        final List<Image> images = body.getImages()
                .stream()
                .map(image -> Image.of(image.getUrl(), image.getWidth(), image.getHeight()))
                .toList();

        return playlistBuilder
                .id(playlistId)
                .name(meta.getMeta().getName())
                .description(meta.getMeta().getDescription())
                .contextUri(playlistId.asContextUri())
                .playlistType(PlaylistType.PUBLIC)
                .images(Images.of(images))
                .playlistOwner(SONATA_ACCOUNT)
                .build();
    }

    @NotNull
    private static List<GeneratedPlaylist.Item> getPlaylistItems(@NotNull final PlaylistImagesGeneratedPayload body) {
        final List<GeneratedTrack> tracks = body.getParent().getParent().getTracks();

        return tracks.stream().map(track -> new GeneratedPlaylist.Item(
                track.getTrackId(), PlayableItemType.TRACK, track.getIndex()
        )).toList();
    }
}
