package storm.parser.scheme;

import storm.parser.StormParserException;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
public class StormSchemeException extends StormParserException {

    public static StormSchemeException newInstance(String message, Object... args) {
        return new StormSchemeException(String.format(message, args));
    }

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