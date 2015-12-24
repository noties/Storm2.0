package storm.sample;

import android.support.v7.app.AppCompatActivity;

import ru.noties.debug.Debug;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String LINE = "##################################";
    private static final String NAME = "### %1$s (%2$s)";

    public abstract void showcase();

    @Override
    public void onStart() {
        super.onStart();

        logStart();

        showcase();
    }

    @Override
    public void onStop() {
        super.onStop();

        logEnd();
    }

    void logStart() {
        Debug.i();
        Debug.i(LINE);
        Debug.i(NAME, getClass().getSimpleName(), "start");
        Debug.i(LINE);
    }

    void logEnd() {
        Debug.i(LINE);
        Debug.i(NAME, getClass().getSimpleName(), "end");
        Debug.i(LINE);
        Debug.i();
    }
}
