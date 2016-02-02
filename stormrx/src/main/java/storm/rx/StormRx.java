package storm.rx;

import java.util.Collection;

import storm.core.Storm;
import storm.core.StormException;
import storm.core.StormObject;
import storm.db.Database;
import storm.db.DatabaseModule;
import storm.query.Query;
import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormRx extends Storm {

    public static StormRx newInstance(Database.Configuration configuration) {
        return new StormRx(configuration);
    }

    private StormObservablePreprocessor mObservablePreprocessor;

    private StormRx(Database.Configuration configuration) {
        super(configuration);
    }

    // note, that if stream is modified after the final storm method (for ex, flatMap, map, etc),
    // schedulers must be set independently (if this preprocessor is used for setting schedulers, of cause)
    public StormRx registerObservablePreprocessor(StormObservablePreprocessor preprocessor) {
        mObservablePreprocessor = preprocessor;
        return this;
    }


    @Override
    public StormRx registerDatabaseModule(DatabaseModule module) {
        return (StormRx) super.registerDatabaseModule(module);
    }

    @Override
    public StormRx registerDatabaseModules(Collection<? extends DatabaseModule> modules) {
        return (StormRx) super.registerDatabaseModules(modules);
    }

    @Override
    public <T extends StormObject> StormRx registerTable(Class<T> tableClass) {
        return (StormRx) super.registerTable(tableClass);
    }

    public StormObservablePreprocessor observablePreprocessor() {
        if (mObservablePreprocessor == null) {
            return StormObservablePreprocessorNoOp.getInstance();
        }
        return mObservablePreprocessor;
    }


    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table) {
        return new StormQueryRx<>(super.query(table));
    }

    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table, String selection, Object... args) {
        return new StormQueryRx<>(super.query(table, selection, args));
    }

    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table, Selection selection) {
        return new StormQueryRx<>(super.query(table, selection));
    }

    @Override
    public <T extends StormObject> StormQueryRx<T> query(Class<T> table, Query query) {
        return new StormQueryRx<>(super.query(table, query));
    }


    @Override
    public <T extends StormObject> StormSimpleQueryRx<T> simpleQuery(Class<T> table, String column) {
        return new StormSimpleQueryRx<T>(super.simpleQuery(table, column));
    }

    @Override
    public <T extends StormObject> StormSimpleQueryRx<T> simpleQuery(Class<T> table, Query query) {
        return new StormSimpleQueryRx<T>(super.simpleQuery(table, query));
    }


    @Override
    public <T extends StormObject> StormCountRx<T> count(Class<T> table) {
        return new StormCountRx<>(super.count(table));
    }

    @Override
    public <T extends StormObject> StormCountRx<T> count(Class<T> table, Selection selection) {
        return new StormCountRx<>(super.count(table, selection));
    }

    @Override
    public <T extends StormObject> StormCountRx<T> count(Class<T> table, String where, Object... args) {
        return new StormCountRx<>(super.count(table, where, args));
    }

    @Override
    public <T extends StormObject> StormSaveOneRx<T> save(T value) {
        return new StormSaveOneRx<>(super.save(value));
    }

    @Override
    public <T extends StormObject> StormSaveManyRx<T> save(Collection<T> values) throws StormException {
        return new StormSaveManyRx<>(super.save(values));
    }

    @Override
    public <T extends StormObject> StormUpdateOneRx<T> update(T value) {
        return new StormUpdateOneRx<>(super.update(value));
    }

    @Override
    public <T extends StormObject> StormUpdateManyRx<T> update(Collection<T> values) throws StormException {
        return new StormUpdateManyRx<>(super.update(values));
    }

    @Override
    public <T extends StormObject> StormFillRx<T> fill(T value) throws StormException {
        return new StormFillRx<>(super.fill(value));
    }

    @Override
    public <T extends StormObject> StormFillRx<T> fill(T value, Selection selection) throws StormException {
        return new StormFillRx<>(super.fill(value, selection));
    }

    @Override
    public <T extends StormObject> StormFillRx<T> fill(T value, String selection, Object... args) throws StormException {
        return new StormFillRx<>(super.fill(value, selection, args));
    }

    @Override
    public <T extends StormObject> StormDeleteAllRx<T> deleteAll(Class<T> table) {
        return new StormDeleteAllRx<>(super.deleteAll(table));
    }

    @Override
    public <T extends StormObject> StormDeleteRx<T> delete(Class<T> table) {
        return new StormDeleteRx<>(super.delete(table));
    }

    @Override
    public <T extends StormObject> StormDeleteRx<T> delete(Class<T> table, Selection selection) {
        return new StormDeleteRx<>(super.delete(table, selection));
    }

    @Override
    public <T extends StormObject> StormDeleteRx<T> delete(Class<T> table, String where, Object... args) {
        return new StormDeleteRx<>(super.delete(table, where, args));
    }
}
