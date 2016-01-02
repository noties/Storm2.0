package storm.parser.metadata;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class PrimaryKeySelection {

    private final String mPrimaryKeyName;
    private final Object mPrimaryKeyValue;

    PrimaryKeySelection(String primaryKeyName, Object primaryKeyValue) {
        mPrimaryKeyName = primaryKeyName;
        mPrimaryKeyValue = primaryKeyValue;
    }

    public String getPrimaryKeyName() {
        return mPrimaryKeyName;
    }

    public Object getPrimaryKeyValue() {
        return mPrimaryKeyValue;
    }
}
