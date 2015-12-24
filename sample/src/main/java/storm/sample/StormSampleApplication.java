package storm.sample;

import android.app.Application;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import storm.db.Database;
import storm.query.Selection;
import storm.rx.StormObservablePreprocessor;
import storm.rx.StormRx;

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
