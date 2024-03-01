package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.entity.PlaylistOwnerEntity;
import com.odeyalo.sonata.playlists.model.EntityType;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlaylistOwnerEntityFaker {
    private final PlaylistOwnerEntity.PlaylistOwnerEntityBuilder builder = PlaylistOwnerEntity.builder();
    private final Faker faker = Faker.instance();

    public PlaylistOwnerEntityFaker() {
        builder.publicId(RandomStringUtils.randomAlphanumeric(22))
                .displayName(faker.name().username());
    }

    public static PlaylistOwnerEntityFaker create() {
        return new PlaylistOwnerEntityFaker();
    }

    public PlaylistOwnerEntity get() {
        return builder.build();
    }

    public PlaylistOwnerEntityFaker id(Long id) {
        builder.id(id);
        return this;
    }

    public PlaylistOwnerEntityFaker publicId(@NotNull String publicId) {
        builder.publicId(publicId);
        return this;
    }

    public PlaylistOwnerEntityFaker displayName(@Nullable String displayName) {
        builder.displayName(displayName);
        return this;
    }

    public PlaylistOwnerEntityFaker entityType(@NotNull EntityType entityType) {
        builder.entityType(entityType);
        return this;
    }
}
