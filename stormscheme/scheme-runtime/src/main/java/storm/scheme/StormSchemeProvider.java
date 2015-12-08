package storm.scheme;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
public interface StormSchemeProvider {

    StormScheme provide(Class<?> cl) throws StormSchemeException;

}
