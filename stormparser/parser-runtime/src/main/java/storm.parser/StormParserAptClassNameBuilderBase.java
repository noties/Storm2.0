package storm.parser;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public abstract class StormParserAptClassNameBuilderBase implements StormParserAptClassNameBuilder {

    private static final String FULL_NAME = "%s.%s_%s"; // package(.)className(_)Suffix
    private static final String CL_NAME = "%s_%s"; // className(_)Suffix

    private final String mSuffix;

    public StormParserAptClassNameBuilderBase(String suffix) {
        this.mSuffix = suffix;
    }

    @Override
    public String fullName(String pkg, String cl) {
        return String.format(FULL_NAME, pkg, cl, mSuffix);
    }

    @Override
    public String className(String cl) {
        return String.format(CL_NAME, cl, mSuffix);
    }
}
