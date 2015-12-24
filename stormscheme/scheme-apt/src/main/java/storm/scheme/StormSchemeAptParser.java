package storm.scheme;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
class StormSchemeAptParser {

    private static final StormSchemeParser PARSER = new StormSchemeParser();

    final Elements mElements;
//
    final StormSchemeTypeApt mTypeApt;

    StormSchemeAptParser(Types types, Elements elements) {
        mElements = elements;
        mTypeApt = new StormSchemeTypeApt(types, elements);
    }

    // return NULL if for this element scheme should not be generated
    StormSchemeTable table(TypeElement type) throws StormSchemeException {

        return PARSER.parseTable(type, mTypeApt);
    }

    String packageName(TypeElement element) {
        return mElements.getPackageOf(element).getQualifiedName().toString();
    }
}
