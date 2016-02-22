package storm.parser;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import storm.annotations.Serialize;
import storm.parser.converter.serializer.StormSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserHelperApt implements StormParserHelper<TypeElement, Element, TypeMirror> {

    private static final Pattern SERIALIZER_TYPES = Pattern.compile(
            ".+<(.+),\\s*(.+)>"
    );

    private final Types mTypes;
    private final Elements mElements;

    public StormParserHelperApt(Types types, Elements elements) {
        this.mTypes = types;
        this.mElements = elements;
    }

    @Override
    public <A extends Annotation> A getMainAnnotation(TypeElement element, Class<A> aClass) {
        return element.getAnnotation(aClass);
    }

    @Override
    public <A extends Annotation> A getElementAnnotation(Element element, Class<A> aClass) {
        return element.getAnnotation(aClass);
    }

    @Override
    public String getMainSimpleName(TypeElement element) {
        return element.getSimpleName().toString();
    }

    @Override
    public String getElementSimpleName(Element element) {
        return element.getSimpleName().toString();
    }

    @Override
    public boolean shouldParseElement(Element element) {
        if (element.getKind() != ElementKind.FIELD) {
            return false;
        }
        final Set<Modifier> modifiers = element.getModifiers();
        return !(modifiers.contains(Modifier.TRANSIENT)
                || modifiers.contains(Modifier.STATIC)
                || modifiers.contains(Modifier.FINAL));
    }

    @Override
    public List<Element> getElements(TypeElement element) {
        final List<? extends Element> elements = element.getEnclosedElements();
        if (elements == null
                || elements.size() == 0) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }

        return new ArrayList<>(elements);
    }

    @Override
    public StormType getType(TypeMirror type) {
        final TypeKind kind = type.getKind();
        switch (kind) {

            case INT:
                return StormType.INT;
            case LONG:
                return StormType.LONG;
            case FLOAT:
                return StormType.FLOAT;
            case DOUBLE:
                return StormType.DOUBLE;

            // only byte[]
            case ARRAY:
                if (((ArrayType) type).getComponentType().getKind() == TypeKind.BYTE) {
                    return StormType.BYTE_ARRAY;
                }
                return null;

            case DECLARED:
                final String name = type.toString();
                if ("java.lang.String".equals(name)) {
                    return StormType.STRING;
                } else if ("java.lang.Integer".equals(name)) {
                    return StormType.INT;
                } else if ("java.lang.Long".equals(name)) {
                    return StormType.LONG;
                } else if ("java.lang.Float".equals(name)) {
                    return StormType.FLOAT;
                } else if ("java.lang.Double".equals(name)) {
                    return StormType.DOUBLE;
                }
                return null;

            default:
                return null;
        }
    }

    @Override
    public TypeMirror getSerializeType(Serialize serialize) throws SerializerNotOfTypeException, SerializerTypeNotSqliteType, SerializerWrongTypeArgumentsException {
        TypeMirror typeMirror = null;
        try {
            serialize.value();
        } catch (MirroredTypeException e) {
            typeMirror = e.getTypeMirror();
        }

        if (typeMirror == null) {
            throw new RuntimeException("Unexpected exception. Could not parse TypeMirror");
        }

        final TypeMirror serializer = extractSerializerInterface(typeMirror);
        if (serializer == null) {
            throw new SerializerNotOfTypeException();
        }

        final Matcher matcher = SERIALIZER_TYPES.matcher(serializer.toString());
        if (!matcher.matches()) {
            throw new SerializerWrongTypeArgumentsException();
        }

        final String out = matcher.group(2);

        final TypeElement outType = mElements.getTypeElement(out);
        if (outType == null) {
            throw new SerializerWrongTypeArgumentsException();
        }

        final TypeMirror outMirror = outType.asType();
        final StormType serializerType = getType(outMirror);

        if (serializerType == null) {
            throw new SerializerTypeNotSqliteType();
        }

        return outMirror;
    }

    @Override
    public TypeMirror getSerializeValue(Serialize serialize) {
        try {
            final Class<?> cl = serialize.value();
            return mElements.getTypeElement(cl.getName()).asType();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
    }

    @Override
    public TypeMirror getElementType(Element element) {
        return element.asType();
    }

    @Override
    public boolean isSerializerGeneric(Serialize serialize) {
        TypeMirror typeMirror = null;
        try {
            serialize.value();
        } catch (MirroredTypeException e) {
            typeMirror = e.getTypeMirror();
        }

        if (typeMirror == null) {
            throw new RuntimeException("Unexpected exception. Could not parse TypeMirror");
        }


        final TypeMirror serializer = extractSerializerInterface(typeMirror);
        if (serializer == null) {
            throw new RuntimeException("Unexpected exception");
        }

        final String type = typeFirstArgument(serializer.toString());
        if (type == null) {
            throw new RuntimeException("Unexpected exception");
        }

        if (type.contains("<")) {
            return false;
        }

        final TypeElement outType = mElements.getTypeElement(type);
        return outType == null;
    }

    private TypeMirror extractSerializerInterface(TypeMirror type) {

        final String object = "java.lang.Object";

        TypeElement typeElement = (TypeElement) mTypes.asElement(type);

        // a super class for this class is not object
        if (!typeElement.getSuperclass().toString().equals(object)) {
            TypeMirror serializerInterface;
            while (!typeElement.getSuperclass().toString().equals(object)) {
                serializerInterface = extractSerializerInterface(typeElement);
                if (serializerInterface != null) {
                    return serializerInterface;
                }
                typeElement = (TypeElement) mTypes.asElement(typeElement.getSuperclass());
            }
        }

        return extractSerializerInterface(typeElement);
    }

    private TypeMirror extractSerializerInterface(TypeElement element) {
        final List<? extends TypeMirror> interfaces = element.getInterfaces();
        if (interfaces == null
                || interfaces.size() == 0) {
            return null;
        }

        final String serializerName = StormSerializer.class.getName();
        for (TypeMirror type: interfaces) {
            if (type.toString().startsWith(serializerName)) {
                return type;
            }
        }
        return null;
    }

    // return null if there are no types
    private static String typeFirstArgument(String type) {
        // we need first `<`, then last `,`
        final int openIndex = type.indexOf('<');
        if (openIndex < 0) {
            return null;
        }
        final int lastCommaIndex = type.lastIndexOf(',');
        if (lastCommaIndex < 0) {
            return null;
        }

        return type.substring(openIndex + 1, lastCommaIndex);
    }
}
