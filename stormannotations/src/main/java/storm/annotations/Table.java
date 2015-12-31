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
    String value() default "";
    String notificationUri() default "";
    boolean generateScheme() default true;
    boolean generateParser() default true;
    boolean recreateOnUpgrade() default false;
}
