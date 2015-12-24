package storm.scheme;

import java.util.ArrayList;
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

import storm.annotations.Column;
import storm.annotations.Default;
import storm.annotations.ForeignKey;
import storm.annotations.Index;
import storm.annotations.NewColumn;
import storm.annotations.NewTable;
import storm.annotations.PrimaryKey;
import storm.annotations.SQLiteNotNull;
import storm.annotations.Serialize;
import storm.annotations.Table;
import storm.annotations.Unique;
import storm.serializer.StormSerializer;
import storm.types.StormType;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
class StormSchemeAptParser {

    private static final Pattern SERIALIZER_TYPES = Pattern.compile(
            ".+<(.+),\\s*(.+)>"
    );

    final Types mTypes;
    final Elements mElements;

    StormSchemeAptParser(Types types, Elements elements) {
        mTypes = types;
        mElements = elements;
    }

    // return NULL if for this element scheme should not be generated
    StormSchemeTable table(TypeElement type) throws StormSchemeException {

        final Table table = type.getAnnotation(Table.class);
        // pseudo tables are used for parsing only, they have no db schema
        if (!table.generateScheme()) {
            return null;
        }

        final String tableName;
        if (SchemeTextUtils.isEmpty(table.value())) {
            tableName = type.getSimpleName().toString();
        } else {
            tableName = table.value();
        }

        final List<? extends Element> elements = type.getEnclosedElements();
        if (elements == null
                || elements.size() == 0) {
            throw StormSchemeException.newInstance("Element `%s` is annotated with @Table, but has no fields", type);
        }

        final List<StormSchemeColumn> columns = new ArrayList<>();

        StormSchemeColumn column;

        boolean hasPrimaryKey = false;

        for (Element element: elements) {
            column = parseColumn(element);
            if (column == null) {
                continue;
            }
            hasPrimaryKey |= column.isPrimaryKey();
            columns.add(column);
        }

        if (columns.size() == 0) {
            throw StormSchemeException.newInstance("Element `%s` has no suitable for scheme generation columns", type);
        }

        if (!hasPrimaryKey) {
            throw StormSchemeException.newInstance("Element `%s` has no primary key set (@PrimaryKey)", type);
        }

        final int tableVersionWhenAdded;
        {
            final NewTable newTable = type.getAnnotation(NewTable.class);
            if (newTable == null) {
                tableVersionWhenAdded = 0;
            } else {
                tableVersionWhenAdded = newTable.value();
            }
        }

        return new StormSchemeTable(tableName, columns)
                .setVersionWhenAdded(tableVersionWhenAdded);
    }

    private StormSchemeColumn parseColumn(Element element) throws StormSchemeException {

        if (element.getKind() != ElementKind.FIELD) {
            return null;
        }

        if (element.getModifiers().contains(Modifier.TRANSIENT)) {
            return null;
        }

        final String name;
        {
            final Column column = element.getAnnotation(Column.class);
            if (column == null) {
                throw StormSchemeException.newInstance(
                        "Field `%s` in class `%s` should be included " +
                        "in table scheme, but is not annotated with `@Column`. " +
                        "If field should not be included add `transient` modifier. " +
                        "Else annotate with `@Column` (even if field is already annotated with `@PrimaryKey`)",
                        element.getSimpleName(), element.getEnclosingElement()
                );
            }

            if (SchemeTextUtils.isEmpty(column.value())) {
                name = element.getSimpleName().toString();
            } else {
                name = column.value();
            }
        }

        final StormType type = parseType(element);

        final boolean isPrimaryKey;
        final boolean isAutoIncrement;
        {
            final PrimaryKey primaryKey = element.getAnnotation(PrimaryKey.class);
            isPrimaryKey = primaryKey != null;
            isAutoIncrement = isPrimaryKey && primaryKey.autoincrement();
        }

        final int versionWhenAdded;
        {
            final NewColumn newColumn = element.getAnnotation(NewColumn.class);
            versionWhenAdded = newColumn != null ? newColumn.value() : 0;
        }

        final boolean isUnique;
        {
            final Unique unique = element.getAnnotation(Unique.class);
            isUnique = unique != null;
        }

        final String defValue;
        {
            final Default def = element.getAnnotation(Default.class);
            if (def != null) {
                defValue = def.value();
            } else {
                defValue = null;
            }
        }

        final boolean isNotNull;
        {
            final SQLiteNotNull notNull = element.getAnnotation(SQLiteNotNull.class);
            isNotNull = notNull != null;
        }

        final StormSchemeIndex index = parseIndex(element);
        final StormSchemeForeignKey foreignKey = parseForeignKey(element);

        return new StormSchemeColumn()
                .setIsPrimaryKey(isPrimaryKey)
                .setIsAutoIncrement(isAutoIncrement)
                .setColumnName(name)
                .setType(type)
                .setVersionWhenAdded(versionWhenAdded)
                .setIsUnique(isUnique)
                .setDefaultValue(defValue)
                .setIsNonNull(isNotNull)
                .setIndex(index)
                .setForeignKey(foreignKey);
    }

    private StormType parseType(Element element) throws StormSchemeException {

        final StormType type;

        final Serialize serialize = element.getAnnotation(Serialize.class);
        if (serialize == null) {
            type = parseType(element.asType());
        } else {
            StormType serializerType = null;
            try {
                serialize.value();
            } catch (MirroredTypeException e) {
                serializerType = parseSerializerType(element, e.getTypeMirror());
            }
            type = serializerType;
        }

        if (type == null) {
            // check for boolean
            if (element.asType().getKind() == TypeKind.BOOLEAN
                    || element.asType().toString().equals("java.lang.Boolean")) {
                throw StormSchemeException.newInstance("Boolean types don't supported natively. " +
                        "One must register a serializer for them. Element `%s` in `%s`",
                        element, element.getEnclosingElement()
                );
            }

            throw StormSchemeException.newInstance("Cannot parse type for `%s` in `%s`", element, element.getEnclosingElement());
        }

        return type;
    }

    private StormType parseType(TypeMirror type) {
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

    private StormType parseSerializerType(Element element, TypeMirror type) throws StormSchemeException {

        final TypeMirror serializer = extractSerializerInterface(type);
        if (serializer == null) {
            throw StormSchemeException.newInstance("Could not parse serializer type for element `%s` & type: `%s`", element, type);
        }

        final Matcher matcher = SERIALIZER_TYPES.matcher(serializer.toString());
        if (!matcher.matches()) {
            throw StormSchemeException.newInstance("Could not parse generic types of `%s`", serializer);
        }

//        final String in     = matcher.group(1);
        final String out    = matcher.group(2);

        final TypeElement outType = mElements.getTypeElement(out);
        if (outType == null) {
            throw StormSchemeException.newInstance("Serializer `%s` has generic type as `OUT` value. Could not process.", type);
        }

        final TypeMirror outMirror = outType.asType();
        final StormType serializerType = parseType(outType);

        if (serializerType == null) {
            throw StormSchemeException.newInstance("Serializer `%s` has `OUT` type not supported by SQLite. Type: %s", type, outMirror);
        }

        return serializerType;
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

    private StormSchemeIndex parseIndex(Element element) {
        final Index index = element.getAnnotation(Index.class);
        if (index == null) {
            return null;
        }
        return new StormSchemeIndex(index.value(), index.sorting().name());
    }

    private StormSchemeForeignKey parseForeignKey(Element element) {
        final ForeignKey foreignKey = element.getAnnotation(ForeignKey.class);
        if (foreignKey == null) {
            return null;
        }

        final ForeignKey.ForeignKeyAction onUpdate = foreignKey.onUpdate();
        final ForeignKey.ForeignKeyAction onDelete = foreignKey.onDelete();

        final StormSchemeForeignKey key = new StormSchemeForeignKey(
                foreignKey.parentTable(),
                foreignKey.parentColumnName()
        );

        if (onUpdate != ForeignKey.ForeignKeyAction.NO_ACTION) {
            key.setOnUpdateAction(onUpdate.name());
        }

        if (onDelete != ForeignKey.ForeignKeyAction.NO_ACTION) {
            key.setOnDeleteAction(onDelete.name());
        }

        return key;
    }

    String fullName(TypeElement element) {
        return StormSchemeAptClassNameBuilder.fullName(
                packageName(element),
                element.getSimpleName().toString()
        );
    }

    String packageName(TypeElement element) {
        return mElements.getPackageOf(element).getQualifiedName().toString();
    }
}
