package testing.asserts;

import org.assertj.core.api.AbstractAssert;

public final class ContextUriAssert extends AbstractAssert<ContextUriAssert, String> {

    public ContextUriAssert(final String contextUri) {
        super(contextUri, ContextUriAssert.class);
    }
}
