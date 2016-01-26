package storm.parser;

import storm.parser.scheme.StormSchemeForeignKey;
import storm.parser.scheme.StormSchemeIndex;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class StormParserColumn<ELEMENT, TYPE> {

    private String mName;
    private StormType mStormType;

    private boolean mIsPrimaryKey;
    private boolean mIsAutoIncrement;
    private boolean mIsNonNull;
    private boolean mIsUnique;

    private String mDefValue;

    private TYPE mSerializerType;

    private ELEMENT mElement;

    private StormSchemeIndex mIndex;
    private StormSchemeForeignKey mForeignKey;

    private int mVersionWhenAdded;

    public String getName() {
        return mName;
    }

    public ELEMENT getElement() {
        return mElement;
    }

    public StormType getType() {
        return mStormType;
    }

    public boolean isPrimaryKey() {
        return mIsPrimaryKey;
    }

    public boolean isAutoIncrement() {
        return mIsAutoIncrement;
    }

    public TYPE getSerializerType() {
        return mSerializerType;
    }

    public StormSchemeIndex getIndex() {
        return mIndex;
    }

    public int getVersionWhenAdded() {
        return mVersionWhenAdded;
    }

    public boolean isNonNull() {
        return mIsNonNull;
    }

    public boolean isUnique() {
        return mIsUnique;
    }

    public String getDefValue() {
        return mDefValue;
    }

    public StormSchemeForeignKey getForeignKey() {
        return mForeignKey;
    }

    public StormParserColumn<ELEMENT, TYPE> setName(String name) {
        mName = name;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setStormType(StormType stormType) {
        mStormType = stormType;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setIsPrimaryKey(boolean isPrimaryKey) {
        mIsPrimaryKey = isPrimaryKey;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setIsAutoIncrement(boolean isAutoIncrement) {
        mIsAutoIncrement = isAutoIncrement;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setSerializerType(TYPE serializerType) {
        mSerializerType = serializerType;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setElement(ELEMENT element) {
        mElement = element;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setIndex(StormSchemeIndex index) {
        mIndex = index;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setVersionWhenAdded(int versionWhenAdded) {
        mVersionWhenAdded = versionWhenAdded;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setIsNonNull(boolean isNonNull) {
        mIsNonNull = isNonNull;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setIsUnique(boolean isUnique) {
        mIsUnique = isUnique;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setDefValue(String defValue) {
        mDefValue = defValue;
        return this;
    }

    public StormParserColumn<ELEMENT, TYPE> setForeignKey(StormSchemeForeignKey foreignKey) {
        mForeignKey = foreignKey;
        return this;
    }
}
