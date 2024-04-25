package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.ItemEntity;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for managing items that can be saved to playlist.
 */
public interface ItemRepository {

    /**
     * Saves an item entity.
     *
     * @param entity The item entity to save.
     * @return A {@link Mono} emitting the saved item entity.
     */
    @NotNull
    Mono<ItemEntity> save(@NotNull ItemEntity entity);

    /**
     * Saves an item entity.
     *
     * @param entity The item entity to save.
     * @return A {@link Mono} emitting the saved item entity.
     */
    @NotNull
    Flux<ItemEntity> saveAll(@NotNull ItemEntity... entity);

    /**
     * Finds an item entity by its ID.
     *
     * @param id The ID of the item entity to find.
     * @return A Mono emitting the found item entity.
     */
    @NotNull
    Mono<ItemEntity> findById(@NotNull Long id);

    /**
     * Finds an item entity by its context URI.
     *
     * @param contextUri The context URI of the item entity to find.
     * @return A Mono emitting the found item entity.
     */
    @NotNull
    Mono<ItemEntity> findByContextUri(@NotNull String contextUri);

    /**
     * Finds an item entity by its public ID.
     *
     * @param publicId The public ID of the item entity to find.
     * @return A Mono emitting the found item entity.
     */
    @NotNull
    Mono<ItemEntity> findByPublicId(@NotNull String publicId);

    /**
     * Removes an item entity by its ID.
     *
     * @param id The ID of the item entity to remove.
     * @return A Mono emitting the removed item entity.
     */
    @NotNull
    Mono<Void> removeById(@NotNull Long id);

    /**
     * Removes all items, primary used for tests
     * @return - A Mono on complete
     */
    @NotNull
    Mono<Void> deleteAll();
}