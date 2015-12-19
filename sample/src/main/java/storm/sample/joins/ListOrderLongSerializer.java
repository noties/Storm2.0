package storm.sample.joins;

import java.util.List;

import storm.query.Selection;
import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
class ListOrderLongSerializer implements StormSerializer<List<Order>, Long> {

    @Override
    public Long serialize(List<Order> orders) {
        // actually we don't need serialization step
        return null;
    }

    @Override
    public List<Order> deserialize(Long aLong) {
        return JoinsSample.sStorm.query(Order.class)
                .where(Selection.eq("personId", aLong))
                .asList();
    }
}
