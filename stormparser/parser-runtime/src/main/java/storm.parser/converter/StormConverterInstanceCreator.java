package storm.parser.converter;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public interface StormConverterInstanceCreator<T> {
    T create();
}
