package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistEntity;
import com.odeyalo.sonata.playlists.model.PlaylistType;
import org.apache.commons.lang3.RandomStringUtils;

public class PlaylistEntityFaker {
    private Long id;
    private String publicId;
    private String playlistName;
    private String playlistDescription;
    private PlaylistType playlistType;
    private static final Faker faker = Faker.instance();

    public PlaylistEntityFaker(Long id, String publicId, String playlistName, String playlistDescription, PlaylistType playlistType) {
        this.id = id;
        this.publicId = publicId;
        this.playlistName = playlistName;
        this.playlistDescription = playlistDescription;
        this.playlistType = playlistType;
    }

    public static PlaylistEntityFaker create() {
        return builder()
                .id(faker.random().nextLong())
                .playlistName(faker.music().instrument())
                .playlistDescription(faker.weather().description())
                .playlistType(faker.options().option(PlaylistType.class))
                .publicId(RandomStringUtils.randomAlphanumeric(22))
                .build();
    }

    public static PlaylistEntityFaker createWithNoId() {
        return create().setId(null);
    }

    protected static PlaylistEntityFaker.PlaylistEntityFakerBuilder builder() {
        return new PlaylistEntityFakerBuilder();
    }

    public R2dbcPlaylistEntity asR2dbcEntity() {
        return R2dbcPlaylistEntity.builder()
                .id(id)
                .publicId(publicId)
                .playlistName(playlistName)
                .playlistDescription(playlistDescription)
                .playlistType(playlistType)
                .build();
    }

    public PlaylistEntityFaker setId(Long id) {
        this.id = id;
        return this;
    }

    public PlaylistEntityFaker setPublicId(String publicId) {
        this.publicId = publicId;
        return this;
    }

    public PlaylistEntityFaker setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
        return this;
    }

    public PlaylistEntityFaker setPlaylistDescription(String playlistDescription) {
        this.playlistDescription = playlistDescription;
        return this;
    }

    public PlaylistEntityFaker setPlaylistType(PlaylistType playlistType) {
        this.playlistType = playlistType;
        return this;
    }

    public static class PlaylistEntityFakerBuilder {
        private Long id;
        private String publicId;
        private String playlistName;
        private String playlistDescription;
        private PlaylistType playlistType;

        PlaylistEntityFakerBuilder() {
        }

        public PlaylistEntityFakerBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PlaylistEntityFakerBuilder publicId(String publicId) {
            this.publicId = publicId;
            return this;
        }

        public PlaylistEntityFakerBuilder playlistName(String playlistName) {
            this.playlistName = playlistName;
            return this;
        }

        public PlaylistEntityFakerBuilder playlistDescription(String playlistDescription) {
            this.playlistDescription = playlistDescription;
            return this;
        }

        public PlaylistEntityFakerBuilder playlistType(PlaylistType playlistType) {
            this.playlistType = playlistType;
            return this;
        }

        public PlaylistEntityFaker build() {
            return new PlaylistEntityFaker(id, publicId, playlistName, playlistDescription, playlistType);
        }

        public String toString() {
            return "PlaylistEntityFaker.PlaylistEntityFakerBuilder(id=" + this.id + ", publicId=" + this.publicId + ", playlistName=" + this.playlistName + ", playlistDescription=" + this.playlistDescription + ", playlistType=" + this.playlistType + ")";
        }
    }
}
