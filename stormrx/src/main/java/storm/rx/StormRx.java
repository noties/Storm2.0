package storm.rx;

import storm.core.Storm;
import storm.core.StormObject;
import storm.db.Database;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormRx extends Storm {

    static {
        newInstance(null)
                .query(null)
                .stream()
                .asCursor()
    }

    public static StormRx newInstance(Database.Configuration configuration) {
        return new StormRx(configuration);
    }

    private StormRx(Database.Configuration configuration) {
        super(configuration);
    }

    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table) {
        return new StormQueryRx<>(super.query(table));
    }
}
