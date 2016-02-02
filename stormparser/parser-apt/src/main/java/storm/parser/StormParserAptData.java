package storm.parser;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptData {

    private final StormParserTable<TypeElement, Element, TypeMirror> mTable;
    private final boolean mConverter;
    private final boolean mMetadata;
    private final boolean mScheme;

    public StormParserAptData(
            StormParserTable<TypeElement, Element, TypeMirror> table,
            boolean converter,
            boolean metadata,
            boolean scheme
    ) {
        this.mTable = table;
        this.mConverter = converter;
        this.mMetadata = metadata;
        this.mScheme = scheme;
    }

    public StormParserTable<TypeElement, Element, TypeMirror> getTable() {
        return mTable;
    }

    public boolean converter() {
        return mConverter;
    }

    public boolean metadata() {
        return mMetadata;
    }

    public boolean scheme() {
        return mScheme;
    }
}


