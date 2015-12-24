package storm.scheme;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import storm.annotations.Table;
import storm.serializer.StormSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 24.12.2015.
 */
class StormSchemeTypeApt implements StormSchemeType<TypeElement, Element, TypeMirror> {

    private static final Pattern SERIALIZER_TYPES = Pattern.compile(
            ".+<(.+),\\s*(.+)>"
    );

    private final Types mTypes;
    private final Elements mElements;

    StormSchemeTypeApt(Types types, Elements elements) {
        mTypes = types;
        mElements = elements;
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
        return element.getKind() == ElementKind.FIELD
                && !element.getModifiers().contains(Modifier.TRANSIENT);
    }

    @Override
    public <A> A getMainAnnotation(TypeElement element, Class<? extends Annotation> annotationType) {
        //noinspection unchecked
        return (A) element.getAnnotation(annotationType);
    }

    @Override
    public <A> A getElementAnnotation(Element element, Class<? extends Annotation> annotationType) {
        //noinspection unchecked
        return (A) element.getAnnotation(annotationType);
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
    public StormType parseType(TypeMirror type) {
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
    public TypeMirror getElementType(Element element) {
        return element.asType();
    }

    @Override
    public boolean isOfTypeBoolean(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.BOOLEAN
                || typeMirror.toString().equals("java.lang.Boolean");
    }

    @Override
    public TypeMirror getSerializerType(Serialize serialize) throws SerializerNotOfTypeException, SerializerWrongTypeArgumentsException, SerializerTypeNotSqliteType {

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
        final StormType serializerType = parseType(outMirror);

        if (serializerType == null) {
            throw new SerializerTypeNotSqliteType();
        }

        return outMirror;
    }

    @Override
    public boolean isMainValid(TypeElement element) {
        return true;
    }

    @Override
    public boolean shouldGenerateScheme(TypeElement element) {
        final Table table = element.getAnnotation(Table.class);
        return table.generateScheme();
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
}
