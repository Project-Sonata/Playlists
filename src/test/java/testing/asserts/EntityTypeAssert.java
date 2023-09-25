package testing.asserts;

import com.odeyalo.sonata.playlists.model.EntityType;
import org.assertj.core.api.AbstractAssert;

import static com.odeyalo.sonata.playlists.model.EntityType.PLAYLIST;
import static com.odeyalo.sonata.playlists.model.EntityType.USER;

/**
 * Asserts for {@link EntityType}
 */
public class EntityTypeAssert extends AbstractAssert<EntityTypeAssert, EntityType> {
    public EntityTypeAssert(EntityType actual) {
        super(actual, EntityTypeAssert.class);
    }

    public static EntityTypeAssert forEntityType(EntityType actual) {
        return new EntityTypeAssert(actual);
    }

    public EntityTypeAssert playlist() {
        return entityTypeAssert(PLAYLIST);
    }

    public EntityTypeAssert user() {
        return entityTypeAssert(USER);
    }

    protected EntityTypeAssert entityTypeAssert(EntityType expected) {
        if (actual != expected) {
            throw failureWithActualExpected(actual, expected, "Expected type to be %s", expected);
        }
        return this;
    }
}
