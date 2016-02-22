package storm.sample.serialize;

import java.util.List;
import java.util.Map;

import storm.annotations.Column;
import storm.annotations.NewColumn;
import storm.annotations.PrimaryKey;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.core.StormObject;
import storm.lazy.Lazy;
import storm.parser.converter.serializer.BooleanIntSerializer;
import storm.parser.converter.serializer.BooleanStringSerializer;

/**
 * Created by Dimitry Ivanov on 13.02.2016.
 */
@Table
class SerializeItem implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    long id;

    @Column
    @Serialize(BooleanIntSerializer.class)
    boolean intBool;

    @Column
    @Serialize(BooleanStringSerializer.class)
    boolean strBool;

    @Column
    @Serialize(JsonSerializer.class)
    SerializeJsonItem simpleJson;

    @Column
    @Serialize(LazyJsonSerializer.class)
    Lazy<SerializeJsonItem> lazyJson;

    @Column
    @NewColumn(2)
    @Serialize(GenericJsonSerializer.class)
    SerializeJsonItem object;

    @Column
    @NewColumn(3)
    @Serialize(GenericJsonSerializer.class)
    List<List<String>> listListString;

    @Column
    @NewColumn(3)
    @Serialize(GenericLazyJsonSerializer.class)
    Lazy<Map<String, List<Integer>>> lazyMap;

    public long getId() {
        return id;
    }

    public boolean isIntBool() {
        return intBool;
    }

    public SerializeItem setIntBool(boolean intBool) {
        this.intBool = intBool;
        return this;
    }

    public boolean isStrBool() {
        return strBool;
    }

    public SerializeItem setStrBool(boolean strBool) {
        this.strBool = strBool;
        return this;
    }

    public SerializeJsonItem getSimpleJson() {
        return simpleJson;
    }

    public SerializeItem setSimpleJson(SerializeJsonItem simpleJson) {
        this.simpleJson = simpleJson;
        return this;
    }

    public Lazy<SerializeJsonItem> getLazyJson() {
        return lazyJson;
    }

    public SerializeItem setLazyJson(Lazy<SerializeJsonItem> lazyJson) {
        this.lazyJson = lazyJson;
        return this;
    }

    public SerializeJsonItem getObject() {
        return object;
    }

    public SerializeItem setObject(SerializeJsonItem object) {
        this.object = object;
        return this;
    }

    public List<List<String>> getListListString() {
        return listListString;
    }

    public SerializeItem setListListString(List<List<String>> listListString) {
        this.listListString = listListString;
        return this;
    }

    public Lazy<Map<String, List<Integer>>> getLazyMap() {
        return lazyMap;
    }

    public SerializeItem setLazyMap(Lazy<Map<String, List<Integer>>> lazyMap) {
        this.lazyMap = lazyMap;
        return this;
    }
}
