package storm.scheme;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormSchemeAptClassNameBuilder {

    private StormSchemeAptClassNameBuilder() {}

    private static final String CLASS_NAME_PATTERN = "%s_StormScheme";
    private static final String FULL_NAME_PATTERN = "%s." + CLASS_NAME_PATTERN;

    public static String fullName(String packageName, String className) {
        return String.format(
                FULL_NAME_PATTERN,
                packageName,
                className
        );
    }

    public static String className(String className) {
        return String.format(CLASS_NAME_PATTERN, className);
    }
}
