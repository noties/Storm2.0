package storm.scheme;

/**
 * Created by Dimitry Ivanov on 14.12.2015.
 */
public class StormSchemeFactory {

    private static final StormSchemeProvider PROVIDER_APT = new StormSchemeProviderApt();
    private static final StormSchemeProvider PROVIDER_RUNTIME = new StormSchemeProviderRuntime();


    public StormSchemeFactory() {

    }

    public StormScheme provide(Class<?> cl) throws StormSchemeException {
        if (StormSchemeProviderApt.lookup(cl)) {
            return PROVIDER_APT.provide(cl);
        }
        return PROVIDER_RUNTIME.provide(cl);
    }
}
