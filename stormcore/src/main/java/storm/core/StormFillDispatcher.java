package storm.core;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
interface StormFillDispatcher {

    <T extends StormObject> int fill(Storm storm, Selection selection, T value);

}
