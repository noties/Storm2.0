package storm.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import storm.sample.prefill.PreFillSampleActivity;
import storm.sample.rx.RxSampleActivity;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class MainActivity extends Activity {

    private static final Class<?>[] SAMPLES = new Class<?>[] {
            PreFillSampleActivity.class,
            RxSampleActivity.class
    };

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        final ListView listView = new ListView(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Class<?> cl = (Class<?>) parent.getAdapter().getItem(position);
                startActivity(new Intent(MainActivity.this, cl));
            }
        });

        final ListAdapter adapter = new ArrayAdapter<Class<?>>(
                this,
                android.R.layout.simple_list_item_1,
                SAMPLES
        );
        listView.setAdapter(adapter);

        setContentView(listView);
    }
}
