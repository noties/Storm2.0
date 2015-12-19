package storm.sample;

import android.app.Application;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
public class StormSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(true));
    }
}
