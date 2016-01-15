package storm.core;

import storm.parser.converter.StormConverterInstanceCreator;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
public class StormReflectionInstanceCreator<T> implements StormConverterInstanceCreator<T> {

    final Class<T> mClass;

    public StormReflectionInstanceCreator(Class<T> aClass) {
        mClass = aClass;
    }

    @Override
    public T create() {
        return storm.reflect.ReflectionInstanceCreator.newInstance(mClass);
    }
}
