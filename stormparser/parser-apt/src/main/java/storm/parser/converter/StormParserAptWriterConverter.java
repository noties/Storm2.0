package storm.parser.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import storm.parser.Indent;
import storm.parser.StormParserAptData;
import storm.parser.StormParserAptWriterBase;
import storm.parser.StormParserColumn;
import storm.parser.StormParserTable;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptWriterConverter extends StormParserAptWriterBase {

    private static final String CURSOR_INDEXES_CLASS = "CursorIndexes";
    private static final String CURSOR_CLASS = "android.database.Cursor";
    private static final String SERIALIZERS_CLASS = "Serializers";
    private static final String CONTENT_VALUES_CLASS = "android.content.ContentValues";

    private static final String LIST_CLASS = List.class.getName() + "<%s>";
    private static final String ARRAY_LIST_CLASS = ArrayList.class.getName() + "<%s>";
    private static final String COLLECTION_CLASS = Collection.class.getName() + "<%s>";

    private static final String PARSE_METHOD_NAME = "parse";
    private static final String TO_CONTENT_VALUES_GENERIC_METHOD_NAME = "toContentValuesInner";

    private static final String TYPE_CLASS_NAME = Type.class.getName();
    private static final String PARAMETERIZED_CLASS_IMPL_NAME = "ParameterizedImpl";

    public StormParserAptWriterConverter(Elements elements, Filer filer) {
        super(elements, filer, StormConverterAptClassNameBuilder.getInstance());
    }

    @Override
    protected boolean shouldCreateSourceCodeFile(StormParserAptData data) {
        return data.converter();
    }

    @Override
    protected String getSourceCode(String packageName, String className, String type, StormParserAptData data) throws Throwable {

        final Indent indent = indent();
        final StringBuilder builder = new StringBuilder();

        // package name
        builder.append("package ")
                .append(packageName)
                .append(";\n\n");

        // imports

        // class definition
        builder.append("public class ")
                .append(className)
                .append(" implements ")
                .append(StormConverter.class.getName())
                .append("<")
                .append(type)
                .append("> {\n\n");

        indent.increment();

        // cursor indexes class
        builder.append(cursorIndexesClass(indent, data.getTable()))
                .append("\n")
                .append(indent);

        // serializers class
        if (hasSerializers(data)) {
            builder.append(serializersClass(indent, data))
                    .append("\n\n")
                    .append(indent);
        }

        // additional type params
        if (hasSerializerWithParameterizedType(data)) {
            // common class for use
            builder.append(parameterizedClass(indent));
            builder.append(parameterizedConsts(indent, data));
        }

        // generic parse method
        builder.append(genericParseMethod(indent, type, data));

        // `fromCursor` method
        builder.append(fromCursorMethod(indent, type, data));

        // `fromCursorList` method
        builder.append(fromCursorListMethod(indent, type, data));

        // `toContentValues` method
        builder.append(toContentValuesGenericMethod(indent, type, data));
        builder.append(toContentValuesListMethod(indent, type, data));
        builder.append(toContentValuesMethod(indent, type, data));

        builder.append("}\n");

        return builder.toString();
    }

    private static String cursorIndexesClass(Indent indent, StormParserTable table) {

        //noinspection unchecked
        final List<StormParserColumn> columns = table.getElements();

        final StringBuilder builder = new StringBuilder();

        // class definition
        builder.append(indent)
                .append("private static class ")
                .append(CURSOR_INDEXES_CLASS)
                .append(" {\n");

        indent.increment();

        for (StormParserColumn column: columns) {
            builder.append(indent)
                    .append("final int ")
                    .append(column.getName()) // this bit is confusing... can a column name contain spaces?
                    .append(";\n");
        }

        final String cursorName = "c";

        // constructor
        builder.append(indent)
                .append(CURSOR_INDEXES_CLASS)
                .append("(")
                .append(CURSOR_CLASS)
                .append(" ")
                .append(cursorName)
                .append(") {\n");

        indent.increment();

        for (StormParserColumn column: columns) {
            builder.append(indent)
                    .append("this.")
                    .append(column.getName())
                    .append(" = ")
                    .append(cursorName)
                    .append(".getColumnIndex(\"")
                    .append(column.getName())
                    .append("\");\n");
        }

        builder.append(indent.decrement())
                .append("}\n")
                .append(indent.decrement())
                .append("}\n");

        return builder.toString();
    }

    private static boolean hasSerializers(StormParserAptData data) {
        for (StormParserColumn<Element, TypeMirror> column: data.getTable().getElements()) {
            if (column.getSerializerType() != null) {
                return true;
            }
        }
        return false;
    }

    private static String serializersClass(Indent indent, StormParserAptData data) {

        // if we have no serializers, no

        final StringBuilder builder = new StringBuilder();

        builder.append("private static class ")
                .append(SERIALIZERS_CLASS)
                .append(" {");

        indent.increment();

//        final Set<StormParserColumn<Element, TypeMirror>> columnsWithSerializers = new HashSet<>();
        final Set<SerializerItem> elements = new HashSet<>();

        for (StormParserColumn<Element, TypeMirror> column: data.getTable().getElements()) {
            if (column.getSerializerType() != null) {
//                columnsWithSerializers.add(column);
                elements.add(new SerializerItem(
                        serializerType(column.getSerializerType()),
                        serializerFieldName(column.getSerializerType())
                ));
            }
        }

        // fields
//        for (StormParserColumn<Element, TypeMirror> column: columnsWithSerializers) {
//            builder.append("\n")
//                    .append(indent)
//                    .append("final ")
//                    .append(serializerType(column))
//                    .append(" ")
//                    .append(serializerFieldName(column.getSerializerType()))
//                    .append(";");
//        }
        for (SerializerItem item: elements) {
            builder.append("\n")
                    .append(indent)
                    .append("final ")
                    .append(item.type)
                    .append(" ")
                    .append(item.fieldName)
                    .append(";");
        }

        // constructor
        builder.append("\n")
                .append(indent)
                .append(SERIALIZERS_CLASS)
                .append("() {");

        indent.increment();

//        for (StormParserColumn<Element, TypeMirror> column: columnsWithSerializers) {
//            builder.append("\n")
//                    .append(indent)
//                    .append("this.")
//                    .append(serializerFieldName(column.getSerializerType()))
//                    .append(" = new ")
//                    .append(serializerType(column))
//                    .append("();");
//        }
        for (SerializerItem item: elements) {
            builder.append("\n")
                    .append(indent)
                    .append("this.")
                    .append(item.fieldName)
                    .append(" = new ")
                    .append(item.type)
                    .append("();");
        }

        builder.append("\n")
                .append(indent.decrement())
                .append("}");

        builder.append("\n")
                .append(indent.decrement())
                .append("}");

        return builder.toString();
    }

    private static String serializerType(TypeMirror mirror) {
//        if (column.isSerializerGeneric()) {
//            return String.format("%s<%s>", column.getSerializerType(), column.getElement().asType().toString());
//        }
        return rawType(mirror);
    }

    private static class SerializerItem {
        private final String type;
        private final String fieldName;
        SerializerItem(String type, String fieldName) {
            this.type = type;
            this.fieldName = fieldName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SerializerItem that = (SerializerItem) o;

            if (type != null ? !type.equals(that.type) : that.type != null) return false;
            return !(fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null);

        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
            return result;
        }
    }


    private static String genericParseMethod(Indent indent, String type, StormParserAptData data) {

        final boolean hasSerializers = hasSerializers(data);

        final StringBuilder builder = new StringBuilder();

        final String varName = "value";
        final String cursorVarName = "c";
        final String cursorIndexesVarName = "ci";
        final String serializersVarName = "s";

        final String ifHasIndex = "if (" + cursorIndexesVarName + ".%s > -1)";
        final String cursorGetValue = cursorVarName + ".%s(" + cursorIndexesVarName + ".%s)";
        final String serializersGetValue = serializersVarName + ".%s.deserialize(%s, %s)";

        builder.append("static ")
                .append(type)
                .append(" ")
                .append(PARSE_METHOD_NAME)
                .append("(")
                .append(CURSOR_CLASS)
                .append(" ")
                .append(cursorVarName)
                .append(", ")
                .append(CURSOR_INDEXES_CLASS)
                .append(" ")
                .append(cursorIndexesVarName);

        // if we have serializers change method
        if (hasSerializers) {
            builder.append(", ")
                    .append(SERIALIZERS_CLASS)
                    .append(" ")
                    .append(serializersVarName);
        }

        builder.append(") {\n")
                .append(indent.increment());

        // create new instance of parsed class
        builder.append("final ")
                .append(type)
                .append(" ")
                .append(varName)
                .append(" = new ")
                .append(type)
                .append("();");

        String fieldName;

        for (StormParserColumn<Element, TypeMirror> column: data.getTable().getElements()) {
            fieldName = column.getElement().getSimpleName().toString();

            builder.append("\n")
                    .append(indent)
                    .append(String.format(ifHasIndex, column.getName()))
                    .append(" {\n")
                    .append(indent.increment());

            final String getFromCursor = String.format(cursorGetValue, cursorMethodGet(column.getType()), column.getName());

            // check if we have serializer
            final String init;
            if (column.getSerializerType() != null) {

                final TypeMirror typeMirror = column.getElement().asType();
                final String fieldType;
                if (isParameterizedType(typeMirror)) {
                    fieldType = parametrizedTypeConstName(typeMirror);
                } else {
                    fieldType = column.getElement().asType().toString() + ".class";
                }

                final String serializerCast;
                if (column.isSerializerGeneric()) {
                    serializerCast = "(" + column.getElement().asType() + ") ";
                } else {
                    serializerCast = "";
                }

                // if we have serializer
                init = serializerCast + String.format(
                        serializersGetValue,
                        serializerFieldName(column.getSerializerType()),
                        fieldType,
                        getFromCursor
                );
            } else {
                init = getFromCursor;
            }

            builder.append(varName)
                    .append(".")
                    .append(fieldName)
                    .append(" = ")
                    .append(init)
                    .append(";\n");

            builder.append(indent.decrement())
                    .append("}");
        }

        builder.append("\n")
                .append(indent)
                .append("return ")
                .append(varName)
                .append(";")
                .append("\n")
                .append(indent.decrement())
                .append("}\n");

        return builder.toString();
    }

    private static String parameterizedClass(Indent indent) {

        final StringBuilder builder = new StringBuilder();

        final String rawTypeVar = "rawType";
        final String typeArgsVar = "typeArgs";

        builder.append("private static class ")
                .append(PARAMETERIZED_CLASS_IMPL_NAME)
                .append(" implements ")
                .append(ParameterizedType.class.getName())
                .append(" {\n")
                .append(indent.increment());

        // fields
        builder.append("private final ")
                .append(TYPE_CLASS_NAME)
                .append(" ")
                .append(rawTypeVar)
                .append(";\n")
                .append(indent)
                .append("private final ")
                .append(TYPE_CLASS_NAME)
                .append("[] ")
                .append(typeArgsVar)
                .append(";\n")
                .append(indent);

        // constructor
        builder.append(PARAMETERIZED_CLASS_IMPL_NAME)
                .append("(")
                .append(TYPE_CLASS_NAME)
                .append(" ")
                .append(rawTypeVar)
                .append(", ")
                .append(TYPE_CLASS_NAME)
                .append("... ")
                .append(typeArgsVar)
                .append(") {\n")
                .append(indent.increment())
                .append("this.")
                .append(rawTypeVar)
                .append(" = ")
                .append(rawTypeVar)
                .append(";\n")
                .append(indent)
                .append("this.")
                .append(typeArgsVar)
                .append(" = ")
                .append(typeArgsVar)
                .append(";\n")
                .append(indent.decrement())
                .append("}\n")
                .append(indent);

        // requiredMethods
        // Type getRawType();
        builder.append("public ")
                .append(TYPE_CLASS_NAME)
                .append(" getRawType() { return ")
                .append(rawTypeVar)
                .append("; }\n")
                .append(indent);

        // Type[] getActualTypeArguments();
        builder.append("public ")
                .append(TYPE_CLASS_NAME)
                .append("[] getActualTypeArguments() { return ")
                .append(typeArgsVar)
                .append("; }\n")
                .append(indent);

        // Type getOwnerType();
        builder.append("public ")
                .append(TYPE_CLASS_NAME)
                .append(" getOwnerType() { return null; }\n");

        builder.append(indent.decrement())
                .append("}\n\n")
                .append(indent);

        return builder.toString();
    }

    private static String parameterizedConsts(Indent indent, StormParserAptData data) {

        final StringBuilder builder = new StringBuilder();

        TypeMirror mirror;

        for (StormParserColumn<Element, TypeMirror> column: data.getTable().getElements()) {
            mirror = column.getElement().asType();
            if (isParameterizedType(mirror)) {
                builder.append("private static final ")
                        .append(TYPE_CLASS_NAME)
                        .append(" ")
                        .append(parametrizedTypeConstName(mirror))
                        .append(" = ")
                        .append(parameterizedConst(mirror))
                        .append(";\n")
                        .append(indent);
            }
        }

        builder.append("\n\n")
                .append(indent);

        return builder.toString();
    }

    private static String parameterizedConst(TypeMirror typeMirror) {

        if (isParameterizedType(typeMirror)) {

            final List<? extends TypeMirror> typeArgs = ((DeclaredType) typeMirror).getTypeArguments();
            final StringBuilder builder = new StringBuilder()
                    .append("new ")
                    .append(PARAMETERIZED_CLASS_IMPL_NAME)
                    .append("(")
                    .append(rawType(typeMirror))
                    .append(".class");

            for (TypeMirror mirror: typeArgs) {
                builder.append(", ")
                        .append(parameterizedConst(mirror));
            }

            builder.append(")");

            return builder.toString();
        }

        return typeMirror.toString() + ".class";
    }

    private static String rawType(TypeMirror mirror) {
        final String value = mirror.toString();
        final int index = value.indexOf("<");
        if (index > -1) {
            return value.substring(0, index);
        }
        return value;
    }

    private static boolean hasSerializerWithParameterizedType(StormParserAptData data) {
        for (StormParserColumn<Element, TypeMirror> column: data.getTable().getElements()) {
            if (column.getSerializerType() != null) {
                final TypeMirror typeMirror = column.getElement().asType();
                if (isParameterizedType(typeMirror)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isParameterizedType(TypeMirror mirror) {
        if (!(mirror instanceof DeclaredType)) {
            return false;
        }
        final DeclaredType declaredType = (DeclaredType) mirror;
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        return typeArguments != null && typeArguments.size() > 0;
    }

    private static String parametrizedTypeConstName(TypeMirror typeMirror) {
        final String type = typeMirror.toString();
        final StringBuilder builder = new StringBuilder();
        char c;
        for (int i = 0, count = type.length(); i < count; i++) {
            c = type.charAt(i);
            if (Character.isLetter(c)) {
                builder.append(Character.toUpperCase(c));
            } else {
                builder.append("_");
            }
        }
        return builder.toString();
    }

    private static String cursorMethodGet(StormType type) {
        switch (type) {

            case INT:
                return "getInt";

            case LONG:
                return "getLong";

            case FLOAT:
                return "getFloat";

            case DOUBLE:
                return "getDouble";

            case STRING:
                return "getString";

            case BYTE_ARRAY:
                return "getBlob";
        }

        throw new IllegalStateException("Unknown type: " + type);
    }

    private static String serializerFieldName(TypeMirror typeMirror) {
        final String simpleName = ((DeclaredType) typeMirror).asElement().getSimpleName().toString();
        return Character.toLowerCase(simpleName.charAt(0))
                + simpleName.substring(1);
    }

    private static String fromCursorMethod(Indent indent, String type, StormParserAptData data) {

        final StringBuilder builder = new StringBuilder();

        final String cursorVarName = "c";

        builder.append(indent)
                .append("public ")
                .append(type)
                .append(" fromCursor(")
                .append(CURSOR_CLASS)
                .append(" ")
                .append(cursorVarName)
                .append(") {\n")
                .append(indent.increment())
                .append("return ")
                .append(PARSE_METHOD_NAME)
                .append("(")
                .append(cursorVarName)
                .append(", new ")
                .append(CURSOR_INDEXES_CLASS)
                .append("(")
                .append(cursorVarName)
                .append(")");

        if (hasSerializers(data)) {
            builder.append(", new ")
                    .append(SERIALIZERS_CLASS)
                    .append("()");
        }

        builder.append(");\n")
                .append(indent.decrement())
                .append("}\n\n")
                .append(indent);

        return builder.toString();
    }

    private static String fromCursorListMethod(Indent indent, String type, StormParserAptData data) {

        final StringBuilder builder = new StringBuilder();

        final String cursorVarName = "c";
        final String cursorIndexesVarName = "ci";
        final String serializersVarName = "s";
        final String listVarName = "list";
        final String cursorCountVarName = "count";

        final boolean hasSerializers = hasSerializers(data);

        builder.append("public ")
                .append(String.format(LIST_CLASS, type))
                .append(" fromCursorList(")
                .append(CURSOR_CLASS)
                .append(" ")
                .append(cursorVarName)
                .append(") {\n")
                .append(indent.increment());

        // cursor count
        builder.append("final int ")
                .append(cursorCountVarName)
                .append(" = ")
                .append(cursorVarName)
                .append(".getCount();\n");

        // list
        builder.append(indent)
                .append("final ")
                .append(String.format(LIST_CLASS, type))
                .append(" ")
                .append(listVarName)
                .append(" = new ")
                .append(String.format(ARRAY_LIST_CLASS, type))
                .append("(")
                .append(cursorCountVarName)
                .append(");\n");

        // indexes
        builder.append(indent)
                .append("final ")
                .append(CURSOR_INDEXES_CLASS)
                .append(" ")
                .append(cursorIndexesVarName)
                .append(" = new ")
                .append(CURSOR_INDEXES_CLASS)
                .append("(")
                .append(cursorVarName)
                .append(");\n");

        if (hasSerializers) {
            builder.append(indent)
                    .append("final ")
                    .append(SERIALIZERS_CLASS)
                    .append(" ")
                    .append(serializersVarName)
                    .append(" = new ")
                    .append(SERIALIZERS_CLASS)
                    .append("();\n");
        }

        // while loop
        builder.append(indent)
                .append("while (!")
                .append(cursorVarName)
                .append(".isAfterLast()) {\n")
                .append(indent.increment())
                .append(listVarName)
                .append(".add(")
                .append(PARSE_METHOD_NAME)
                .append("(")
                .append(cursorVarName)
                .append(", ")
                .append(cursorIndexesVarName);

        if (hasSerializers) {
            builder.append(", ")
                    .append(serializersVarName);
        }

        builder.append("));\n")
                .append(indent)
                .append(cursorVarName)
                .append(".moveToNext();\n")
                .append(indent.decrement())
                .append("}\n")
                .append(indent)
                .append("return ")
                .append(listVarName)
                .append(";\n")
                .append(indent.decrement())
                .append("}\n\n")
                .append(indent);

        return builder.toString();
    }

    private static String toContentValuesGenericMethod(Indent indent, String type, StormParserAptData data) {

        final StringBuilder builder = new StringBuilder();

        final List<StormParserColumn<Element, TypeMirror>> columns = data.getTable().getElements();

        final boolean hasSerializers = hasSerializers(data);

        final String varName = "value";
        final String putPrimaryKeyVarName = "putPrimaryKey";
        final String serializersVarName = "s";
        final String columnsCountVarName = "count";
        final String cvVarName = "cv";

        builder.append("static ")
                .append(CONTENT_VALUES_CLASS)
                .append(" ")
                .append(TO_CONTENT_VALUES_GENERIC_METHOD_NAME)
                .append("(");

        if (hasSerializers) {
            builder.append(SERIALIZERS_CLASS)
                    .append(" ")
                    .append(serializersVarName)
                    .append(", ");
        }

        builder.append(type)
                .append(" ")
                .append(varName)
                .append(", boolean ")
                .append(putPrimaryKeyVarName)
                .append(") {\n");

        indent.increment();

//        if (hasSerializers(data)) {
//            builder.append(indent)
//                    .append("final ")
//                    .append(SERIALIZERS_CLASS)
//                    .append(" ")
//                    .append(serializersVarName)
//                    .append(" = new ")
//                    .append(SERIALIZERS_CLASS)
//                    .append("();\n");
//        }

        builder.append(indent)
                .append("final int ")
                .append(columnsCountVarName)
                .append(" = ")
                .append(putPrimaryKeyVarName)
                .append(" ? ")
                .append(columns.size())
                .append(" : ")
                .append(columns.size() - 1)
                .append(";\n");

        builder.append(indent)
                .append("final ")
                .append(CONTENT_VALUES_CLASS)
                .append(" ")
                .append(cvVarName)
                .append(" = new ")
                .append(CONTENT_VALUES_CLASS)
                .append("(")
                .append(columnsCountVarName)
                .append(");");

        for (StormParserColumn<Element, TypeMirror> column: columns) {

            builder.append("\n")
                    .append(indent);

            final boolean isPrimaryKey = column.isPrimaryKey();

            if (isPrimaryKey) {
                builder.append("if (")
                        .append(putPrimaryKeyVarName)
                        .append(") {\n")
                        .append(indent.increment());
            }

            final String fieldName = column.getElement().getSimpleName().toString();

            final String value;
            if (column.getSerializerType() != null) {
                value = serializersVarName + "." + serializerFieldName(column.getSerializerType())
                        + ".serialize(" + varName + "." + fieldName + ")";
            } else {
                value = varName + "." + fieldName;
            }

            builder.append(cvVarName)
                    .append(".put(\"")
                    .append(column.getName())
                    .append("\", ")
                    .append(value)
                    .append(");");

            if (isPrimaryKey) {
                builder.append("\n")
                        .append(indent.decrement())
                        .append("}");
            }
        }

        builder.append("\n")
                .append(indent)
                .append("return ")
                .append(cvVarName)
                .append(";\n")
                .append(indent.decrement())
                .append("}\n\n")
                .append(indent);

        return builder.toString();
    }

    private static String toContentValuesListMethod(Indent indent, String type, StormParserAptData data) {

        final StringBuilder builder = new StringBuilder();
//        final List<StormParserColumn<Element, TypeMirror>> columns = data.getTable().getElements();

        final String valuesVar = "values";
        final String putPrimaryKeyVar = "putPrimaryKey";
        final String serializersVar = "s";
        final String outVar = "out";
        final String valueVar = "value";
        final String cvVar = "cv";

        final boolean hasSerializers = hasSerializers(data);

        builder.append("public ")
                .append(String.format(LIST_CLASS, CONTENT_VALUES_CLASS))
                .append(" toContentValuesList(")
                .append(String.format(COLLECTION_CLASS, type))
                .append(" ")
                .append(valuesVar)
                .append(", boolean ")
                .append(putPrimaryKeyVar)
                .append(") {\n")
                .append(indent.increment());

        if (hasSerializers) {
            builder.append("final ")
                    .append(SERIALIZERS_CLASS)
                    .append(" ")
                    .append(serializersVar)
                    .append(" = new ")
                    .append(SERIALIZERS_CLASS)
                    .append("();\n")
                    .append(indent);
        }

        builder.append("final ")
                .append(String.format(LIST_CLASS, CONTENT_VALUES_CLASS))
                .append(" ")
                .append(outVar)
                .append(" = new ")
                .append(String.format(ARRAY_LIST_CLASS, CONTENT_VALUES_CLASS))
                .append("();\n")
                .append(indent);

        builder.append(CONTENT_VALUES_CLASS)
                .append(" ")
                .append(cvVar)
                .append(";\n")
                .append(indent);

        builder.append("for (")
                .append(type)
                .append(" ")
                .append(valueVar)
                .append(": ")
                .append(valuesVar)
                .append(") {\n")
                .append(indent.increment())
                .append(cvVar)
                .append(" = ")
                .append(TO_CONTENT_VALUES_GENERIC_METHOD_NAME)
                .append("(");

        final String methodVars;
        if (hasSerializers) {
            methodVars = String.format("%s, %s, %s", serializersVar, valueVar, putPrimaryKeyVar);
        } else {
            methodVars = String.format("%s, %s", valueVar, putPrimaryKeyVar);
        }

        builder.append(methodVars)
                .append(");\n")
                .append(indent);

        builder.append("if (")
                .append(cvVar)
                .append(" != null) { ")
                .append(outVar)
                .append(".add(")
                .append(cvVar)
                .append("); }\n");

        builder.append(indent.decrement())
                .append("}\n")
                .append(indent)
                .append("return ")
                .append(outVar)
                .append(";\n")
                .append(indent.decrement())
                .append("}\n\n")
                .append(indent);

        return builder.toString();
    }

    private static String toContentValuesMethod(Indent indent, String type, StormParserAptData data) {

        final StringBuilder builder = new StringBuilder();

        final String valueVar = "value";
        final String putPrimaryKeyVar = "putPrimaryKey";
        final String serializersVar = "s";

        final boolean hasSerializers = hasSerializers(data);

        builder.append("public ")
                .append(CONTENT_VALUES_CLASS)
                .append(" toContentValues(")
                .append(type)
                .append(" ")
                .append(valueVar)
                .append(", boolean ")
                .append(putPrimaryKeyVar)
                .append(") {\n")
                .append(indent.increment());

        if (hasSerializers) {
            builder.append("final ")
                    .append(SERIALIZERS_CLASS)
                    .append(" ")
                    .append(serializersVar)
                    .append(" = new ")
                    .append(SERIALIZERS_CLASS)
                    .append("();\n")
                    .append(indent);
        }

        builder.append("return ")
                .append(TO_CONTENT_VALUES_GENERIC_METHOD_NAME)
                .append("(");

        final String methodVars;
        if (hasSerializers) {
            methodVars = String.format("%s, %s, %s", serializersVar, valueVar, putPrimaryKeyVar);
        } else {
            methodVars = String.format("%s, %s", valueVar, putPrimaryKeyVar);
        }

        builder.append(methodVars)
                .append(");\n")
                .append(indent.decrement())
                .append("}\n\n")
                .append(indent.decrement());

        return builder.toString();
    }
}
