package storm.parser;

import java.lang.annotation.Annotation;
import java.util.List;

import storm.annotations.Serialize;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
interface StormParserHelper<MAIN, ELEMENT, TYPE> {

    class SerializerNotOfTypeException extends Exception {}
    class SerializerTypeNotSqliteType extends Exception {}
    class SerializerWrongTypeArgumentsException extends Exception {}

    <A extends Annotation> A getMainAnnotation(MAIN main, Class<A> type);
    <A extends Annotation> A getElementAnnotation(ELEMENT element, Class<A> type);

    String getMainSimpleName(MAIN main);
    String getElementSimpleName(ELEMENT element);

    boolean shouldParseElement(ELEMENT element);

    List<ELEMENT> getElements(MAIN main);

    StormType getType(TYPE type);

    TYPE getSerializeType(Serialize serialize)
            throws SerializerNotOfTypeException, SerializerTypeNotSqliteType, SerializerWrongTypeArgumentsException;

    TYPE getSerializeValue(Serialize serialize);

    TYPE getElementType(ELEMENT element);

    boolean isSerializerGeneric(Serialize serialize);
}
