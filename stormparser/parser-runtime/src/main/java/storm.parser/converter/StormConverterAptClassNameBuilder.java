package storm.parser.converter;

import storm.parser.StormParserAptClassNameBuilderBase;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormConverterAptClassNameBuilder extends StormParserAptClassNameBuilderBase {

    private static final StormConverterAptClassNameBuilder INSTANCE = new StormConverterAptClassNameBuilder();

    public static StormConverterAptClassNameBuilder getInstance() {
        return INSTANCE;
    }

    private StormConverterAptClassNameBuilder() {
        super("Converter");
    }
}
