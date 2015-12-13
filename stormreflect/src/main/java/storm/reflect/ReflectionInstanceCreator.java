package storm.reflect;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class ReflectionInstanceCreator {

    public static <T> T newInstance(Class<T> cl) {
        try {
            return cl.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException("Cannot instantiate class: " + cl.getName(), t);
        }
    }

    private ReflectionInstanceCreator() {}
}
