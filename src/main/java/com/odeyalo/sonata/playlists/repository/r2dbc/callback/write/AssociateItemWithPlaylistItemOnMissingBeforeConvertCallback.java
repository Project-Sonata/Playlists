package com.odeyalo.sonata.playlists.repository.r2dbc.callback.write;

import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.repository.ItemRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Saves a {@link ItemEntity} to repository if item does not exist and associate it with a playlist item
 */
@Component
public final class AssociateItemWithPlaylistItemOnMissingBeforeConvertCallback implements BeforeConvertCallback<PlaylistItemEntity> {
    private final ItemRepository itemRepository;

    public AssociateItemWithPlaylistItemOnMissingBeforeConvertCallback(@Lazy ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @NotNull
    public Publisher<PlaylistItemEntity> onBeforeConvert(@NotNull final PlaylistItemEntity entity,
                                                         @NotNull final SqlIdentifier table) {
        Mono<ItemEntity> saveItem = Mono.defer(() -> itemRepository.save(entity.getItem()));

        return itemRepository.findByPublicId(entity.getItem().getPublicId())
                .switchIfEmpty(saveItem)
                .doOnNext(it -> entity.setItemId(it.getId()))
                .thenReturn(entity);
    }
}
