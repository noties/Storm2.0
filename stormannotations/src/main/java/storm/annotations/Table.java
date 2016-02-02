package storm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dimitry Ivanov on 05.08.2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    /**
     * @return the name of the table or empty string to use class simple name as a table name
     */
    String value() default "";

    /**
     * @return valid uri scheme, which will be triggered on table update. Return empty string to
     * create this value automatically.
     */
    String notificationUri() default "";

    /**
     * @return boolean to indicate that this table should be recreated on database version update
     */
    boolean recreateOnUpgrade() default false;

    /**
     * @return boolean to indicate whether or not annotation processor should generate StormConverter class
     */
    boolean converter() default true;
    boolean metadata()  default true;
    boolean scheme()    default true;
}
