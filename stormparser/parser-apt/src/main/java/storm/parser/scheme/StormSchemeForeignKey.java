package storm.parser.scheme;

/**
 * Created by Dimitry Ivanov on 09.12.2015.
 */
public class StormSchemeForeignKey {

    private final String mParentTable;
    private final String mParentColumn;

    private String mOnUpdateAction;
    private String mOnDeleteAction;

    public StormSchemeForeignKey(String parentTable, String parentColumn) {
        mParentTable = parentTable;
        mParentColumn = parentColumn;
    }

    public String getParentTable() {
        return mParentTable;
    }

    public String getParentColumn() {
        return mParentColumn;
    }

    public String getOnUpdateAction() {
        return mOnUpdateAction;
    }

    public StormSchemeForeignKey setOnUpdateAction(String onUpdateAction) {
        mOnUpdateAction = onUpdateAction;
        return this;
    }

    public String getOnDeleteAction() {
        return mOnDeleteAction;
    }

    public StormSchemeForeignKey setOnDeleteAction(String onDeleteAction) {
        mOnDeleteAction = onDeleteAction;
        return this;
    }
}
