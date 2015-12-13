package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class SynchronousPragma implements Pragma {

    public enum Synchronous {
        OFF     (0),
        NORMAL  (1),
        FULL    (2);

        final int mValue;

        Synchronous(int value) {
            this.mValue = value;
        }
    }

    public static SynchronousPragma of(Synchronous synchronous) {
        return new SynchronousPragma(String.valueOf(synchronous.mValue));
    }

    private final String mValue;

    private SynchronousPragma(String value) {
        this.mValue = value;
    }

    @Override
    public String getName() {
        return "synchronous";
    }

    @Override
    public String getValue() {
        return mValue;
    }
}
