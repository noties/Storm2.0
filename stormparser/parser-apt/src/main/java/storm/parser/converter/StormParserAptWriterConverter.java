package storm.parser.converter;

import java.util.HashSet;
import java.util.List;
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

    private static final String LIST_CLASS = "java.util.List<%s>";
    private static final String ARRAY_LIST_CLASS = "java.util.ArrayList<%s>";

    private static final String PARSE_METHOD_NAME = "parse";
    private static final String TO_CONTENT_VALUES_GENERIC_METHOD_NAME = "toContentValuesInner";

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

        final Set<StormParserColumn<Element, TypeMirror>> columnsWithSerializers = new HashSet<>();

        for (StormParserColumn<Element, TypeMirror> column: data.getTable().getElements()) {
            if (column.getSerializerType() != null) {
                columnsWithSerializers.add(column);
            }
        }

        // fields
        for (StormParserColumn<Element, TypeMirror> column: columnsWithSerializers) {
            builder.append("\n")
                    .append(indent)
                    .append("final ")
                    .append(column.getSerializerType().toString())
                    .append(" ")
                    .append(serializerFieldName(column.getSerializerType()))
                    .append(";");
        }

        // constructor
        builder.append("\n")
                .append(indent)
                .append(SERIALIZERS_CLASS)
                .append("() {");

        indent.increment();

        for (StormParserColumn<Element, TypeMirror> column: columnsWithSerializers) {
            builder.append("\n")
                    .append(indent)
                    .append("this.")
                    .append(serializerFieldName(column.getSerializerType()))
                    .append(" = new ")
                    .append(column.getSerializerType().toString())
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

    private static String genericParseMethod(Indent indent, String type, StormParserAptData data) {

        final boolean hasSerializers = hasSerializers(data);

        final StringBuilder builder = new StringBuilder();

        final String varName = "value";
        final String cursorVarName = "c";
        final String cursorIndexesVarName = "ci";
        final String serializersVarName = "s";

        final String ifHasIndex = "if (" + cursorIndexesVarName + ".%s > -1)";
        final String cursorGetValue = cursorVarName + ".%s(" + cursorIndexesVarName + ".%s)";
        final String serializersGetValue = serializersVarName + ".%s.deserialize(%s)";

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
                // if we have serializer
                init = String.format(serializersGetValue, serializerFieldName(column.getSerializerType()), getFromCursor);
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
                .append(String.format(LIST_CLASS, type))
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
