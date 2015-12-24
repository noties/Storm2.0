package storm.sample.prefill;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
@Table
class PrefillItem implements StormObject {

    @PrimaryKey
    @Column
    private String key;

    @Column
    private String value;

    public PrefillItem() {

    }

    public PrefillItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public PrefillItem setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PrefillItem setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "PrefillItem{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
