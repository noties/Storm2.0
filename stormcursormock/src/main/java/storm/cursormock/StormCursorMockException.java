package storm.cursormock;

/**
 * Created by Dimitry Ivanov on 18.12.2015.
 */
public class StormCursorMockException extends RuntimeException {

    static StormCursorMockException newInstance(String msg, Object... args) {
        return new StormCursorMockException(String.format(msg, args));
    }

    public StormCursorMockException() {
        super();
    }

    public StormCursorMockException(String detailMessage) {
        super(detailMessage);
    }
}
