package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class JournalModePragma implements Pragma {

    public enum JournalMode {
        DELETE,
        TRUNCATE,
        PERSIST,
        MEMORY,
        WAL,
        OFF
    }

    public static JournalModePragma of(JournalMode mode) {
        return new JournalModePragma(mode.name());
    }

    private final String mValue;

    private JournalModePragma(String value) {
        this.mValue = value;
    }

    @Override
    public String getName() {
        return "journal_mode";
    }

    @Override
    public String getValue() {
        return mValue;
    }
}
