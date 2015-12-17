package storm.core;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
interface StormDeleteDispatcher {
    <T extends StormObject> int execute(Storm storm, Class<T> table, Selection selection);
}
