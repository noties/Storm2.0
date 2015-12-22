package storm.rx;

import rx.Observable;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
class StormObservablePreprocessorNoOp implements StormObservablePreprocessor {

    private static final StormObservablePreprocessor INSTANCE = new StormObservablePreprocessorNoOp();

    public static StormObservablePreprocessor getInstance() {
        return INSTANCE;
    }

    @Override
    public <V> Observable<V> preProcess(Observable<V> observable) {
        return observable;
    }
}
