package storm.sample.keyvalue;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 28.02.2016.
 */
@Table("key_value_table")
class StormKvItem implements StormObject {

    @Column
    @PrimaryKey(autoincrement = false)
    String key;

    @Column
    String value;

    StormKvItem() {

    }

    StormKvItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public StormKvItem setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public StormKvItem setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "StormKvItem{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
