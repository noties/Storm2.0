package storm.core;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
public class StormException extends RuntimeException {

    public StormException(String detailMessage) {
        super(detailMessage);
    }

    public StormException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public StormException(Throwable throwable) {
        super(throwable);
    }

    public StormException() {
        super();
    }

}
