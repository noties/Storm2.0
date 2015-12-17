package storm.core;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
interface StormUpdateOneDispatcher {

    <T extends StormObject> int update(Storm storm, T value);

}
