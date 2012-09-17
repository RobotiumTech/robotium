package com.jayway.android.robotium.solo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import android.view.View;

import com.jayway.android.robotium.solo.annotation.FindById;

/**
 * Init field in activity page. Similar to page factory in Selenium2.
 * 
 * @author zhangjunjun, chasezjj@gmail.com
 *
 */
public class PageFactory {

	/**
	 * init view field in activity page class.
	 */
	public static <T> T initViews(Solo solo, Class<T> pageClassToInit) {
		T page = instantiatePage(solo, pageClassToInit);
		Class<?> clazz = page.getClass();
		while (clazz != Object.class) {
			initFields(solo, page, clazz);
			clazz = clazz.getSuperclass();
		}
		return page;
	}

	/**
	 * instantiate activity page class.
	 */
	private static <T> T instantiatePage(Solo solo, Class<T> pageClassToInit) {
		try {
			try {
				Constructor<T> constructor = pageClassToInit
						.getConstructor(Solo.class);
				return constructor.newInstance(solo);
			} catch (NoSuchMethodException e) {
				return pageClassToInit.newInstance();
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * find view by the id attribute of FindById annotation.
	 */
	private static void initFields(Solo solo, Object page, Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			FindById annotation = field.getAnnotation(FindById.class);
			if (annotation != null) {
				int id = annotation.id();

				if (View.class.isAssignableFrom(field.getType())) {
					Object value = solo.getView(id);
					if (value != null) {
						try {
							field.setAccessible(true);
							field.set(page, value);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
	}
}
