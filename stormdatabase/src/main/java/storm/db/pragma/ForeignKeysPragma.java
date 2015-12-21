package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class ForeignKeysPragma extends AbsPragma {

    public static ForeignKeysPragma off() {
        return new ForeignKeysPragma("0");
    }

    public static ForeignKeysPragma on() {
        return new ForeignKeysPragma("1");
    }

    private ForeignKeysPragma(String value) {
        super("foreign_keys", value);
    }
}
