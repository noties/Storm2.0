package storm.rx;

import rx.Observable;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public interface StormObservablePreProcessor {
    <V> Observable<V> preProcess(Observable<V> observable);
}
