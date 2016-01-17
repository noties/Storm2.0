package storm.parser.converter;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

import storm.parser.StormParserAptClassNameBuilder;
import storm.parser.StormParserAptData;
import storm.parser.StormParserAptWriter;
import storm.parser.StormParserAptWriterBase;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptWriterConverter extends StormParserAptWriterBase {

    public StormParserAptWriterConverter(Elements elements, Filer filer) {
        super(elements, filer, StormConverterAptClassNameBuilder.getInstance());
    }

    @Override
    protected boolean shouldWriteToFile(StormParserAptData data) {
        return data.isShouldGenerateConverter();
    }

    @Override
    protected String getSourceCode(StormParserAptData data) throws Throwable {
        return null;
    }
}
