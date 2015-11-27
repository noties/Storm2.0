package storm.query;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by Dimitry Ivanov on 10.10.2015.
 */
public abstract class AbsStatementTest<S extends IStatementBuilder> extends TestCase {

    S builder;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        builder = setUpStatement();
    }

    protected abstract S setUpStatement();

    public void assertStatement(String statement) {
        assertEquals(statement, this.builder.getStatement());
    }

    public void assertArgs(String... args) {
        if (args == null
                || args.length == 0) {
            assertNull(builder.getArguments());
            return;
        }
        assertTrue(Arrays.equals(args, builder.getArguments()));
    }
}
