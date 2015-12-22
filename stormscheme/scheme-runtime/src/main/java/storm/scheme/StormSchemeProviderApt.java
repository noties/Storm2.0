package storm.scheme;

import storm.reflect.ReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
class StormSchemeProviderApt implements StormSchemeProvider {

    static boolean lookup(Class<?> cl) {
        try {
            schemeClass(cl);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public StormScheme provide(Class<?> cl) throws StormSchemeException {

        Class<?> scheme;
        try {
            scheme = schemeClass(cl);
        } catch (ClassNotFoundException e) {
            throw new StormSchemeException(e);
        }

        return (StormScheme) ReflectionInstanceCreator.newInstance(scheme);
    }

    static Class<?> schemeClass(Class<?> table) throws ClassNotFoundException {
        return Class.forName(StormSchemeAptClassNameBuilder.fullName(
                table.getPackage().getName(),
                table.getSimpleName()
        ));
    }
}
