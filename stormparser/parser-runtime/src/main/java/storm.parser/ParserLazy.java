package storm.parser;

import storm.lazy.LazyThrows;

/**
 * Created by Dimitry Ivanov on 24.01.2016.
 */
class ParserLazy<V extends StormParserItem> extends LazyThrows<V, StormParserException> {

    interface ParserLazyProvider<V> extends LazyThrowsProvider<V, StormParserException> {}

    public ParserLazy(LazyThrowsProvider<V, StormParserException> provider) {
        super(provider);
    }
}
