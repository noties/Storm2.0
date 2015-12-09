package storm.scheme;

/**
 * Created by Dimitry Ivanov on 09.12.2015.
 */
public class StormSchemeIndex {

    private final String mIndexName;
    private final String mSorting;

    public StormSchemeIndex(String indexName, String sorting) {
        mIndexName = indexName;
        mSorting = sorting;
    }

    public String getIndexName() {
        return mIndexName;
    }

    public String getSorting() {
        return mSorting;
    }
}
