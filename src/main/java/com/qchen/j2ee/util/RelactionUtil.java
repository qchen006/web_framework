package com.qchen.j2ee.util;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelactionUtil {
	private static final Logger logger = LoggerFactory.getLogger(RelactionUtil.class);

	public static Object newInstance(Class clz){
		Object result = null;
		try {
			result = clz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("error when inistating object for class {}", clz);
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static void setField(Object obj, Field field, Object value){
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			logger.error("error when set the object filed {}", field.getName());
			throw new RuntimeException(e);
		}
	}
}
