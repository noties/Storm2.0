package storm.rx;

import storm.core.StormDeleteDispatcher;
import storm.core.StormObject;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
interface StormDeleteRxType<T extends StormObject> {
    StormRx storm();
    Class<T> table();
    Selection selection();
    StormDeleteDispatcher dispatcher();
}
