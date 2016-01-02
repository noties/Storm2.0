package storm.parser;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public class StormParserTable<MAIN, ELEMENT, TYPE> {

    private final MAIN mMain;
    private final String mTableName;
    private final List<StormParserColumn<ELEMENT, TYPE>> mColumns;

    StormParserTable(MAIN main, String tableName, List<StormParserColumn<ELEMENT, TYPE>> columns) {
        this.mMain = main;
        mTableName = tableName;
        mColumns = columns;
    }

    public String getTableName() {
        return mTableName;
    }

    public MAIN getMain() {
        return mMain;
    }

    public List<StormParserColumn<ELEMENT, TYPE>> getElements() {
        return mColumns;
    }
}
