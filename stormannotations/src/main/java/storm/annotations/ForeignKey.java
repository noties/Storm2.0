package storm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dimitry Ivanov on 05.08.2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {

    enum ForeignKeyAction {

        NO_ACTION   ("NO ACTION"),
        RESTRICT    ("RESTRICT"),
        SET_NULL    ("SET NULL"),
        SET_DEFAULT ("SET DEFAULT"),
        CASCADE     ("CASCADE");

        private final String mSqlRepresentation;

        ForeignKeyAction(String sql) {
            this.mSqlRepresentation = sql;
        }

        public String getSqlRepresentation() {
            return mSqlRepresentation;
        }
    }

    String parentTable();
    String parentColumnName();

    ForeignKeyAction onDelete() default ForeignKeyAction.NO_ACTION;
    ForeignKeyAction onUpdate() default ForeignKeyAction.NO_ACTION;
}
