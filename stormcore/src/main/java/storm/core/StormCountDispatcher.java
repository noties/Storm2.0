package storm.core;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormCountDispatcher {

    <T extends StormObject> int execute(Storm storm, Class<T> table, Selection selection);
}
