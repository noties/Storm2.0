package storm.sample.pseudotable;

import storm.annotations.Column;
import storm.annotations.ForeignKey;
import storm.annotations.PrimaryKey;
import storm.annotations.Table;
import storm.core.StormObject;

/**
 * Created by Dimitry Ivanov on 08.02.2016.
 */
@Table(notificationUri = PseudoSalary.NOTIFICATION_URI)
class PseudoSalary implements StormObject {

    static final String NOTIFICATION_URI = "pseudo://salary";

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
//    @ForeignKey(
//            parentTable = PseudoMainInfo_Metadata.TABLE_NAME,
//            parentColumnName = PseudoMainInfo_Metadata.COL_ID,
//            onDelete = ForeignKey.ForeignKeyAction.CASCADE
//    )
    long mainInfoId;

    @Column
    int month;

    @Column
    double amount;

    // required empty constructor
    PseudoSalary() {

    }

    // helper constructor to create objects
    PseudoSalary(long mainInfoId, int month, double amount) {
        this.mainInfoId = mainInfoId;
        this.month = month;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PseudoSalary{" +
                "id=" + id +
                ", mainInfoId=" + mainInfoId +
                ", month=" + month +
                ", amount=" + amount +
                '}';
    }
}
