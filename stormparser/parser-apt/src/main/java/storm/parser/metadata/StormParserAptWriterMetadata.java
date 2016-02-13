package storm.parser.metadata;

import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import storm.parser.Indent;
import storm.parser.StormParserAptData;
import storm.parser.StormParserAptWriterBase;
import storm.parser.StormParserColumn;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptWriterMetadata extends StormParserAptWriterBase {

    private static final String STORM_METADATA_CLASS = StormMetadata.class.getName();
    private static final String CONST_COL_PATTERN = "public static final String COL_%1$s = \"%2$s\";";

    public StormParserAptWriterMetadata(Elements elements, Filer filer) {
        super(elements, filer, StormMetadataAptClassNameBuilder.getInstance());
    }

    @Override
    protected boolean shouldCreateSourceCodeFile(StormParserAptData data) {
        return data.metadata();
    }

    @Override
    protected String getSourceCode(String packageName, String className, String type, StormParserAptData data) throws Throwable {

        final Indent indent = indent();
        final StringBuilder builder = new StringBuilder();

        final StormParserColumn<Element, TypeMirror> primaryKey = findPrimaryKeyColumn(data.getTable().getElements());

        // package name
        builder.append("package ")
                .append(packageName)
                .append(";\n");

        builder.append("import ")
                .append(PrimaryKeySelection.class.getName())
                .append(";\n\n");

        builder.append("public class ")
                .append(className)
                .append(" implements ")
                .append(STORM_METADATA_CLASS)
                .append("<")
                .append(type)
                .append(">")
                .append("{\n")
                .append(indent.increment());

        for (StormParserColumn column: data.getTable().getElements()) {
            builder.append("\n")
                    .append(indent)
                    .append(String.format(CONST_COL_PATTERN, createConstName(column.getName()), column.getName()));
        }

        final String tableName = "TABLE_NAME";
        builder.append("\n")
                .append(indent)
                .append(String.format("public static final String %s = \"%s\";", tableName, data.getTable().getTableName()));

        // `tableName` method
        builder.append("\n")
                .append(indent)
                .append("public String tableName() { return ")
                .append(tableName)
                .append("; }\n\n")
                .append(indent);

        // `notificationUri` method
        final String customNotification = data.getTable().getCustomNotificationUri();
        final String notificationUri = StormNotificationUriBuilder.getDefault(packageName, type, customNotification);
        builder.append("public static final String NOTIFICATION_URI_STRING = \"")
                .append(notificationUri)
                .append("\";\n")
                .append(indent);

        builder.append("public static final android.net.Uri NOTIFICATION_URI = android.net.Uri.parse(")
                .append("NOTIFICATION_URI_STRING")
                .append(");\n")
                .append(indent);
        builder.append("public android.net.Uri notificationUri() { return NOTIFICATION_URI; }\n\n")
                .append(indent);

        // `isPrimaryKeyAutoincrement` method
        builder.append("public boolean isPrimaryKeyAutoincrement() { return ")
                .append(primaryKey.isAutoIncrement())
                .append("; }\n\n")
                .append(indent);

        builder.append("public PrimaryKeySelection primaryKeySelection(")
                .append(type)
                .append(" value) {\n")
                .append(indent.increment())
                .append("return new PrimaryKeySelection(\"")
                .append(primaryKey.getName())
                .append("\", value.")
                .append(primaryKey.getElement().getSimpleName().toString())
                .append(");\n")
                .append(indent.decrement())
                .append("}\n")
                .append(indent);

        builder.append("\n}")
                .append(indent.decrement());

        return builder.toString();
    }

    static StormParserColumn<Element, TypeMirror> findPrimaryKeyColumn(List<StormParserColumn<Element, TypeMirror>> columns) {
        for (StormParserColumn<Element, TypeMirror> column: columns) {
            if (column.isPrimaryKey()) {
                return column;
            }
        }
        throw new IllegalStateException("Unexpected error. Cannot find a primary key for a table");
    }

    static String createConstName(String columnName) {
        final int length = columnName.length();
        final StringBuilder builder = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = columnName.charAt(i);
            if (i == 0) {
                builder.append(Character.toUpperCase(c));
                continue;
            }
            // if it's upper case insert a underscore
            if (Character.isUpperCase(c)) {
                builder.append("_")
                        .append(c);
                continue;
            }

            builder.append(Character.toUpperCase(c));
        }
        return builder.toString();
    }
}
