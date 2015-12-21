package storm.core;

import java.util.Collection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormUpdateManyDispatcher extends StormDispatcher {

    <T extends StormObject> int update(Storm storm, Collection<T> values);

}
