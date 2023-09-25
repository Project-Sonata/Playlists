package testing.asserts;

import com.odeyalo.sonata.playlists.dto.PlaylistOwnerDto;
import org.assertj.core.api.AbstractAssert;

public class PlaylistOwnerDtoAssert extends AbstractAssert<PlaylistOwnerDtoAssert, PlaylistOwnerDto> {

    public PlaylistOwnerDtoAssert(PlaylistOwnerDto actual) {
        super(actual, PlaylistOwnerDtoAssert.class);
    }

    public IdAssert id() {
        return new IdAssert(actual.getId());
    }

    public DisplayNameAssert displayName() {
        return new DisplayNameAssert(actual.getDisplayName());
    }

    public EntityTypeAssert entityType() {
        return new EntityTypeAssert(actual.getEntityType());
    }
}