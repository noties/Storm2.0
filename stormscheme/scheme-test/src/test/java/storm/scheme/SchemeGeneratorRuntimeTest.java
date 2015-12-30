package storm.scheme;

/**
 * Created by Dimitry Ivanov on 12.12.2015.
 */
public class SchemeGeneratorRuntimeTest extends SchemeGeneratorBaseTest {

    public StormScheme getScheme(Class<?> cl) {
        try {
            return new StormSchemeProviderRuntime().provide(cl);
        } catch (StormSchemeException e) {
            throw new RuntimeException(e);
        }
    }
}
