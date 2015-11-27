package storm.query;

/**
 * Created by Dimitry Ivanov on 10.10.2015.
 */
public class OrderBy {

    public static OrderBy from(String column, Sorting sorting) {
        return new OrderBy(column, sorting);
    }

    final String column;
    final Sorting sorting;

    public OrderBy(String column, Sorting sorting) {
        this.column = column;
        this.sorting = sorting;
    }
}
