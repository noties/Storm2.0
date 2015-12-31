package storm.scheme;

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
}
