package storm.sample.extreme;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 13.02.2016.
 */
@Table
class ExtremeItem implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    long id;

    @Column
    String data1;

    @Column
    String data2;

    @Column
    String data3;

    ExtremeItem() {

    }

    ExtremeItem(String data1, String data2, String data3) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }
}
