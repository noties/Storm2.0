package storm.rx;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public interface StormRxStreamWithUpdates extends StormRxStream {
    StormRxStreamWithUpdates subscribeForUpdates();
}
