package storm.scheme;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
class StormSchemeAptWriter {

    private static final String ON_UPGRADE_OLD = "oldVersion";
    private static final String ON_UPGRADE_NEW = "newVersion";
    private static final String IS_UPGRADE_VERSION_PATTERN = "%1$d > %2$s && %1$d <= %3$s";

    final Elements mElements;
    final Filer mFiler;
    final Logger mLogger;

    StormSchemeAptWriter(Elements elements, Filer filer, Logger logger) {
        mElements = elements;
        mFiler = filer;
        mLogger = logger;
    }

    void write(String packageName, String className, StormSchemeTable table) throws StormSchemeException {

        final Indent indent = new Indent(4);
        final StringBuilder builder = new StringBuilder();

        // package
        builder.append("package ").append(packageName).append(";\n\n");

        // imports

        // class definition
        builder.append("public class ")
                .append(className)
                .append(" implements ")
                .append(StormScheme.class.getName())
                .append(" {\n\n");

        // public empty constuctor
        builder.append(indent.increment())
                .append("public ")
                .append(className)
                .append("() {}\n\n");

        // onCreate
        builder.append(indent)
                .append("@Override public java.util.List<String> onCreate() {\n")
                .append(generateOnCreateStatementsSourceBlock(indent, table))
                .append("}\n\n");

        // onUpgrade
        builder.append(indent)
                .append("@Override public java.util.List<String> onUpgrade(int ")
                .append(ON_UPGRADE_OLD)
                .append(", int ")
                .append(ON_UPGRADE_NEW)
                .append(") {\n")
                .append(generateOnUpgradeStatementsSourceBlock(indent, table))
                .append("}\n\n");


        // end class definition
        builder.append("}");

        Writer writer = null;
        final String javaFile = packageName + "." + className;
        try {
            final JavaFileObject javaFileObject = mFiler.createSourceFile(javaFile);
            writer = javaFileObject.openWriter();
            writer.write(builder.toString());
        } catch (IOException e) {
            throw StormSchemeException.newInstance("Exception creating a source java file `%s`, e: %s", javaFile, e);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {/*ignored*/}
            }
        }
    }

    private static String generateOnCreateStatementsSourceBlock(Indent indent, StormSchemeTable table) throws StormSchemeException {
        final StormSchemeStatementsGenerator generator = new StormSchemeStatementsGenerator(table);
        final List<String> statements = generator.onCreate();

        final StringBuilder builder = new StringBuilder()
                .append(indent.increment())
                .append("return java.util.Arrays.asList(\n")
                    .append(indent.increment());

        boolean isFirst = true;
        for (String statement: statements) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(",\n").append(indent);
            }
            builder.append('\"')
                    .append(statement)
                    .append('\"');
        }

        builder.append("\n").append(indent.decrement()).append(");\n").append(indent.decrement());

        return builder.toString();
    }

    private static String generateOnUpgradeStatementsSourceBlock(Indent indent, StormSchemeTable table) {

        final StormSchemeStatementsGenerator generator = new StormSchemeStatementsGenerator(table);

        final int tableVersion = table.getVersionWhenAdded();

        final StringBuilder builder = new StringBuilder()
                .append(indent.increment())
                .append("final java.util.List<String> list = new java.util.ArrayList<String>();\n")
                .append(indent)
                .append("final java.util.List<String> indexes = new java.util.ArrayList<String>();\n")
                .append(indent)
                .append("if (")
                .append(isUpgradeBoolString(tableVersion))
                .append(") {\n")
                .append(indent.increment());

        builder.append("final java.util.List<String> columns = new java.util.ArrayList<String>();\n");

        int columnVersion;
        for (StormSchemeColumn column: table.getColumns()) {
            columnVersion = column.getVersionWhenAdded();
            if (columnVersion == 0) {
                builder.append(indent)
                        .append("columns.add(\"")
                        .append(StormSchemeStatementsGenerator.getColumnCreateStatement(column))
                        .append("\");\n");
                if (column.getIndex() != null) {
                    builder.append(indent)
                            .append("indexes.add(\"")
                            .append(StormSchemeStatementsGenerator.getColumnIndexStatement(table.getTableName(), column.getColumnName(), column.getIndex()))
                            .append("\");\n");
                }
            } else {
                builder.append(indent)
                        .append("if (")
                        .append(isUpgradeBoolString(columnVersion))
                        .append(") {\n")
                        .append(indent.increment())
                        .append("columns.add(\"")
                        .append(StormSchemeStatementsGenerator.getColumnCreateStatement(column))
                        .append("\");\n");
                if (column.getIndex() != null) {
                    builder.append(indent)
                            .append("indexes.add(\"")
                            .append(StormSchemeStatementsGenerator.getColumnIndexStatement(table.getTableName(), column.getColumnName(), column.getIndex()))
                            .append("\");\n");
                }
                builder.append(indent.decrement())
                        .append("}\n");
            }
        }

        // builder statement
        builder.append(indent)
                .append("if (columns.size() > 0) {\n")
                .append(indent.increment())
                .append("final java.lang.StringBuilder builder = new java.lang.StringBuilder();\n")
                .append(indent)
                .append("builder.append(\"")
                .append("CREATE TABLE ")
                .append(table.getTableName())
                .append("(\");\n")
                .append(indent)
                .append("boolean isFirst = true;\n")
                .append(indent)
                .append("for (String column: columns) {\n")
                .append(indent.increment())
                .append("if (isFirst) isFirst = false;\n")
                .append(indent)
                .append("else builder.append(\", \");\n")
                .append(indent)
                .append("builder.append(column);\n")
                .append(indent.decrement())
                .append("}\n")
                .append(indent)
                .append("builder.append(\");\");\n")
                .append(indent)
                .append("list.add(builder.toString());\n")
                .append(indent.decrement())
                .append("}\n");

        // else statement when it's not table upgrade (ALTER TABLE)
        builder.append(indent.decrement())
                .append("} else {\n");

        indent.increment();

        for (StormSchemeColumn column: table.getColumns()) {
            if (column.getVersionWhenAdded() > 0) {
                builder.append(indent)
                        .append("if (")
                        .append(isUpgradeBoolString(column.getVersionWhenAdded()))
                        .append(") {\n")
                        .append(indent.increment())
                        .append("list.add(\"")
                        .append(StormSchemeStatementsGenerator.getAlterTableAddColumnStatement(table.getTableName(), column))
                        .append("\");\n");
                if (column.getIndex() != null) {
                    builder.append(indent)
                            .append("indexes.add(\"")
                            .append(StormSchemeStatementsGenerator.getColumnIndexStatement(table.getTableName(), column.getColumnName(), column.getIndex()))
                            .append("\");\n");
                }
                builder.append(indent.decrement())
                        .append("}\n");
            }
        }

        builder.append("\n")
                .append(indent.decrement())
                .append("}\n")
                .append(indent)
                .append("list.addAll(indexes);\n")
                .append(indent)
                .append("return list;\n")
                .append(indent.decrement());

        return builder.toString();
    }

    private static String isUpgradeBoolString(int version) {
        return String.format(IS_UPGRADE_VERSION_PATTERN, version, ON_UPGRADE_OLD, ON_UPGRADE_NEW);
    }
}
