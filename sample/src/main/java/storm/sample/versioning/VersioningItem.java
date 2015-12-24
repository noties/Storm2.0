package storm.sample.versioning;

import storm.annotations.Column;
import storm.annotations.NewColumn;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
@Table
class VersioningItem implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    private String data;

    @NewColumn(2)
    @Column
    private long time;

    public long getId() {
        return id;
    }

    public VersioningItem setId(long id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    public VersioningItem setData(String data) {
        this.data = data;
        return this;
    }

    public long getTime() {
        return time;
    }

    public VersioningItem setTime(long time) {
        this.time = time;
        return this;
    }
}
