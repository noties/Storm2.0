package storm.core;

import java.util.Collection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormUpdateManyDispatcher {

    <T extends StormObject> int update(Storm storm, Collection<T> values);

}
