package storm.parser;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public abstract class StormParserAptWriterBase implements StormParserAptWriter {

    private final Filer mFiler;
    protected final Elements mElements;
    protected final StormParserAptClassNameBuilder mBuilder;

    public StormParserAptWriterBase(Elements elements, Filer filer, StormParserAptClassNameBuilder builder) {
        this.mElements = elements;
        this.mFiler = filer;
        this.mBuilder = builder;
    }

    protected abstract boolean shouldWriteToFile(StormParserAptData data);
    protected abstract String getSourceCode(StormParserAptData data) throws Throwable;

    @Override
    public final void write(StormParserAptData data) throws Throwable {
        if (!shouldWriteToFile(data)) {
            return;
        }

        final TypeElement element = data.getTable().getMain();

        final String javaFileName = mBuilder.fullName(
                mElements.getPackageOf(element).getQualifiedName().toString(),
                element.getSimpleName().toString()
        );

        final String source = getSourceCode(data);

        writeToJavaFile(javaFileName, source);
    }

    private void writeToJavaFile(String javaFileName, String source) throws Throwable {

        Writer writer = null;
        try {
            final JavaFileObject fileObject = mFiler.createSourceFile(javaFileName);
            writer = fileObject.openWriter();
            writer.write(source);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) { /* ignored */}
            }
        }
    }
}
