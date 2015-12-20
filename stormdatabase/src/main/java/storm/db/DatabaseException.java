package storm.db;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String detailMessage) {
        super(detailMessage);
    }
}
