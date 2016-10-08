package com.qchen.j2ee.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import org.junit.Test;

import com.qchen.j2ee.util.ClassUtil;

import static org.junit.Assert.*;

public class TestClassUtil {
	
	@Test
	public void testLoadClass(){
		printClassPath();
		
		Class clz =  ClassUtil.loadClass("com.qchen.j2ee.util.TestClassUtil$TestingClass");
		assertNotNull(clz);
	}
	
	private void printClassPath() {
		ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
        	System.out.println(url.getFile());
        }

	}

	@Test
	public void testClassSet(){
		Set<Class> clsses =  ClassUtil.getClassSet("com.qchen.j2ee");
		assertNotNull(clsses);
	}
	
	public static class TestingClass{
		
	}
}


