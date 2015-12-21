package storm.rx;

import rx.Observable;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public interface StormRxStreamSingleValue<T> extends StormRxStream {
    Observable<T> create();
}
