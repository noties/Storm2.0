package storm.parser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import storm.annotations.Table;
import storm.parser.converter.StormParserAptWriterConverter;
import storm.parser.metadata.StormParserAptWriterMetadata;
import storm.parser.scheme.StormParserAptWriterScheme;

/**
 * Created by Dimitry Ivanov on 30.12.2015.
 */
public class StormParserAptProcessor extends AbstractProcessor {

    private Messager mMessager;

    private StormParserAptDataParser mParser;
    private List<StormParserAptWriter> mWriters;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mMessager   = processingEnv.getMessager();

        final Types types = processingEnv.getTypeUtils();
        final Elements elements = processingEnv.getElementUtils();
        final Filer filer = processingEnv.getFiler();

        mParser = new StormParserAptDataParser(types, elements);

        mWriters = Arrays.asList(
                (StormParserAptWriter) new StormParserAptWriterScheme(elements, filer),
                new StormParserAptWriterMetadata(elements, filer),
                new StormParserAptWriterConverter(elements, filer)
        );
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Table.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Set<? extends Element> tables = roundEnv.getElementsAnnotatedWith(Table.class);

        if (tables != null
                && tables.size() > 0) {

            for (Element table: tables) {
                try {
                    process(table);
                } catch (Throwable t) {
                    log(Diagnostic.Kind.WARNING, "Exception during processing element `%s`, exception: %s, msg: %s", table, t.getClass().getSimpleName(), t.getMessage());
//                    log(Diagnostic.Kind.WARNING, "StackTrace: %s", stackTrace(t));
                }
            }
        }

        return true;
    }

    private void process(Element table) throws Throwable {

        if (table == null
                || !(table instanceof TypeElement)) {
            throw StormParserAptException.newInstance("Unexpected error. Element `%s` is not of type Type.");
        }

        final TypeElement typeElement = (TypeElement) table;

        final StormParserAptData data = mParser.parseData(typeElement);

        if (data == null) {
            return;
        }

        // so, parse
        for (StormParserAptWriter writer: mWriters) {
            writer.write(data);
        }
    }

    private void log(Diagnostic.Kind level, String msg, Object... args) {
        mMessager.printMessage(level, String.format(msg, args));
    }

    private static String stackTrace(Throwable throwable) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);
        throwable.printStackTrace(writer);
        return stringWriter.toString();
    }
}
