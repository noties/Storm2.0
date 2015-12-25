package storm.sample.basic;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 25.12.2015.
 */
@Table("basic_table")
class BasicItem implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    private String text;

    @Column
    private double real;

    // empty constructor
    // note, that if object has no empty constructor an `InstanceCreator` should be registered
    public BasicItem() {

    }

    public long getId() {
        return id;
    }

    public BasicItem setId(long id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public BasicItem setText(String text) {
        this.text = text;
        return this;
    }

    public double getReal() {
        return real;
    }

    public BasicItem setReal(double real) {
        this.real = real;
        return this;
    }
}
