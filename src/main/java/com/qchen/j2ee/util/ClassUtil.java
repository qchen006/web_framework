package com.qchen.j2ee.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class ClassUtil {
	
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(ClassUtil.class);
	
	public static Class loadClass(String name) {
		Class clz = null;
		
		try {
			clz = Class.forName(name, false, getClassLoader());
		} catch (ClassNotFoundException e) {
			logger.error("error when loading class");
			throw new RuntimeException("Error loading class "+ name);
		}
		return clz;
	}
	
	private static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * Load all the classes into the set in the classpath
	 * @param packageBaseName - the package name, the name may be particial 
	 * @return - the class set
	 */
	public static Set<Class> getClassSet(String packageBaseName){
		Set<Class> classSet = new HashSet<Class>();
		
		try {
			Enumeration<URL> urls =  getClassLoader().getResources(packageBaseName.replace(".", "/"));
			while (urls.hasMoreElements()) {
				URL url = (URL) urls.nextElement();
				if (url!=null) {
					String protocol = url.getProtocol();
					
					if (protocol.equals("file")) {
						String packagePath = url.getPath().replaceAll("%20", " ");
						addClass(classSet,packagePath,packageBaseName);
					}else if (protocol.equals("jar")) {
						JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
						if (jarURLConnection!=null) {
							JarFile jarFile =  jarURLConnection.getJarFile();
							if (jarFile!=null) {
								Enumeration<JarEntry> entries =  jarFile.entries();
								while (entries.hasMoreElements()) {
									String jarEntryName = entries
											.nextElement().getName();
									
									if (jarEntryName.endsWith(".class")) {
										String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
										doAddClass(classSet, className);
									}
								}
							}
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not load class");
		}
		
		return classSet;
	}

	/**
	 * Add the class name to the class set
	 * @param classSet - the class set
	 * @param className - the absolute path of the class name
	 */
	private static void doAddClass(Set<Class> classSet, String className) {
		classSet.add(loadClass(className));
	}

	/**
	 * Add all the classes in the package path which starts with the package baseName
	 * @param classSet - 
	 * @param packagePath - the package path, this should also be part of the package
	 * @param baseName - package baseName
	 */
	private static void addClass(Set<Class> classSet, String packagePath,
			String baseName) {
		File[] files = new File(packagePath).listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.isDirectory()||file.getName().endsWith(".class")&&file.isFile();
			}
		});
		
		for (File file : files) {
			String fileName = file.getName();
			if (file.isFile()) {
				String className = fileName.substring(0,fileName.lastIndexOf("."));
				if (StringUtils.isNotEmpty(baseName)) {
					className = baseName+"."+className;
				}
				doAddClass(classSet, className);
			}else {
				String subPackagePath = fileName;
				if (StringUtils.isNotEmpty(subPackagePath)) {
					subPackagePath = packagePath+"/"+subPackagePath;
				}
				
				String subPackageName = fileName;
				if (StringUtils.isNotEmpty(subPackageName)) {
					subPackageName = baseName + "."+subPackageName;
				}
				addClass(classSet, subPackagePath, subPackageName);
			}
		}
	}
}
