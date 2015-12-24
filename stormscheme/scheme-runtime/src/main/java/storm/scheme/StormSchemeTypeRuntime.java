package storm.scheme;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.reflect.TypeResolverLight;
import storm.serializer.StormSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
class StormSchemeTypeRuntime implements StormSchemeType<Class<?>, Field, Class<?>> {

    @Override
    public String getMainSimpleName(Class<?> main) {
        return main.getSimpleName();
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
    public <A> A getMainAnnotation(Class<?> aClass, Class<? extends Annotation> annotationType) {
        //noinspection unchecked
        return (A) aClass.getAnnotation(annotationType);
    }

    @Override
    public <A> A getElementAnnotation(Field field, Class<? extends Annotation> annotationType) {
        //noinspection unchecked
        return (A) field.getAnnotation(annotationType);
    }

    @Override
    public List<Field> getElements(Class<?> cl) {
        final Field[] fields = cl.getDeclaredFields();
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
    public StormType parseType(Class<?> aClass) {
        return StormType.forValue(aClass);
    }

    @Override
    public Class<?> getElementType(Field field) {
        return field.getType();
    }

    @Override
    public boolean isOfTypeBoolean(Class<?> aClass) {
        return Boolean.TYPE.equals(aClass)
                || Boolean.class.equals(aClass);
    }

    @Override
    public Class<?> getSerializerType(Serialize serialize)
            throws SerializerNotOfTypeException, SerializerWrongTypeArgumentsException, SerializerTypeNotSqliteType {

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
    public boolean isMainValid(Class<?> aClass) {
        final Table table = aClass.getAnnotation(Table.class);
        return table != null;
    }

    @Override
    public boolean shouldGenerateScheme(Class<?> aClass) {
        return true;
    }
}
