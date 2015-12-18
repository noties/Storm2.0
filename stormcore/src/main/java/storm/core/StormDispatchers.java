package storm.core;

/**
 * Created by Dimitry Ivanov on 18.12.2015.
 */
interface StormDispatchers {

    StormCountDispatcher countDispatcher();
    StormDeleteDispatcher deleteDispatcher();
    StormFillDispatcher fillDispatcher();
    StormQueryDispatcher queryDispatcher();
    StormSaveManyDispatcher saveManyDispatcher();
    StormSaveOneDispatcher saveOneDispatcher();
    StormUpdateOneDispatcher updateOneDispatcher();
    StormUpdateManyDispatcher updateManyDispatcher();

}
