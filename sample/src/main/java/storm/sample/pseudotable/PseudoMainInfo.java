package storm.sample.pseudotable;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 08.02.2016.
 */
@Table
class PseudoMainInfo implements StormObject {

    @PrimaryKey
    @Column
    long id;

    @Column
    String name;

    // required empty constructor
    PseudoMainInfo() {

    }

    // helper constructor to create objects
    PseudoMainInfo(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
