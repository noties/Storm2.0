package storm.sample.versioning;

import storm.annotations.Column;
import storm.annotations.NewColumn;
import storm.annotations.NewTable;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
@Table
@NewTable(2)
class VersioningNewTable implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    private String data;

    @NewColumn(3)
    @Column
    private double newField;

    public long getId() {
        return id;
    }

    public VersioningNewTable setId(long id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    public VersioningNewTable setData(String data) {
        this.data = data;
        return this;
    }

    public double getNewField() {
        return newField;
    }

    public VersioningNewTable setNewField(double newField) {
        this.newField = newField;
        return this;
    }
}
