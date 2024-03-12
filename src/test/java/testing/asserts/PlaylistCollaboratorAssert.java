package testing.asserts;

import com.odeyalo.sonata.playlists.model.EntityType;
import com.odeyalo.sonata.playlists.model.PlaylistCollaborator;
import org.assertj.core.api.AbstractAssert;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PlaylistCollaboratorAssert extends AbstractAssert<PlaylistCollaboratorAssert, PlaylistCollaborator> {

    public PlaylistCollaboratorAssert(@NotNull PlaylistCollaborator actual) {
        super(actual, PlaylistCollaboratorAssert.class);
    }

    public PlaylistCollaboratorAssert hasId(String id) {
        if ( Objects.equals(actual.getId(), id) ) {
            return this;
        }
        throw failureWithActualExpected(actual.getId(), id, "Collaborator ID mismatch");
    }

    public PlaylistCollaboratorAssert hasDisplayName(String displayName) {
        if ( Objects.equals(actual.getDisplayName(), displayName) ) {
            return this;
        }
        throw failureWithActualExpected(actual.getDisplayName(), displayName, "Display name mismatch");
    }

    public PlaylistCollaboratorAssert hasEntityType(EntityType type) {
        if ( Objects.equals(actual.getType(), type) ) {
            return this;
        }
        throw failureWithActualExpected(actual.getType(), type, "Entity type mismatch");
    }

    public PlaylistCollaboratorAssert hasContextUri(String contextUri) {
        if ( Objects.equals(actual.getContextUri(), contextUri) ) {
            return this;
        }
        throw failureWithActualExpected(actual.getContextUri(), contextUri, "Context URI mismatch");
    }
}
