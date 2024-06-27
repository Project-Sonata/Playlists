package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.repository.PlaylistRepository;
import com.odeyalo.sonata.playlists.repository.r2dbc.delegate.R2dbcPlaylistRepositoryDelegate;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * {@link PlaylistRepository} implementation that saves data using R2DBC
 *
 * @see PlaylistRepository for furher information
 */
@Component
public final class R2dbcPlaylistRepository implements PlaylistRepository {
    private final R2dbcPlaylistRepositoryDelegate playlistRepositoryDelegate;

    public R2dbcPlaylistRepository(R2dbcPlaylistRepositoryDelegate playlistRepositoryDelegate) {
        this.playlistRepositoryDelegate = playlistRepositoryDelegate;
    }

    @Override
    @NotNull
    public Mono<PlaylistEntity> save(@NotNull final PlaylistEntity playlist) {
        return playlistRepositoryDelegate.save(playlist);
    }

    @Override
    @NotNull
    public Mono<PlaylistEntity> findByPublicId(@NotNull final String publicId) {
        return playlistRepositoryDelegate.findByPublicId(publicId);
    }

    @Override
    @NotNull
    public Mono<Void> clear() {
        return playlistRepositoryDelegate.deleteAll();
    }

}
