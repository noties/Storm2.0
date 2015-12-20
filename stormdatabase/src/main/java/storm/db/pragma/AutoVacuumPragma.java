package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
public class AutoVacuumPragma extends AbsPragma {

    public static AutoVacuumPragma off() {
        return new AutoVacuumPragma("0");
    }

    public static AutoVacuumPragma full() {
        return new AutoVacuumPragma("1");
    }

    public static AutoVacuumPragma incremental() {
        return new AutoVacuumPragma("2");
    }

    private AutoVacuumPragma(String value) {
        super("auto_vacuum", value);
    }
}
