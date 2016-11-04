package com.github.xiaomi007.annotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * //TODO: Javadoc on com.github.xiaomi007.annotations.annotations.Table
 *
 * @author Damien
 * @version //TODO version
 * @since 2016-11-01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    String table();
}
