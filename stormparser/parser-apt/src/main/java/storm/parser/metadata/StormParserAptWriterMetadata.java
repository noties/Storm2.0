package storm.parser.metadata;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

import storm.parser.StormParserAptClassNameBuilder;
import storm.parser.StormParserAptData;
import storm.parser.StormParserAptWriter;
import storm.parser.StormParserAptWriterBase;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptWriterMetadata extends StormParserAptWriterBase {

    public StormParserAptWriterMetadata(Elements elements, Filer filer) {
        super(elements, filer, StormMetadataAptClassNameBuilder.getInstance());
    }

    @Override
    protected String getSourceCode(String packageName, String className, StormParserAptData data) throws Throwable {
        return null;
    }
}
