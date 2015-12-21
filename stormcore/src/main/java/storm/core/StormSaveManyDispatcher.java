package storm.core;

import java.util.Collection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormSaveManyDispatcher extends StormDispatcher {
    <T extends StormObject> long[] save(Storm storm, Collection<T> collection);
}
