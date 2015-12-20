package storm.db.pragma;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
class AbsPragma implements Pragma {

    private final String name;
    private final String value;

    AbsPragma(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }
}
