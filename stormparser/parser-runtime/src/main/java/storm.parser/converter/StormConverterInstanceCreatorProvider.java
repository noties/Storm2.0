package storm.parser.converter;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public interface StormConverterInstanceCreatorProvider {
    <T> StormConverterInstanceCreator<T> provide(Class<T> cl);
}
