package storm.core;

import storm.parser.StormInstanceCreator;

/**
 * Created by Dimitry Ivanov on 16.12.2015.
 */
public class ReflectionInstanceCreator<T> implements StormInstanceCreator<T> {

    final Class<T> mClass;

    public ReflectionInstanceCreator(Class<T> aClass) {
        mClass = aClass;
    }

    @Override
    public T create() {
        return storm.reflect.ReflectionInstanceCreator.newInstance(mClass);
    }
}
