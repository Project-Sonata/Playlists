package testing;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.entity.PlaylistCollaboratorEntity;
import com.odeyalo.sonata.playlists.model.EntityType;
import org.apache.commons.lang3.RandomStringUtils;

public final class PlaylistCollaboratorEntityFaker {
    private final PlaylistCollaboratorEntity.PlaylistCollaboratorEntityBuilder builder = PlaylistCollaboratorEntity.builder();
    private final Faker faker = Faker.instance();


    public PlaylistCollaboratorEntityFaker() {
        String id = RandomStringUtils.randomAlphanumeric(22);
        builder
                .publicId(id)
                .displayName(faker.name().username())
                .type(EntityType.USER)
                .contextUri("sonata:user:" + id);
    }


    public static PlaylistCollaboratorEntityFaker create() {
        return new PlaylistCollaboratorEntityFaker();
    }

    public static PlaylistCollaboratorEntityFaker createWithoutId() {
        return new PlaylistCollaboratorEntityFaker()
                .id(null);
    }

    public PlaylistCollaboratorEntityFaker id(Long id) {
        builder.id(id);
        return this;
    }

    public PlaylistCollaboratorEntityFaker publicId(String publicId) {
        builder.publicId(publicId);
        return this;
    }

    public PlaylistCollaboratorEntityFaker displayName(String displayName) {
        builder.displayName(displayName);
        return this;
    }

    public PlaylistCollaboratorEntity get() {
        return builder.build();
    }

    public PlaylistCollaboratorEntityFaker withContextUri(String contextUri) {
        builder.contextUri(contextUri);
        return this;
    }

    public PlaylistCollaboratorEntityFaker withPublicId(String publicId) {
        builder.publicId(publicId);
        return this;
    }
}
