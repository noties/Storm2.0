package storm.scheme;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
class StormSchemeProviderApt implements StormSchemeProvider {

    static boolean lookup(Class<?> cl) {
        return false;
    }

    @Override
    public StormScheme provide(Class<?> cl) throws StormSchemeException {
        return null;
    }
}
