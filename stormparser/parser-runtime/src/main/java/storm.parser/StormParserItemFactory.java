package storm.parser;

import storm.reflect.ReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
class StormParserItemFactory {
    <T, I extends StormParserItem<T>> I provide(StormParserAptClassNameBuilder nameBuilder, Class<T> cl, Object... args) {
        Class<?> aptClass;
        try {
            aptClass = Class.forName(
                    nameBuilder.fullName(
                            cl.getPackage().getName(),
                            cl.getSimpleName()
                    )
            );
        } catch (ClassNotFoundException e) {
            return null;
        }

        //noinspection unchecked
        return (I) ReflectionInstanceCreator.newInstance(aptClass, args);
    }
}
