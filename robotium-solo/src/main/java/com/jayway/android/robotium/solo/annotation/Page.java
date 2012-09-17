package com.jayway.android.robotium.solo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * marker field is activity page. If you test case extends RobotiumTestCase,it
 * will init this field first. see activity page {@link FindById}.
 * 
 * Example of usage :
 * 
 * <pre>
 * 
 * public class LoginActivityTest extends RobotiumTestCase&lt;LoginActivity&gt; {
 * 
 * 	&#064;Page
 * 	private LoginActivityPage loginActivityPage;
 * 
 * }
 * 
 * </pre>
 * 
 * @author zhangjunjun, chasezjj@gmail.com
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Page {

}
