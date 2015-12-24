package storm.scheme;

import java.util.Collections;
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

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSchemeAptProcessor extends AbstractProcessor implements Logger {

    private StormSchemeAptParser mParser;
    private StormSchemeAptWriter mWriter;

    private Messager mMessager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Table.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        final Types types = processingEnv.getTypeUtils();
        final Elements elements = processingEnv.getElementUtils();
        final Filer filer = processingEnv.getFiler();

        mParser = new StormSchemeAptParser(types, elements);
        mWriter = new StormSchemeAptWriter(elements, filer, this);

        mMessager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean result = false;
        for (TypeElement annotation : annotations) {
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);

            if (elements == null
                    || elements.size() == 0) {
                log(Diagnostic.Kind.NOTE, "No elements annotated with @%s", annotation);
                continue;
            }

            for (Element element: elements) {
                if (!(element instanceof TypeElement)) {
                    log(Diagnostic.Kind.WARNING, "Element annotated with @Table, but it's not of type Type, %s", element);
                    continue;
                }

                try {
                    result |= process((TypeElement) element);
                } catch (Throwable t) {
                    log(Diagnostic.Kind.ERROR, "Exception during processing annotation: `%s`, element: `%s`, exception: %s", annotation, element, t);
                }
            }
        }
        return result;
    }

    private boolean process(TypeElement element) throws Throwable {

        final StormSchemeTable table = mParser.table(element);
        if (table == null) {
            return true;
        }

        final String packageName = mParser.packageName(element);
        final String className = StormSchemeAptClassNameBuilder.className(element.getSimpleName().toString());
        mWriter.write(packageName, className, table);
        return true;
    }

    @Override
    public void log(Diagnostic.Kind level, String msg, Object... args) {
        mMessager.printMessage(level, String.format(msg, args));
    }
}
