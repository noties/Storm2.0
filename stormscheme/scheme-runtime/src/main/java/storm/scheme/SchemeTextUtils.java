package storm.scheme;

/**
 * Created by Dimitry Ivanov on 02.12.2015.
 */
class SchemeTextUtils {

    private SchemeTextUtils() {}

    static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    static String ifNotEmpty(String str, String def) {
        if (isEmpty(str)) {
            return def;
        }
        return str;
    }
}
