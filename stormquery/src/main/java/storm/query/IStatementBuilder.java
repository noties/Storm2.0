package storm.query;

/**
 * Created by Dimitry Ivanov on 10.10.2015.
 */
public interface IStatementBuilder {
    String getStatement();
    String[] getArguments();
}
