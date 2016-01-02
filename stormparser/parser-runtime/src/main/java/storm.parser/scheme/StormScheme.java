package storm.parser.scheme;

import java.util.List;

/**
 * Created by Dimitry Ivanov on 01.12.2015.
 */
public interface StormScheme {

    List<String> onCreate() throws StormSchemeException;
    List<String> onUpgrade(int oldVersion, int newVersion) throws StormSchemeException;

}
