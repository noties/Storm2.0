package storm.parser;

/**
 * Created by Dimitry Ivanov on 13.12.2015.
 */
public abstract class StormParserException extends Exception {

//    public static StormParserException newInstance(String msg, Object... args) {
//        return new StormParserException(String.format(msg, args));
//    }

    public StormParserException(Throwable throwable) {
        super(throwable);
    }

    public StormParserException(String detailMessage) {
        super(detailMessage);
    }

    public StormParserException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
