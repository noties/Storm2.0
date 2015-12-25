package storm.parser;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
class StormParserTable<ELEMENT, TYPE> {

    private final String mTableName;
    private final List<StormParserColumn<ELEMENT, TYPE>> mColumns;

    StormParserTable(String tableName, List<StormParserColumn<ELEMENT, TYPE>> columns) {
        mTableName = tableName;
        mColumns = columns;
    }

    String getTableName() {
        return mTableName;
    }

    List<StormParserColumn<ELEMENT, TYPE>> getColumns() {
        return mColumns;
    }
}
