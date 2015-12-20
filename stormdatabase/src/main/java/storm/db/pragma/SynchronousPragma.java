package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class SynchronousPragma extends AbsPragma {

    public static SynchronousPragma off() {
        return new SynchronousPragma("0");
    }

    public static SynchronousPragma normal() {
        return new SynchronousPragma("1");
    }

    public static SynchronousPragma full() {
        return new SynchronousPragma("2");
    }

    private SynchronousPragma(String value) {
        super("synchronous", value);
    }
}
