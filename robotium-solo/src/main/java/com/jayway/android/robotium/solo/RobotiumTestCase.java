package com.jayway.android.robotium.solo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.PageFactory;
import com.jayway.android.robotium.solo.Solo;
import com.jayway.android.robotium.solo.annotation.Failover;
import com.jayway.android.robotium.solo.annotation.Page;

/**
 * 
 * RobotiumTestCase provides fail retry, fail screenshot ,activity
 * initialization and so on.
 * 
 * @author zhangjunjun, chasezjj@gmail.com
 * 
 */
public class RobotiumTestCase<T extends Activity> extends
		ActivityInstrumentationTestCase2<T> {

	protected Solo solo;

	public RobotiumTestCase(String pkg, Class<T> activityClass) {
		super(pkg, activityClass);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// fix the bug in android ActivityInstrumentationTestCase2
		setActivityInitialTouchMode(false);
		setActivityIntent(null);

		solo = new Solo(getInstrumentation(), getActivity());
		initRobotiumTestCase(solo);
	}

	@Override
	protected void runTest() throws Throwable {
		int retryTimes = 1;
		boolean screenshot = false;
		Method method = getClass().getMethod(getName(), (Class[]) null);
		Failover failover = method.getAnnotation(Failover.class);
		if (failover != null) {
			if (failover.retryTimes() > 1) {
				retryTimes = failover.retryTimes();
			}
			screenshot = failover.screanshot();
		}

		while (retryTimes > 0) {
			try {
				super.runTest();
				break;
			} catch (Throwable e) {
				if (retryTimes > 1) {
					retryTimes--;
					continue;
				} else {
					if (screenshot) {
						solo.takeScreenshot();
					}
					throw e;
				}
			}
		}
	}

	protected void initRobotiumTestCase(Solo solo)
			throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Page.class)) {
				Object page = PageFactory.initViews(solo, field.getType());
				field.setAccessible(true);
				field.set(this, page);
			}
		}
	}

}
