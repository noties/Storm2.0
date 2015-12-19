package storm.sample.joins;

import storm.annotations.Column;
import storm.annotations.ForeignKey;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
@Table("orders")
public class Order implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    private double price;

    @Column
    private String name;

    @Column
    @ForeignKey(parentTable = "persons", parentColumnName = "id", onDelete = ForeignKey.ForeignKeyAction.CASCADE)
    private long personId;

    public long getId() {
        return id;
    }

    public Order setId(long id) {
        this.id = id;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Order setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getName() {
        return name;
    }

    public Order setName(String name) {
        this.name = name;
        return this;
    }

    public long getPersonId() {
        return personId;
    }

    public Order setPersonId(long personId) {
        this.personId = personId;
        return this;
    }
}
