package storm.parser;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public class StormParserTable<MAIN, ELEMENT, TYPE> {

    private final MAIN mMain;
    private final String mTableName;
    private final List<StormParserColumn<ELEMENT, TYPE>> mColumns;
    private final int mVersionWhenAdded;
    private final boolean mIsRecreateOnUpgrade;
    private final String mCustomNotificationUri;

    StormParserTable(
            MAIN main,
            String tableName,
            List<StormParserColumn<ELEMENT, TYPE>> columns,
            int versionWhenAdded,
            boolean isRecreateOnUpgrade,
            String customNotificationUri
    ) {
        this.mMain = main;
        this.mTableName = tableName;
        this.mColumns = columns;
        this.mVersionWhenAdded = versionWhenAdded;
        this.mIsRecreateOnUpgrade = isRecreateOnUpgrade;
        this.mCustomNotificationUri = customNotificationUri;
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

    public int getVersionWhenAdded() {
        return mVersionWhenAdded;
    }

    public boolean isRecreateOnUpgrade() {
        return mIsRecreateOnUpgrade;
    }

    public String getCustomNotificationUri() {
        return mCustomNotificationUri;
    }
}
