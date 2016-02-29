package storm.sample;

import rx.functions.Action1;

/**
 * Created by Dimitry Ivanov on 29.02.2016.
 */
public class NoOpAction<T> implements Action1<T> {
    @Override
    public void call(T t) {

    }
}
