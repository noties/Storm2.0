package storm.sample;

import ru.noties.debug.Debug;
import rx.functions.Action1;

/**
 * Created by Dimitry Ivanov on 13.02.2016.
 */
public class DebugErrorAction implements Action1<Throwable> {
    @Override
    public void call(Throwable throwable) {
        Debug.e(throwable);
    }
}
