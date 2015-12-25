package storm.sample.rx;

import storm.annotations.Column;
import storm.annotations.Default;
import storm.annotations.NewColumn;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
@Table
class RxItem implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    private String data;

    @Column
    @NewColumn(2)
    @Default("def") // it's always good to have @Default for new columns
    private String column;

    public long getId() {
        return id;
    }

    public RxItem setId(long id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    public RxItem setData(String data) {
        this.data = data;
        return this;
    }

    public String getColumn() {
        return column;
    }

    public RxItem setColumn(String column) {
        this.column = column;
        return this;
    }

    @Override
    public String toString() {
        return "RxSampleItem{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", column='" + column + '\'' +
                '}';
    }
}
