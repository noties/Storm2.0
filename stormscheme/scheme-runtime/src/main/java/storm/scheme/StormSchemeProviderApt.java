package storm.scheme;

import storm.reflect.ReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
class StormSchemeProviderApt implements StormSchemeProvider {

    static boolean lookup(Class<?> cl) {
        try {
            Class.forName(StormSchemeAptClassNameBuilder.fullName(
                    cl.getPackage().getName(),
                    cl.getSimpleName()
            ));
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public StormScheme provide(Class<?> cl) throws StormSchemeException {

        Class<?> scheme;
        try {
            scheme = Class.forName(StormSchemeAptClassNameBuilder.fullName(
                    cl.getPackage().getName(),
                    cl.getSimpleName()
            ));
        } catch (ClassNotFoundException e) {
            throw new StormSchemeException(e);
        }

        return (StormScheme) ReflectionInstanceCreator.newInstance(scheme);
    }
}
