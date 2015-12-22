package storm.scheme;

import java.util.Arrays;

/**
 * Created by Dimitry Ivanov on 22.12.2015.
 */
class Indent {

    private final int mSpaces;
    private int mIndentations;

    Indent(int spaces) {
        mSpaces = spaces;
    }

    Indent increment() {
        ++mIndentations;
        return this;
    }

    Indent decrement() {
        --mIndentations;
        return this;
    }

    @Override
    public String toString() {
        final char[] chars = new char[mSpaces * mIndentations];
        Arrays.fill(chars, ' ');
        return new String(chars);
    }
}
