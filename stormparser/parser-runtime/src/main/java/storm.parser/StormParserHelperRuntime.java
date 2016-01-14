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
class StormParserHelperRuntime implements StormParserHelper<Class<?>, Field, Class<?>> {

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
    public Class<?> getSerializeType(Serialize serialize)
            throws SerializerNotOfTypeException, SerializerTypeNotSqliteType, SerializerWrongTypeArgumentsException {

        // obtain serializer class
        final Class<?> serializerClass = serialize.value();

        // check if it implements `StormSerializer`
        if (!StormSerializer.class.isAssignableFrom(serializerClass)) {
            throw new SerializerNotOfTypeException();
        }

        // obtain type parameters & check them
        // IN should be of field's type & OUT should SQLite supported type
        final Class<?>[] typeParams = TypeResolverLight.resolveRawArguments(StormSerializer.class, serializerClass);
        if (TypeResolverLight.Unknown.class.equals(typeParams[0])
                || TypeResolverLight.Unknown.class.equals(typeParams[1])) {
            throw new SerializerWrongTypeArgumentsException();
        }

        // now, check if OUT is supported
        final StormType type = StormType.forValue(typeParams[1]);
        if (type == null) {
            throw new SerializerTypeNotSqliteType();
        }

        return typeParams[1];
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
