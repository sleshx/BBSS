package sg.gov.msf.bbss.apputils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bandaray on 11/12/2014.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface DisplayHeaderNameId {
    public int value();
}


