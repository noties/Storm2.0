package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class ForeignKeysPragma implements Pragma {

    public static ForeignKeysPragma of(boolean isEnabled) {
        return new ForeignKeysPragma(isEnabled ? "1" : "0");
    }

    private final String mValue;

    private ForeignKeysPragma(String value) {
        this.mValue = value;
    }

    @Override
    public String getName() {
        return "foreign_keys";
    }

    @Override
    public String getValue() {
        return mValue;
    }
}
