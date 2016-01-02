package storm.parser.scheme;

import java.util.List;

import storm.parser.StormParserItem;

/**
 * Created by Dimitry Ivanov on 01.12.2015.
 */
public interface StormScheme extends StormParserItem {

    List<String> onCreate() throws StormSchemeException;
    List<String> onUpgrade(int oldVersion, int newVersion) throws StormSchemeException;

}
