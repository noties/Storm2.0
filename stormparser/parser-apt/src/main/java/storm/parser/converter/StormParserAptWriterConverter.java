package storm.parser.converter;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

import storm.parser.StormParserAptData;
import storm.parser.StormParserAptWriterBase;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptWriterConverter extends StormParserAptWriterBase {

    public StormParserAptWriterConverter(Elements elements, Filer filer) {
        super(elements, filer, StormConverterAptClassNameBuilder.getInstance());
    }

    @Override
    protected String getSourceCode(String packageName, String className, StormParserAptData data) throws Throwable {
        return null;
    }
}
