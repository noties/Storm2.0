package storm.scheme;

import javax.tools.Diagnostic;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public interface Logger {
    void log(Diagnostic.Kind level, String msg, Object... args);
}
