package storm.parser;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
interface StormInstanceCreator<T> {
    T create();
}
