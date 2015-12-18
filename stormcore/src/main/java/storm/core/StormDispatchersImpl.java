package storm.core;

/**
 * Created by Dimitry Ivanov on 18.12.2015.
 */
class StormDispatchersImpl implements StormDispatchers {

    private static final StormCountDispatcher COUNT_DISPATCHER = new StormCountDispatcherImpl();
    private static final StormDeleteDispatcher DELETE_DISPATCHER = new StormDeleteDispatcherImpl();
    private static final StormFillDispatcher FILL_DISPATCHER = new StormFillDispatcherImpl();
    private static final StormQueryDispatcher QUERY_DISPATCHER = new StormQueryDispatcherImpl();
    private static final StormSaveManyDispatcher SAVE_MANY_DISPATCHER = new StormSaveManyDispatcherImpl();
    private static final StormSaveOneDispatcher SAVE_ONE_DISPATCHER = new StormSaveOneDispatcherImpl();
    private static final StormUpdateManyDispatcher UPDATE_MANY_DISPATCHER = new StormUpdateManyDispatcherImpl();
    private static final StormUpdateOneDispatcher UPDATE_ONE_DISPATCHER = new StormUpdateOneDispatcherImpl();

    @Override
    public StormCountDispatcher countDispatcher() {
        return COUNT_DISPATCHER;
    }

    @Override
    public StormDeleteDispatcher deleteDispatcher() {
        return DELETE_DISPATCHER;
    }

    @Override
    public StormFillDispatcher fillDispatcher() {
        return FILL_DISPATCHER;
    }

    @Override
    public StormQueryDispatcher queryDispatcher() {
        return QUERY_DISPATCHER;
    }

    @Override
    public StormSaveManyDispatcher saveManyDispatcher() {
        return SAVE_MANY_DISPATCHER;
    }

    @Override
    public StormSaveOneDispatcher saveOneDispatcher() {
        return SAVE_ONE_DISPATCHER;
    }

    @Override
    public StormUpdateOneDispatcher updateOneDispatcher() {
        return UPDATE_ONE_DISPATCHER;
    }

    @Override
    public StormUpdateManyDispatcher updateManyDispatcher() {
        return UPDATE_MANY_DISPATCHER;
    }
}
