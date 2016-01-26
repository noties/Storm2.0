package storm.parser.columns;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

import storm.parser.Indent;
import storm.parser.StormParserAptClassNameBuilder;
import storm.parser.StormParserAptData;
import storm.parser.StormParserAptWriterBase;
import storm.parser.StormParserColumn;

/**
 * Created by Dimitry Ivanov on 24.01.2016.
 */
public class StormParserAptWriterColumns extends StormParserAptWriterBase {

    private static final String CONST_PATTERN = "public static final String COL_%1$s = \"%2$s\";";

    public StormParserAptWriterColumns(Elements elements, Filer filer) {
        super(elements, filer, StormColumnsAptNameBuilder.getInstance());
    }

    @Override
    protected String getSourceCode(String packageName, String className, StormParserAptData data) throws Throwable {

        final Indent indent = new Indent(4);
        final StringBuilder builder = new StringBuilder();

        // package information
        builder.append("package ")
                .append(packageName)
                .append(";\n\n");

        // class information
        builder.append("public class ")
                .append(className)
                .append(" {\n");

        // private constructor
        builder.append(indent.increment())
                .append("private ")
                .append(className)
                .append("() {}\n");

        for (StormParserColumn column: data.getTable().getElements()) {
            builder.append("\n")
                    .append(indent)
                    .append(String.format(CONST_PATTERN, createConstName(column.getName()), column.getName()));
        }

        builder.append("\n")
                .append(indent.decrement())
                .append("}\n");

        return builder.toString();
    }

    private static String createConstName(String columnName) {
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
