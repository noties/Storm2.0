package storm.core;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public interface StormOp<T> {
    Storm storm();
    Class<T> table();
    StormDispatcher dispatcher();
}
