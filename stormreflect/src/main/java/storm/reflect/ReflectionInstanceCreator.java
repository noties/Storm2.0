package storm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class ReflectionInstanceCreator {

    // if there is a need to pass null to constructor - pass Class, but never NULL
    // does not work for primitive values...
    public static <T> T newInstance(Class<T> cl, Object... constructorArgs) {

        try {

            final Type[] constructorTypes;
            if (constructorArgs == null
                    || constructorArgs.length == 0) {
                constructorTypes = null;
            } else {
                final int length = constructorArgs.length;
                constructorTypes = new Type[length];
                Object arg;
                for (int i = 0; i < length; i++) {
                    arg = constructorArgs[i];
                    final Type type;
                    if (arg.getClass().equals(Class.class)) {
                        type = (Type) arg;
                        constructorArgs[i] = null;
                    } else {
                        type = arg.getClass();
                    }
                    constructorTypes[i] = type;
                }
            }

            final Constructor<?>[] constructors = cl.getDeclaredConstructors();
            Type[] types;
            for (Constructor constructor: constructors) {

                types = constructor.getGenericParameterTypes();

                if (constructorTypes == null) {
                    if (types == null
                            || types.length == 0) {
                        constructor.setAccessible(true);
                        //noinspection unchecked
                        return (T) constructor.newInstance();
                    }
                } else {
                    if (types != null) {
                        if (Arrays.equals(types, constructorTypes)) {
                            constructor.setAccessible(true);
                            //noinspection unchecked
                            return (T) constructor.newInstance(constructorArgs);
                        }
                    }
                }
            }

            throw new IllegalArgumentException(
                    String.format(
                            "Cannot find constructor for a class: `%s`, with specified arguments: `%s`", cl.getName(), Arrays.toString(constructorArgs)
                    )
            );

        } catch (Throwable t) {
            throw new RuntimeException("Cannot instantiate class: " + cl.getName(), t);
        }
    }

    private ReflectionInstanceCreator() {}
}
