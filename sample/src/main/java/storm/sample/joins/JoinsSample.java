package storm.sample.joins;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import storm.core.Storm;
import storm.db.Database;
import storm.db.pragma.ForeignKeysPragma;
import storm.db.pragma.PragmasModule;
import storm.query.Query;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
public class JoinsSample {

    // it's really bad way newInstance doing this
    static Storm sStorm;

    public JoinsSample(Context context) {
        sStorm = createStorm(context.getApplicationContext());
    }

    private static Storm createStorm(Context context) {
        return Storm.newInstance(new Database.Configuration(context, "joins_sample", 1))
                .registerDatabaseModule(PragmasModule.newInstance(ForeignKeysPragma.of(true)))
                .registerTable(Person.class)
                .registerTable(Order.class);
    }

    public void populateDatabase() {

        final Person ivan = new Person()
                .setName("Ivan")
                .setEmail("ivan1544@ru")
                .setPhoneNumber(null);

        final long ivanId = sStorm.save(ivan).execute();

        final Order ivanOrder1 = new Order()
                .setName("First Ivan's order")
                .setPersonId(ivanId)
                .setPrice(3.D);

        final Order ivanOrder2 = new Order()
                .setName("Ivan's second order")
                .setPersonId(ivanId)
                .setPrice(23.55D);

        sStorm.save(Arrays.asList(ivanOrder1, ivanOrder2)).execute();
    }

    public void sample() {

        final Query query = new Query()
                .select("id")
                .from(sStorm.tableName(Person.class));

        final List<PersonWithOrders> persons = sStorm.query(PersonWithOrders.class, query)
                .asList();

        for (PersonWithOrders personWithOrders: persons) {

        }
    }
}
