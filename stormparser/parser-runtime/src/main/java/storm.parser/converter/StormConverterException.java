package storm.parser.converter;

import storm.parser.StormParserException;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormConverterException extends StormParserException {

    static StormConverterException newInstance(String msg, Object... args) {
        return new StormConverterException(String.format(msg, args));
    }

    public StormConverterException(Throwable throwable) {
        super(throwable);
    }

    public StormConverterException(String detailMessage) {
        super(detailMessage);
    }

    public StormConverterException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
