package storm.parser;

import storm.parser.converter.StormConverterInstanceCreator;
import storm.parser.converter.StormConverterInstanceCreatorProvider;
import storm.reflect.ReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 14.01.2016.
 */
public class TestReflectionInstanceCreator implements StormConverterInstanceCreatorProvider {

    @Override
    public <T> StormConverterInstanceCreator<T> provide(final Class<T> aClass) {
        return new StormConverterInstanceCreator<T>() {
            @Override
            public T create() {
                return ReflectionInstanceCreator.newInstance(aClass);
            }
        };
    }
}
