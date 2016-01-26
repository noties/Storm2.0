package storm.parser;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptData {

    private final StormParserTable<TypeElement, Element, TypeMirror> mTable;

    public StormParserAptData(
            StormParserTable<TypeElement, Element, TypeMirror> table
    ) {
        mTable = table;
    }

    public StormParserTable<TypeElement, Element, TypeMirror> getTable() {
        return mTable;
    }
}


