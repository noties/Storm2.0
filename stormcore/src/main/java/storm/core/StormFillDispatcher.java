package storm.core;

import java.util.List;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormFillDispatcher extends StormDispatcher {

    <T extends StormObject> int fill(Storm storm, Selection selection, List<String> columns, boolean isInclude, T value);

}
