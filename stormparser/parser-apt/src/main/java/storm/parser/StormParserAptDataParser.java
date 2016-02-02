package storm.parser;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import storm.annotations.Table;

/**
 * Created by Dimitry Ivanov on 17.01.2016.
 */
public class StormParserAptDataParser {

    private final StormParserTableParser mTableParser;
    private final StormParserHelperApt mHelper;

    public StormParserAptDataParser(Types types, Elements elements) {
        this.mTableParser = new StormParserTableParser();
        this.mHelper = new StormParserHelperApt(types, elements);
    }

    StormParserAptData parseData(TypeElement table) throws Throwable {

        final Table tableAnnotation = mHelper.getMainAnnotation(table, Table.class);

        if (tableAnnotation == null) {
            return null;
        }

        final boolean converter = tableAnnotation.converter();
        final boolean metadata  = tableAnnotation.metadata();
        final boolean scheme    = tableAnnotation.scheme();

        final StormParserTable<TypeElement, Element, TypeMirror> parsedTable = mTableParser.parseTable(mHelper, table);

        return new StormParserAptData(
                parsedTable,
                converter,
                metadata,
                scheme
        );
    }
}
