package testing.asserts;

import org.assertj.core.api.AbstractAssert;

/**
 * Asserts to test the ID of the entity
 */
public class IdAssert extends AbstractAssert<IdAssert, String> {

    public IdAssert(String actual) {
        super(actual, IdAssert.class);
    }
    protected IdAssert(String actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static IdAssert forId(String actual) {
        return new IdAssert(actual);
    }
}
