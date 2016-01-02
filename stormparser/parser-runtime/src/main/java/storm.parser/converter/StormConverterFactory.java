package storm.parser.converter;

import java.lang.reflect.Field;

import storm.parser.StormParserItemFactoryBase;
import storm.parser.StormParserTable;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public class StormConverterFactory extends StormParserItemFactoryBase<StormConverter> {

    private final StormConverterInstanceCreatorProvider mCreatorProvider;
    private final StormConverterSerializerProvider mSerializerProvider;

    public StormConverterFactory(
            StormConverterInstanceCreatorProvider instanceCreatorProvider,
            StormConverterSerializerProvider serializerProvider
    ) {
        super(StormConverterAptClassNameBuilder.getInstance());
        this.mCreatorProvider = instanceCreatorProvider;
        this.mSerializerProvider = serializerProvider;
    }

    @Override
    public <T> StormConverter<T> runtime(StormParserTable<Class<?>, Field, Class<?>> table) throws StormConverterException {
        //noinspection unchecked
        return new StormConverterRuntime<>(
                table,
                (StormConverterInstanceCreator<T>) mCreatorProvider.provide(table.getMain()),
                mSerializerProvider
        );
    }
}
