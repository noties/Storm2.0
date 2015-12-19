package storm.core;

import android.database.DataSetObserver;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
class CloseDatabaseReferenceObserver extends DataSetObserver {

    final Storm mStorm;

    CloseDatabaseReferenceObserver(Storm storm) {
        mStorm = storm;
    }

    @Override
    public void onInvalidated() {
        super.onInvalidated();
        mStorm.database().close();
    }
}
