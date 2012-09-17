package com.jayway.android.robotium.solo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * use this annotation to find view by id.
 * 
 * Example of usage :
 * 
 * <pre>
 * 
 * public class LoginActivityPage {
 * 
 * 	&#064;FindById(id = R.id.username)
 * 	private EditText usernameView;
 * 
 * 	&#064;FindById(id = R.id.password)
 * 	private EditText passwordView;
 * 
 * 	&#064;FindById(id = R.id.login)
 * 	private Button loginView;
 * 
 * }
 * 
 * </pre>
 * 
 * 
 * 
 * @author zhangjunjun, chasezjj@gmail.com
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FindById {

	int id() default 0;
}
