package storm.parser.metadata;

/**
 * Created by Dimitry Ivanov on 21.12.2015.
 */
public class StormNotificationUriBuilder {

    private StormNotificationUriBuilder() {}

    private static final String PATTERN = "%1$s://storm/%2$s";

    public static String getDefault(String packageName, String className, String value) {

        final String out;
        if (value != null && value.length() > 0) {
            out = value;
        } else {
            // package (remove all unsupported symbols)
            // ://storm
            // class simple name to lower case
            final String scheme = removeUnsupportedSymbols(packageName);
            final String path = removeUnsupportedSymbols(className);

            if (scheme == null
                    || path == null) {
                throw new RuntimeException("Cannot create an uri for a class: `" + className + "`");
            }

            out = String.format(PATTERN, scheme, path);
        }

        return out;
    }

    static String removeUnsupportedSymbols(String str) {
        // just letters (lowercase) & digits
        if (str == null
                || str.length() == 0) {
            return null;
        }

        final String filtered = str.replaceAll("([^0-9a-zA-Z]+)", "");
        return filtered.toLowerCase();
    }
}
