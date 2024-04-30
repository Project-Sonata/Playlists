package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.repository.PlaylistCollaboratorRepository;
import com.odeyalo.sonata.playlists.repository.r2dbc.delegate.R2dbcPlaylistCollaboratorRepositoryDelegate;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public final class R2dbcPlaylistCollaboratorRepository implements PlaylistCollaboratorRepository {
    private final R2dbcPlaylistCollaboratorRepositoryDelegate delegate;

    public R2dbcPlaylistCollaboratorRepository(R2dbcPlaylistCollaboratorRepositoryDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull Mono<PlaylistCollaboratorEntity> save(@NotNull PlaylistCollaboratorEntity collaborator) {
        return delegate.save(collaborator);
    }

    @Override
    public @NotNull Mono<PlaylistCollaboratorEntity> findById(long id) {
        return delegate.findById(id);
    }

    @Override
    public @NotNull Mono<PlaylistCollaboratorEntity> findByPublicId(@NotNull String id) {
        return delegate.findByPublicId(id);
    }

    @Override
    public @NotNull Mono<PlaylistCollaboratorEntity> findByContextUri(@NotNull String contextUri) {
        return delegate.findByContextUri(contextUri);
    }

    @Override
    public @NotNull Mono<Void> clear() {
        return delegate.deleteAll();
    }
}
