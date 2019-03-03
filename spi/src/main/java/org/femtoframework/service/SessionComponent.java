package org.femtoframework.service;

import java.lang.annotation.*;

/**
 * 需要会话的组件<br>
 * 当组件或者方法添加了该Annotation，那么容器会在调用该组件之前进行Session确认，<br>
 * 看是否已经打开会话。如果不存在会话，那么会抛出{@link SessionTimeoutException}<br>
 * 组件上的Annotation会对所有的Action生效，而方法上的仅仅对该方法有效
 *
 * @author fengyun
 * @version 1.00 2005-4-23 11:01:16
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface SessionComponent
{
    Scope scope() default Scope.SERVER;
}
