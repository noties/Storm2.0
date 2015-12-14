package storm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class ReflectionInstanceCreator {

    public static <T> T newInstance(Class<T> cl) {

        try {

            final Constructor<?>[] constructors = cl.getDeclaredConstructors();
            Type[] constructorTypes;
            for (Constructor constructor: constructors) {
                constructorTypes = constructor.getGenericParameterTypes();
                if (constructorTypes == null
                        || constructorTypes.length == 0) {
                    constructor.setAccessible(true);
                    //noinspection unchecked
                    return (T) constructor.newInstance();
                }
            }

            throw new IllegalArgumentException("Class: `" + cl.getName() + "` has no empty constructor");

        } catch (Throwable t) {
            throw new RuntimeException("Cannot instantiate class: " + cl.getName(), t);
        }
    }

    private ReflectionInstanceCreator() {}
}
