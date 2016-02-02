package storm.sample.iterator;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 25.12.2015.
 */
@Table(recreateOnUpgrade = true)
class IteratorItem implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    long id;

    @Column
    String data;

    public long getId() {
        return id;
    }

    public IteratorItem setId(long id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    public IteratorItem setData(String data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return data;
    }
}
