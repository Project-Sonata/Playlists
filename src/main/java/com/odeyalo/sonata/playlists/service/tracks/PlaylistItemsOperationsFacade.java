package com.odeyalo.sonata.playlists.service.tracks;

import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.PlaylistItem;
import com.odeyalo.sonata.playlists.model.User;
import com.odeyalo.sonata.playlists.service.TargetPlaylist;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Facade for {@link PlaylistItemsOperations} that has access to currently authenticated user represented as {@link User}
 *
 * @see SecurityPolicyPlaylistItemsOperationsFacade
 */
public interface PlaylistItemsOperationsFacade {

    /**
     * Same as {@link PlaylistItemsOperations#loadPlaylistItems(TargetPlaylist, Pagination)} but with access to currently authenticated user
     *
     * @param targetPlaylist - playlist to load items from
     * @param pagination     - pagination info
     * @param user           - currently authenticated user
     * @return {@link Flux} with items from playlist
     */
    @NotNull
    Flux<PlaylistItem> loadPlaylistItems(@NotNull final TargetPlaylist targetPlaylist,
                                         @NotNull final Pagination pagination,
                                         @NotNull final User user);

    /**
     * Same as {@link PlaylistItemsOperations#addItems(TargetPlaylist, AddItemPayload, PlaylistCollaborator)} but with access to currently authenticated user
     *
     * @param targetPlaylist - playlist to add item to
     * @param addItemPayload - payload with items to add
     * @param collaborator   - represent a user that wants to add the item to playlist
     * @param user           - currently authenticated user(same as collaborator)
     * @return - empty {@link Mono} on success or {@link Mono#error(Throwable)} if exception occurred
     */
    @NotNull
    Mono<Void> addItems(@NotNull final TargetPlaylist targetPlaylist,
                        @NotNull final AddItemPayload addItemPayload,
                        @NotNull final PlaylistCollaborator collaborator,
                        @NotNull final User user);
}
