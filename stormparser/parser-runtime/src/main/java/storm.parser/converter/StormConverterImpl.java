package storm.parser.converter;

import storm.parser.converter.serializer.StormSerializerProvider;

/**
 * Created by Dimitry Ivanov on 26.01.2016.
 */
public abstract class StormConverterImpl<T> implements StormConverter<T> {

    protected final StormConverterInstanceCreatorProvider mInstanceCreatorProvider;
    protected final StormSerializerProvider mSerializerProvider;

    protected StormConverterImpl(
            StormConverterInstanceCreatorProvider instanceCreatorProvider,
            StormSerializerProvider serializerProvider
    ) {
        this.mInstanceCreatorProvider = instanceCreatorProvider;
        this.mSerializerProvider = serializerProvider;
    }
}
