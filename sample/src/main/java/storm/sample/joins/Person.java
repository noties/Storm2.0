package storm.sample.joins;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
@Table("persons")
public class Person implements StormObject {

    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    public long getId() {
        return id;
    }

    public Person setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Person setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Person setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Person setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
}
