package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import org.apache.commons.lang3.RandomStringUtils;

public final class PlaylistCollaboratorFaker {
    private final PlaylistCollaborator.PlaylistCollaboratorBuilder builder = PlaylistCollaborator.builder();
    private final Faker faker = Faker.instance();


    public PlaylistCollaboratorFaker() {
        final String id = RandomStringUtils.randomAlphanumeric(22);

        builder
                .id(id)
                .displayName(faker.name().username())
                .type(EntityType.USER)
                .contextUri("sonata:user:" + id);
    }


    public static PlaylistCollaboratorFaker create() {
        return new PlaylistCollaboratorFaker();
    }

    public PlaylistCollaboratorFaker withId(final String id) {
        builder.id(id);
        return this;
    }

    public PlaylistCollaboratorFaker withDisplayName(final String displayName) {
        builder.displayName(displayName);
        return this;
    }

    public PlaylistCollaboratorFaker withContextUri(final String contextUri) {
        builder.contextUri(contextUri);
        return this;
    }

    public PlaylistCollaborator get() {
        return builder.build();
    }
}
