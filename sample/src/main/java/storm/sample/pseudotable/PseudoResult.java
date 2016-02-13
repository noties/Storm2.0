package storm.sample.pseudotable;

import storm.annotations.Column;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 08.02.2016.
 */
// we cannot reference generated classes...
//@Table(notificationUri = PseudoSalary_Metadata.NOTIFICATION_URI_STRING)
@Table(notificationUri = PseudoSalary.NOTIFICATION_URI)
class PseudoResult implements StormObject {

    @Column
    @PrimaryKey // every table required a primary key (even pseudo ones)
    long id;

    @Column
    String name;

    @Column
    double amount;

    @Override
    public String toString() {
        return "PseudoResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}
