package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class LockingModePragma extends AbsPragma {

    public static LockingModePragma normal() {
        return new LockingModePragma("NORMAL");
    }

    public static LockingModePragma exclusive() {
        return new LockingModePragma("EXCLUSIVE");
    }

    private LockingModePragma(String value) {
        super("locking_mode", value);
    }
}
