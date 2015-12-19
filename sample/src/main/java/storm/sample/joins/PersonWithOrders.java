package storm.sample.joins;

import java.util.List;

import storm.annotations.Column;
import storm.annotations.Serialize;
import storm.core.StormObject;
import storm.query.Selection;
import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
public class PersonWithOrders implements StormObject {

    static class TotalPriceSerializer implements StormSerializer<Double, Long> {

        @Override
        public Long serialize(Double aDouble) {
            return null;
        }

        @Override
        public Double deserialize(Long aLong) {
            return JoinsSample.sStorm.simpleQuery()
                    .select("sum(price)")
                    .from("orders")
                    .where(Selection.eq("id", aLong))
                    .asDouble();
        }
    }

    @Column("id")
    @Serialize(PersonLongSerializer.class)
    private Person person;

    @Column("id")
    @Serialize(ListOrderLongSerializer.class)
    private List<Order> orders;

    @Column("id")
    @Serialize(TotalPriceSerializer.class)
    private double totalPriceOfOrders;

    public Person getPerson() {
        return person;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public double getTotalPriceOfOrders() {
        return totalPriceOfOrders;
    }
}
