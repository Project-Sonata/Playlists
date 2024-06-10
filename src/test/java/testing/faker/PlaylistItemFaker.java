package testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.playlists.model.PlayableItem;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import com.odeyalo.sonata.playlists.model.PlaylistItem;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public final class PlaylistItemFaker {
    private final PlaylistItem.PlaylistItemBuilder builder = PlaylistItem.builder();
    private final Faker faker = Faker.instance();

    public PlaylistItemFaker() {
        PlayableItem item = TrackPlayableItemFaker.create().get();
        PlaylistCollaborator collaborator = PlaylistCollaboratorFaker.create().get();
        Instant addedAt = faker.date().past(1, TimeUnit.HOURS).toInstant();

        builder.addedAt(addedAt)
                .item(item)
                .addedBy(collaborator)
                .build();
    }

    public static PlaylistItemFaker create() {
        return new PlaylistItemFaker();
    }

    public PlaylistItemFaker withIndex(int index) {
        builder.index(index);
        return this;
    }

    public PlaylistItemFaker withAddedAt(Instant addedAt) {
        builder.addedAt(addedAt);
        return this;
    }

    public PlaylistItem get() {
        return builder.build();
    }
}
