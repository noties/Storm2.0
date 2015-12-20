package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public class TempStorePragma extends AbsPragma {

    public static TempStorePragma def() {
        return new TempStorePragma("0");
    }

    public static TempStorePragma file() {
        return new TempStorePragma("1");
    }

    public static TempStorePragma memory() {
        return new TempStorePragma("2");
    }

    private TempStorePragma(String value) {
        super("temp_store", value);
    }
}
