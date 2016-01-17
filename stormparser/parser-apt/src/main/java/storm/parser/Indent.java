package storm.parser;

import java.util.Arrays;

/**
 * Created by Dimitry Ivanov on 22.12.2015.
 */
public class Indent {

    private final int mSpaces;
    private int mIndentations;
    private String mCachedValue;

    public Indent(int spaces) {
        mSpaces = spaces;
    }

    public Indent increment() {
        ++mIndentations;
        mCachedValue = null;
        return this;
    }

    public Indent decrement() {
        --mIndentations;
        mCachedValue = null;
        return this;
    }

    @Override
    public String toString() {

        if (mCachedValue == null) {
            final char[] chars = new char[mSpaces * mIndentations];
            Arrays.fill(chars, ' ');
            mCachedValue = new String(chars);
        }

        return mCachedValue;
    }
}
