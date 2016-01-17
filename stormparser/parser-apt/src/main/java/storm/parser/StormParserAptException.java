package storm.parser;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptException extends Exception {

    public static StormParserAptException newInstance(String msg, Object... args) {
        return new StormParserAptException(String.format(msg, args));
    }

    public static StormParserAptException newInstance(Throwable cause, String msg, Object... args) {
        return new StormParserAptException(String.format(msg, args), cause);
    }

    public StormParserAptException(String message) {
        super(message);
    }

    public StormParserAptException(String message, Throwable cause) {
        super(message, cause);
    }

    public StormParserAptException(Throwable cause) {
        super(cause);
    }
}
