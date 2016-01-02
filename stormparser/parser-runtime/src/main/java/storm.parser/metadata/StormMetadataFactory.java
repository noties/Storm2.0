package storm.parser.metadata;

import java.lang.reflect.Field;

import storm.parser.StormParserException;
import storm.parser.StormParserItemFactoryBase;
import storm.parser.StormParserTable;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormMetadataFactory extends StormParserItemFactoryBase<StormMetadata> {

    public StormMetadataFactory() {
        super(StormMetadataAptClassNameBuilder.getInstance());
    }

    @Override
    public <T> StormMetadata<T> runtime(StormParserTable<Class<?>, Field, Class<?>> table) throws StormParserException {
        return new StormMetadataRuntime<>(
                table.getTableName(),
                false,
                null,
                null,
                null
        );
    }
}
