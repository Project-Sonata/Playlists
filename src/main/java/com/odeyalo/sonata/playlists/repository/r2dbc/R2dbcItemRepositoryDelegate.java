package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.ItemEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * A simple support interface that used to work with R2DBC and used as delegate by {@link com.odeyalo.sonata.playlists.repository.R2dbcItemRepository}
 */
public interface R2dbcItemRepositoryDelegate extends R2dbcRepository<ItemEntity, Long> {
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
     */
    @NotNull
    Mono<Void> removeById(@NotNull Long id);

}
