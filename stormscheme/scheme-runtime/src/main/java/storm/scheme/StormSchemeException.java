package storm.scheme;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
public class StormSchemeException extends Exception {

    public StormSchemeException(String detailMessage) {
        super(detailMessage);
    }

    public StormSchemeException(Throwable throwable) {
        super(throwable);
    }

    public StormSchemeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
