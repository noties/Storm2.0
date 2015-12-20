package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class JournalModePragma extends AbsPragma {

    public static JournalModePragma delete() {
        return new JournalModePragma("DELETE");
    }

    public static JournalModePragma truncate() {
        return new JournalModePragma("TRUNCATE");
    }

    public static JournalModePragma persist() {
        return new JournalModePragma("PERSIST");
    }

    public static JournalModePragma memory() {
        return new JournalModePragma("MEMORY");
    }

    public static JournalModePragma wal() {
        return new JournalModePragma("WAL");
    }

    public static JournalModePragma off() {
        return new JournalModePragma("OFF");
    }

    private JournalModePragma(String value) {
        super("journal_mode", value);
    }
}
