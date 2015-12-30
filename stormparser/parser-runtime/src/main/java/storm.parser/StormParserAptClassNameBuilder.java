package storm.parser;

/**
 * Created by Dimitry Ivanov on 27.12.2015.
 */
class StormParserAptClassNameBuilder {

    private StormParserAptClassNameBuilder() {}

    private static final String PARSER_CLASS_NAME   = "%s_StormParser";
    private static final String PARSER_FULL_NAME    = "%s." + PARSER_CLASS_NAME;

    private static final String METADATA_CLASS_NAME = "%s_Metadata";
    private static final String METADATA_FULL_NAME  = "%s." + METADATA_CLASS_NAME;

    static String parserFullName(String packageName, String className) {
        return String.format(
                PARSER_FULL_NAME,
                packageName,
                className
        );
    }

    static String parserClassName(String className) {
        return String.format(PARSER_CLASS_NAME, className);
    }

    static String metadataFullName(String packageName, String className) {
        return String.format(
                METADATA_FULL_NAME,
                packageName,
                className
        );
    }

    static String metadataClassName(String className) {
        return String.format(METADATA_CLASS_NAME, className);
    }
}
