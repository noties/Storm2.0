package storm.scheme;

/**
 * Created by Dimitry Ivanov on 22.12.2015.
 */
public class StormGeneratorAptTest extends SchemeGeneratorBaseTest {

    @Override
    public storm.scheme.StormScheme getScheme(Class<?> cl) {
        try {
            return new StormSchemeProviderApt().provide(cl);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
