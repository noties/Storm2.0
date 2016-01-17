package storm.parser;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptData {

    private final boolean mShouldGenerateScheme;
    private final boolean mShouldGenerateMetadata;
    private final boolean mShouldGenerateConverter;

    private final StormParserTable<TypeElement, Element, TypeMirror> mTable;

    public StormParserAptData(
            boolean shouldGenerateScheme,
            boolean shouldGenerateMetadata,
            boolean shouldGenerateConverter,
            StormParserTable<TypeElement, Element, TypeMirror> table
    ) {
        mShouldGenerateScheme = shouldGenerateScheme;
        mShouldGenerateMetadata = shouldGenerateMetadata;
        mShouldGenerateConverter = shouldGenerateConverter;
        mTable = table;
    }

    public boolean isShouldGenerateScheme() {
        return mShouldGenerateScheme;
    }

    public boolean isShouldGenerateMetadata() {
        return mShouldGenerateMetadata;
    }

    public boolean isShouldGenerateConverter() {
        return mShouldGenerateConverter;
    }

    public StormParserTable<TypeElement, Element, TypeMirror> getTable() {
        return mTable;
    }
}


