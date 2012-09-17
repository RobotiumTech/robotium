package com.jayway.android.robotium.solo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UI test always not stable for varieties of resaons.In order to improve this,
 * we provide this annotation it can run test cases some times until it success.
 * Only when all run out of the number of retry times , we think that this test
 * fails.
 * 
 * Example of usage :
 * 
 * <pre>
 * public class LoginActivityTest extends RobotiumTestCase<LoginActivity> {
 * 
 * 	&#064;Failover(retryTimes = 3, screanshot = true)
 * 	public void testTextShows() throws Exception {
 * 		solo.clickOnText(&quot;Categories&quot;);
 * 		solo.clickOnText(&quot;Other&quot;);
 * 		solo.clickOnButton(&quot;Edit&quot;);
 * 		solo.searchText(&quot;Edit Window&quot;);
 * 		solo.clickOnButton(&quot;Commit&quot;);
 * 		assertTrue(solo.searchText(&quot;Changes have been made successfully&quot;));
 * 	}
 * 
 * }
 * 
 * </pre>
 * 
 * As shown above, if testTextShows() runs fail, it will run againt. Only if
 * this case run 3 times are fail,we consider it fail.As long as the whole
 * process run fail will screenshot.
 * 
 * @author zhangjunjun, chasezjj@gmail.com
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Failover {

	/**
	 * The max times test case will run until it success.
	 */
	int retryTimes() default 1;

	/**
	 * whether robotium take screanshot when test case fail.
	 */
	boolean screanshot() default false;
}
