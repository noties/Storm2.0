package storm.sample.basic;

import storm.annotations.Column;
import storm.annotations.Default;
import storm.annotations.NewColumn;
import storm.annotations.PrimaryKey;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.core.StormObject;
import storm.parser.converter.serializer.BooleanIntSerializer;

/**
 * Created by Dimitry Ivanov on 25.12.2015.
 */
@Table("basic_table")
class BasicItem implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    long id;

    @Column
    String text;

    @Column
    double real;

    // Note, that for booleans fields serializer must be specified
    // There is no support for them out-of-box.
    // This is done to be more explicit
    @NewColumn(2)
    @Column
    @Default("0")
    @Serialize(BooleanIntSerializer.class)
    boolean someBool;

    // empty constructor
    BasicItem() {

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

    public boolean isSomeBool() {
        return someBool;
    }

    public BasicItem setSomeBool(boolean someBool) {
        this.someBool = someBool;
        return this;
    }
}
