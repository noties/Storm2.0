package storm.core;

import storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 17.12.2015.
 */
public class StormDeleteAll<T extends StormObject> implements StormOp<T> {

    private final Storm mStorm;
    private final Class<T> mTable;
    private final Selection mSelection;
    private final StormDeleteDispatcher mDeleteDispatcher;

    protected StormDeleteAll(Storm storm, Class<T> table, Selection selection, StormDeleteDispatcher deleteDispatcher) {
        mStorm = storm;
        mTable = table;
        mSelection = selection;
        mDeleteDispatcher = deleteDispatcher;
    }

    public int execute() {
        return mDeleteDispatcher.execute(mStorm, mTable, mSelection);
    }

    @Override
    public Storm storm() {
        return mStorm;
    }

    @Override
    public Class<T> table() {
        return mTable;
    }

    @Override
    public StormDeleteDispatcher dispatcher() {
        return mDeleteDispatcher;
    }

    public Selection selection() {
        return mSelection;
    }
}
