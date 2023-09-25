package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.PlaylistOwner;
import org.apache.commons.lang3.RandomStringUtils;

public class PlaylistOwnerFaker {
    private String id;
    private String displayName;

    public PlaylistOwnerFaker() {
        this.id = RandomStringUtils.randomAlphanumeric(22);
        this.displayName = RandomStringUtils.randomAlphanumeric(15);
    }

    public static PlaylistOwnerFaker create() {
        return new PlaylistOwnerFaker();
    }

    public PlaylistOwnerFaker setId(String id) {
        this.id = id;
        return this;
    }

    public PlaylistOwnerFaker setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public PlaylistOwner get() {
        return PlaylistOwner.builder()
                .id(id)
                .displayName(displayName)
                .build();
    }
}
