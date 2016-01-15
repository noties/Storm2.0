package storm.parser.metadata;

import java.lang.reflect.Field;

import storm.parser.StormParserColumn;
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

        final StormParserColumn<Field, Class<?>> primaryKeyColumn = findPrimaryKeyColumn(table);
        if (primaryKeyColumn == null) {
            throw StormParserException.newInstance("Cannot find primary key for a table defined in class: `%s`", table.getMain().getName());
        }

        return new StormMetadataRuntime<>(
                table.getTableName(),
                primaryKeyColumn.isAutoIncrement(),
                StormNotificationUriBuilder.getDefault(table.getMain(), table.getCustomNotificationUri()),
                primaryKeyColumn.getName(),
                primaryKeyColumn.getElement()
        );
    }

    private static StormParserColumn<Field, Class<?>> findPrimaryKeyColumn(StormParserTable<Class<?>, Field, Class<?>> table) {
        for (StormParserColumn<Field, Class<?>> column: table.getElements()) {
            if (column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }
}
