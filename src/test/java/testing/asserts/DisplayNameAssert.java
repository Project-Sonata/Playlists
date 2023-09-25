package testing.asserts;

import org.assertj.core.api.StringAssert;

public class DisplayNameAssert extends StringAssert {

    public DisplayNameAssert(String actual) {
        super(actual);
    }

    public DisplayNameAssert isUnknown() {
        if (actual != null) {
            throw failure("Expected owner playlist display name to be unknown(null)");
        }
        return this;
    }
}
