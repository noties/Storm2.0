package storm.parser.converter;

import storm.parser.StormParserItemProviderBase;
import storm.parser.StormParserTable;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormConverterProviderFactory extends StormParserItemProviderBase<StormConverter> {

    public StormConverterProviderFactory() {
        super(StormConverterAptClassNameBuilder.getInstance());
    }

    @Override
    public <T> StormConverter<T> runtime(StormParserTable table) throws StormConverterException {
        return null;
    }
}
