package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import org.apache.commons.lang3.RandomStringUtils;

public class PlaylistEntityFaker {
    private final PlaylistEntity.PlaylistEntityBuilder builder = PlaylistEntity.builder();
    private final Faker faker = Faker.instance();

    public PlaylistEntityFaker() {
        String publicId = RandomStringUtils.randomAlphanumeric(22);
        builder
                .id(faker.random().nextLong())
                .playlistName(faker.music().instrument())
                .playlistDescription(faker.weather().description())
                .playlistType(faker.options().option(PlaylistType.class))
                .publicId(publicId)
                .contextUri("sonata:playlist:" + publicId)
                .playlistOwner(PlaylistOwnerEntityFaker.create().get())
                .build();
    }

    public static PlaylistEntityFaker create() {
        return new PlaylistEntityFaker();
    }

    public static PlaylistEntityFaker createWithNoId() {
        return create().setId(null);
    }

    public PlaylistEntity get() {
        return builder.build();
    }

    public PlaylistEntity asR2dbcEntity() {
        return builder.build();
    }

    public PlaylistEntityFaker setId(Long id) {
        builder.id(id);
        return this;
    }

    public PlaylistEntityFaker setOwnerId(String ownerId) {
        builder.playlistOwner(PlaylistOwnerEntityFaker.create().publicId(ownerId).get());
        return this;
    }

    public PlaylistEntityFaker setPublicId(String publicId) {
        builder.publicId(publicId);
        builder.contextUri("sonata:playlist:" + publicId);
        return this;
    }

    public PlaylistEntityFaker setPlaylistName(String playlistName) {
        builder.playlistName(playlistName);
        return this;
    }

    public PlaylistEntityFaker setPlaylistDescription(String playlistDescription) {
        builder.playlistDescription(playlistDescription);
        return this;
    }

    public PlaylistEntityFaker setPlaylistType(PlaylistType playlistType) {
        builder.playlistType(playlistType);
        return this;
    }

    public PlaylistEntityFaker setContextUri(final String contextUri) {
        builder.contextUri(contextUri);
        return this;
    }
}
