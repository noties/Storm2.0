package storm.parser;

import java.lang.reflect.Field;

import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
class StormParserColumn {

    private String mName;
    private StormType mStormType;

    private boolean mIsPrimaryKey;

    private Class<?> mSerializerType;

    private Field mField;

    public String getName() {
        return mName;
    }

    public Field getField() {
        return mField;
    }

    public StormType getType() {
        return mStormType;
    }

    public boolean isPrimaryKey() {
        return mIsPrimaryKey;
    }

    public Class<?> getSerializerType() {
        return mSerializerType;
    }

    public StormParserColumn setName(String name) {
        mName = name;
        return this;
    }

    public StormParserColumn setStormType(StormType stormType) {
        mStormType = stormType;
        return this;
    }

    public StormParserColumn setIsPrimaryKey(boolean isPrimaryKey) {
        mIsPrimaryKey = isPrimaryKey;
        return this;
    }

    public StormParserColumn setSerializerType(Class<?> serializerType) {
        mSerializerType = serializerType;
        return this;
    }

    public StormParserColumn setField(Field field) {
        mField = field;
        return this;
    }
}
