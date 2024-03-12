package testing.asserts;

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
}
