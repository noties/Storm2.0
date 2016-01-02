package storm.parser;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public interface StormParserAptClassNameBuilder {

    String fullName(String pkg, String cl);
    String className(String cl);

}
