package storm.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.annotations.Serialize;
import storm.reflect.TypeResolverLight;
import storm.serializer.StormSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
class StormParserTypeRuntime implements StormParserType<Class<?>, Field, Class<?>> {

    @Override
    public <A extends Annotation> A getMainAnnotation(Class<?> aClass, Class<A> type) {
        return aClass.getAnnotation(type);
    }

    @Override
    public <A extends Annotation> A getElementAnnotation(Field field, Class<A> type) {
        return field.getAnnotation(type);
    }

    @Override
    public String getMainSimpleName(Class<?> aClass) {
        return aClass.getSimpleName();
    }

    @Override
    public String getElementSimpleName(Field field) {
        return field.getName();
    }

    @Override
    public boolean shouldParseElement(Field field) {
        return !Modifier.isTransient(field.getModifiers());
    }

    @Override
    public List<Field> getElements(Class<?> aClass) {
        final Field[] fields = aClass.getDeclaredFields();
        final int size = fields != null ? fields.length : 0;
        if (size == 0) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }

        final List<Field> list = new ArrayList<>(size);
        for (Field field: fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            list.add(field);
        }

        return list;
    }

    @Override
    public StormType getType(Class<?> aClass) {
        return StormType.forValue(aClass);
    }

    @Override
    public Class<?> getSerializeType(Serialize serialize) {
        final Class<?>[] typeArgs = TypeResolverLight.resolveRawArguments(StormSerializer.class, serialize.value());
        return typeArgs[1];
    }

    @Override
    public Class<?> getSerializeValue(Serialize serialize) {
        return serialize.value();
    }

    @Override
    public Class<?> getElementType(Field field) {
        return field.getType();
    }
}
