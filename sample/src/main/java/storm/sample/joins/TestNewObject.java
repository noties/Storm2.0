package storm.sample.joins;

import storm.annotations.Column;
import storm.annotations.Index;
import storm.annotations.NewColumn;
import storm.annotations.NewTable;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;

/**
 * Created by Dimitry Ivanov on 22.12.2015.
 */
@NewTable(2)
@Table
public class TestNewObject {

    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    @Index("hello_index")
    private String str;

    @NewColumn(3)
    @Column
    private double dbl;

    @Column
    @NewColumn(4)
    @Index("other_index_4")
    private String otherNew;
}
