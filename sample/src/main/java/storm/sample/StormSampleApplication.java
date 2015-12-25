package storm.sample;

import android.app.Application;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
public class StormSampleApplication extends Application {

    // samples todo
    // version migration
    // pseudo-joins
    // serializers (with Lazy fields)
    // custom table for parsing query with join
    // rx (with notificationSubscriptions)
    // iterator (with cached)

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(true));


    }
}
