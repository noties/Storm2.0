package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class TempStorePragma implements Pragma {

    public enum TempStore {

        DEFAULT (0),
        FILE    (1),
        MEMORY  (2);

        final int mValue;

        TempStore(int value) {
            this.mValue = value;
        }
    }

    public static TempStorePragma of(TempStore tempStore) {
        return new TempStorePragma(String.valueOf(tempStore.mValue));
    }

    private final String mValue;

    private TempStorePragma(String value) {
        this.mValue = value;
    }

    @Override
    public String getName() {
        return "temp_store";
    }

    @Override
    public String getValue() {
        return mValue;
    }
}
