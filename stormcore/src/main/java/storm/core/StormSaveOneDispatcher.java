package storm.core;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public interface StormSaveOneDispatcher {

    <T extends StormObject> long save(Storm storm, T value);

}
