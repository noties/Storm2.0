package storm.rx;

import rx.Observable;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
class StormObservablePreProcessorNoOp implements StormObservablePreProcessor {

    private static final StormObservablePreProcessor INSTANCE = new StormObservablePreProcessorNoOp();

    public static StormObservablePreProcessor getInstance() {
        return INSTANCE;
    }

    @Override
    public <V> Observable<V> preProcess(Observable<V> observable) {
        return observable;
    }
}
