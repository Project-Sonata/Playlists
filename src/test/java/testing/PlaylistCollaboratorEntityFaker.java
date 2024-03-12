package testing;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import org.apache.commons.lang3.RandomStringUtils;

public final class PlaylistCollaboratorEntityFaker {
    private final PlaylistCollaboratorEntity.PlaylistCollaboratorEntityBuilder builder = PlaylistCollaboratorEntity.builder();
    private final Faker faker = Faker.instance();


    public PlaylistCollaboratorEntityFaker() {
        builder
                .id(RandomStringUtils.randomAlphanumeric(22))
                .displayName(faker.name().username());
    }


    public static PlaylistCollaboratorEntityFaker create() {
        return new PlaylistCollaboratorEntityFaker();
    }

    public PlaylistCollaboratorEntityFaker id(String id) {
        builder.id(id);
        return this;
    }

    public PlaylistCollaboratorEntityFaker displayName(String displayName) {
        builder.displayName(displayName);
        return this;
    }

    public PlaylistCollaboratorEntity get() {
        return builder.build();
    }
}
