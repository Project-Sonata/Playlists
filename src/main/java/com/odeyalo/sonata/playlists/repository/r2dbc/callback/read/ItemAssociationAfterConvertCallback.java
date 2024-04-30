package com.odeyalo.sonata.playlists.repository.r2dbc.callback.read;

import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;
import com.odeyalo.sonata.playlists.repository.ItemRepository;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public final class ItemAssociationAfterConvertCallback implements AfterConvertCallback<PlaylistItemEntity> {
    private final ItemRepository itemRepository;

    public ItemAssociationAfterConvertCallback(@Lazy ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @NotNull
    public Publisher<PlaylistItemEntity> onAfterConvert(@NotNull final PlaylistItemEntity entity,
                                                        @NotNull final SqlIdentifier table) {

        Assert.notNull(entity.getItemId(), () -> String.format("Invalid entity has been received with null item ID. \n [%s]", entity));

        return itemRepository.findById(entity.getItemId())
                .doOnNext(entity::setItem)
                .thenReturn(entity);
    }
}
