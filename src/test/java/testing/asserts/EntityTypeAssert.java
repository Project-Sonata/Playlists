package testing.asserts;

import com.odeyalo.sonata.playlists.model.EntityType;
import org.assertj.core.api.AbstractAssert;

import static com.odeyalo.sonata.playlists.model.EntityType.PLAYLIST;

/**
 * Asserts for {@link EntityType}
 */
public class EntityTypeAssert extends AbstractAssert<EntityTypeAssert, EntityType> {
    public EntityTypeAssert(EntityType actual) {
        super(actual, EntityTypeAssert.class);
    }
    protected EntityTypeAssert(EntityType actual, Class<?> self) {
        super(actual, self);
    }

    public static EntityTypeAssert forEntityType(EntityType actual) {
        return new EntityTypeAssert(actual);
    }

    public EntityTypeAssert playlist() {
        if (actual != PLAYLIST) {
            throw failureWithActualExpected(actual, PLAYLIST, "Expected type to be PLAYLIST");
        }
        return this;
    }
}
