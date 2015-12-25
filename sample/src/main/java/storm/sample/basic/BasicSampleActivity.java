package storm.sample.basic;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import storm.core.Storm;
import storm.core.StormSimpleQuery;
import storm.db.Database;
import storm.iterator.CursorIterator;
import storm.iterator.CursorIteratorCached;
import storm.query.Query;
import storm.query.Selection;
import storm.query.Sorting;
import storm.sample.BaseActivity;

/**
 * Created by Dimitry Ivanov on 25.12.2015.
 */
public class BasicSampleActivity extends BaseActivity {

    @Override
    public void showcase() {

        // to obtain a Storm instance call static method `newInstance`
        // note, that you should manage instances of Storm by yourself (aka storing it somewhere to access)
        // also, there might be as much storm instances as one wish
        // ideally, each Storm instance manages one database
        final Storm storm = Storm.newInstance(new Database.Configuration(getApplicationContext(), "basic.db", 1))
                .registerTable(BasicItem.class);

        // all registrations must be done before executing any of the operations

        // note that all operations are executed synchronously
        // it's always good to have sync methods
        // there is a `rx` module that gives ability to work with every operation as a stream


        // ###############
        // QUERY operation
        // ###############

        // simple, without selection equal to `query all`
        final Cursor queryCursor = storm.query(BasicItem.class).asCursor();

        final BasicItem queryOne = storm.query(BasicItem.class).asOne();

        final List<BasicItem> queryAll = storm.query(BasicItem.class).asList();

        final CursorIterator<BasicItem> queryIterator = storm.query(BasicItem.class).asIterator();

        final CursorIteratorCached<BasicItem> queryCachedIterator = storm.query(BasicItem.class).asCachedIterator(30);

        // each query operation might have a selection
        final BasicItem querySelectionOne1 = storm.query(BasicItem.class)
                .where(Selection.eq("id", 3L))
//                .where("id = ?", 3L) // this won't be a valid query as long as Query builds it's statement as a StringBuilder does
                .asOne();

        final BasicItem querySelectionOne2 = storm.query(BasicItem.class, "id = ?", 5L).asOne();
        final BasicItem querySelectionOne3 = storm.query(BasicItem.class, Selection.eq("id", 5L)).asOne();

        // if you wish to exclude some columns from query
        // or build completely custom query
        final Query query = new Query()
                .select("id", "text")
                .from(storm.tableName(BasicItem.class))
                .where(new Selection().isNotNull("real"))
                .orderBy("real", Sorting.DESC)
                .limit(5);
        final BasicItem querySelectionOne4 = storm.query(BasicItem.class, query).asOne();

        // note that at this point there is already a created Query
        // "SELECT * FROM basic_table"
        // if you wish to customize it, look at `querySelectionOne4` example
        final BasicItem querySelectionOne5 = storm.query(BasicItem.class)
                .asOne(); // here query is executed


        // ######################
        // SIMPLE QUERY operation
        // ######################

        // also there is a possibility to execute a simple query for only one value
        // this operation will execute query as usual, but return only (if present)
        // value from first column at first row
        final long simpleQueryFirstId = storm.simpleQuery(BasicItem.class, "id").asLong();
        final long simpleQueryFirstIdWithDef = storm.simpleQuery(BasicItem.class, "id").asLong(-1L);

        final Query lastIdQuery = new Query()
                .select("id")
                .from(storm.tableName(BasicItem.class))
                .orderBy("id", Sorting.DESC)
                .limit(1);
        final long simpleQueryLastId = storm.simpleQuery(BasicItem.class, lastIdQuery).asLong();
        final long simpleQueryLastIdWithDef = storm.simpleQuery(BasicItem.class, lastIdQuery).asLong(-1L);

        // this operation might be useful for aggregation operations
        // `sum` is a SQLite operation
        final double simpleQuerySum = storm.simpleQuery(BasicItem.class, "sum(real)").asDouble();

        // all SQLite supported types could queried via `simpleQuery`
        final StormSimpleQuery<BasicItem> simpleQuery = storm.simpleQuery(BasicItem.class, "avg(id)");
        simpleQuery.asInt();
        simpleQuery.asLong();
        simpleQuery.asFloat();
        simpleQuery.asDouble();
        simpleQuery.asString();
        simpleQuery.asByteArray();

        // additionally each call takes an optional default value
        // String & byte[] defs = NULL
        // numbers defs = 0
        simpleQuery.asInt(-1);
        simpleQuery.asLong(System.currentTimeMillis());
        simpleQuery.asFloat(Float.NaN);
        simpleQuery.asDouble(Double.POSITIVE_INFINITY);
        simpleQuery.asString("string");
        simpleQuery.asByteArray(new byte[0]);


        // ################
        // COUNT operations
        // ################

        // returns an int with number of rows from executed query

        // this might be useful to determine whether table has rows
        final int countAll = storm.count(BasicItem.class).execute();

        final int countSelection1 = storm.count(BasicItem.class)
                .where() // completely optional call, added only for readability
                .less("id", 10L)
                .and()
                .greaterEquals("id", 2L)
                .execute();

        // might be useful to determine whether such row exists
        final int countSelection2 = storm.count(BasicItem.class, Selection.eq("id", 612L))
                .execute();

        final int countSelection3 = storm.count(BasicItem.class, "id = ?", 777L).execute();


        // ###############
        // SAVE operations
        // ###############

        // executes INSERT operation, to update a row use `update` operation

        // returns an id (if applicable, for ex. if primary key is INTEGER AUTOINCREMENT) of inserted row
        final long saveOneId = storm.save(new BasicItem().setText("text").setReal(66.D))
                .execute();

        final List<BasicItem> saveItems = Arrays.asList(
                new BasicItem().setText("1").setReal(12.D),
                new BasicItem().setText("ho-ho").setReal(-7.9D)
        );

        // returns an array with inserted ids
        final long[] saveManyIds = storm.save(saveItems).execute();


        // ################
        // UPDATE operation
        // ################

        // executes `update` SQLite operation

        // returns number of affected by update rows
        // for a single value update could not be bigger that `1`
        // note that for update to be executed properly
        // updated object must contain a primary key field set
        final int updatedOne = storm.update(new BasicItem().setId(1L).setText(null).setReal(.0D))
                .execute();

        final List<BasicItem> updateManyItems = Arrays.asList(
                new BasicItem().setId(2L), // other values will be NULL
                new BasicItem().setId(3L).setText("updated").setReal(100.D)
        );
        final int updatedMany = storm.update(updateManyItems).execute();


        // ##############
        // FILL operation
        // ##############

        final BasicItem fillItem = new BasicItem();

        // all rows in `basic_table` will be filled with NULLS (except for primary keys)
        final int rowsAffectedByFillOperation1 = storm.fill(fillItem).execute();

        // all rows where column `real` is not null will be filled with NULLs
        final int rowsAffectedByFillOperation2 = storm.fill(fillItem)
                .where()
                .isNotNull("real")
                .execute();

        // all rows where column `text` equals to `some_text` will be filled with NULLs
        final int rowsAffectedByFillOperation3 = storm.fill(fillItem, Selection.eq("text", "some_text"))
                .execute();

        final int rowsAffectedByFillOperation4 = storm.fill(fillItem, "id = ?", 82L).execute();

        // todo, `includeColumns`, `excludeColumns`


        // #################
        // DELETE operations
        // #################

        // all delete operations return number of deleted rows

        // delete all rows from table
        final int deletedAll = storm.deleteAll(BasicItem.class).execute();

        final int deleted1 = storm.delete(BasicItem.class)
                .where()
                .isNull("text")
                .execute();

        final int deleted2 = storm.delete(BasicItem.class, Selection.eq("id", 54L)).execute();
        final int deleted3 = storm.delete(BasicItem.class, "id = ?", 99L).execute();
    }
}
