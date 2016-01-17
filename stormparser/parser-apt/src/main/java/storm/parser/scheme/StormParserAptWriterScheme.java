package storm.parser.scheme;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

import storm.parser.StormParserAptData;
import storm.parser.StormParserAptWriterBase;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptWriterScheme extends StormParserAptWriterBase {

    public StormParserAptWriterScheme(Elements elements, Filer filer) {
        super(elements, filer, StormSchemeAptNameBuilder.getInstance());
    }

    @Override
    protected boolean shouldWriteToFile(StormParserAptData data) {
        return data.isShouldGenerateScheme();
    }

    @Override
    protected String getSourceCode(StormParserAptData data) throws Throwable {
        return null;
    }
}
