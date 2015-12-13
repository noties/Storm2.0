package storm.parser;

import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class StormParserColumn {

    private String mName;
    private StormType mStormType;

    private boolean mIsPrimaryKey;

    private Class<?> mColumnType;
    private Class<?> mSerializerType;

}
