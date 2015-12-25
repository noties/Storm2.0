package storm.scheme;

import java.lang.annotation.Annotation;
import java.util.List;

import storm.annotations.Serialize;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
public interface StormSchemeType<MAIN, ELEMENT, TYPE> {

    class SerializerNotOfTypeException extends Exception {}
    class SerializerTypeNotSqliteType extends Exception {}
    class SerializerWrongTypeArgumentsException extends Exception {}

    String getMainSimpleName(MAIN main);
    String getElementSimpleName(ELEMENT element);

    boolean shouldParseElement(ELEMENT element);

    <A> A getMainAnnotation(MAIN main, Class<? extends Annotation> annotationType);
    <A> A getElementAnnotation(ELEMENT element, Class<? extends Annotation> annotationType);

    List<ELEMENT> getElements(MAIN main);
    StormType parseType(TYPE type);

    TYPE getElementType(ELEMENT element);

    boolean isOfTypeBoolean(TYPE type);

    TYPE getSerializerType(Serialize serialize)
            throws SerializerNotOfTypeException, SerializerWrongTypeArgumentsException, SerializerTypeNotSqliteType;

    // todo, maybe we need only one method for this
    // throwing exception if @Table is NULL & then checking if have `generateScheme` is strange
    boolean isMainValid(MAIN main);
    boolean shouldGenerateScheme(MAIN main);
}
