package testing;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistItemEntity;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public final class PlaylistItemEntityFaker {
    private final PlaylistItemEntity.PlaylistItemEntityBuilder builder = PlaylistItemEntity.builder();
    private final Faker faker = Faker.instance();

    public PlaylistItemEntityFaker(String playlistId) {
        ItemEntity item = ItemEntityFaker.create().get();

        Instant addedAt = faker.date().past(1, TimeUnit.HOURS).toInstant();
        builder.addedAt(addedAt)
                .playlistId(playlistId)
                .id(faker.random().nextLong(10000))
                .item(item)
                .build();
    }

    public static PlaylistItemEntityFaker create(String playlistId) {
        return new PlaylistItemEntityFaker(playlistId);
    }

    public PlaylistItemEntity get() {
        return builder.build();
    }
}
