package storm.scheme;

import java.lang.reflect.Field;

import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
class StormSchemeProviderRuntime implements StormSchemeProvider {

    private static final StormSchemeParser PARSER = new StormSchemeParser();
    private static final StormSchemeTypeRuntime TYPE_RUNTIME = new StormSchemeTypeRuntime();

    @Override
    public StormScheme provide(Class<?> cl) throws StormSchemeException {

        final StormSchemeTable table = PARSER.parseTable(cl, TYPE_RUNTIME);

        return new StormSchemeStatementsGenerator(table);
    }

    static String getTableName(Class<?> cl) throws StormSchemeException {
        return PARSER.getTableName(TYPE_RUNTIME, cl);
    }

    static boolean isFieldShouldBeParsed(Field field) {
        return TYPE_RUNTIME.shouldParseElement(field);
    }
//
    static StormType parseType(Field f) throws StormSchemeException {
        return PARSER.parseType(TYPE_RUNTIME, f.getDeclaringClass(), f);
    }
}
