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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimaryKeySelection selection = (PrimaryKeySelection) o;

        if (mPrimaryKeyName != null ? !mPrimaryKeyName.equals(selection.mPrimaryKeyName) : selection.mPrimaryKeyName != null)
            return false;
        return !(mPrimaryKeyValue != null ? !mPrimaryKeyValue.equals(selection.mPrimaryKeyValue) : selection.mPrimaryKeyValue != null);

    }

    @Override
    public int hashCode() {
        int result = mPrimaryKeyName != null ? mPrimaryKeyName.hashCode() : 0;
        result = 31 * result + (mPrimaryKeyValue != null ? mPrimaryKeyValue.hashCode() : 0);
        return result;
    }
}
