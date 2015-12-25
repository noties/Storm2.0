package storm.parser;

import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
class StormParserColumn<ELEMENT, TYPE> {

    private String mName;
    private StormType mStormType;

    private boolean mIsPrimaryKey;
    private boolean mIsAutoIncrement;

    private TYPE mSerializerType;

    private ELEMENT mElement;

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
}
