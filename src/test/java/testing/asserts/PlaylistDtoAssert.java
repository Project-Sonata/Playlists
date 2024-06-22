package testing.asserts;

import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.StringAssert;

/**
 * Asserts for PlaylistDto class
 */
public class PlaylistDtoAssert extends AbstractAssert<PlaylistDtoAssert, PlaylistDto> {

    public PlaylistDtoAssert(PlaylistDto actual) {
        super(actual, PlaylistDtoAssert.class);
    }

    protected PlaylistDtoAssert(PlaylistDto actual, Class<?> self) {
        super(actual, self);
    }

    public static PlaylistDtoAssert forPlaylist(PlaylistDto actual) {
        return new PlaylistDtoAssert(actual);
    }

    public IdAssert id() {
        return new IdAssert(actual.getId());
    }

    public EntityTypeAssert entityType() {
        return new EntityTypeAssert(actual.getType());
    }

    public StringAssert name() {
        return new StringAssert(actual.getName());
    }

    public StringAssert description() {
        return new StringAssert(actual.getDescription());
    }

    public PlaylistTypeAssert playlistType() {
        return new PlaylistTypeAssert(actual.getPlaylistType());
    }

    public ImagesDtoAssert images() {
        return new ImagesDtoAssert(actual.getImages());
    }

    public PlaylistOwnerDtoAssert owner() {
        return new PlaylistOwnerDtoAssert(actual.getOwner());
    }

    public ContextUriAssert contextUri() {
        return new ContextUriAssert(actual.getContextUri());
    }
}