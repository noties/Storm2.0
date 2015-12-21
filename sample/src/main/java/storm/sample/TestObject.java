package storm.sample;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
@Table
public class TestObject implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    private String data;

    public long getId() {
        return id;
    }

    public TestObject setId(long id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    public TestObject setData(String data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "id=" + id +
                ", data='" + data + '\'' +
                '}';
    }
}