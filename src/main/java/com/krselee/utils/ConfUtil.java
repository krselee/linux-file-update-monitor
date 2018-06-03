package com.krselee.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * conf manager
 */
public class ConfUtil {

	private Properties prop = new Properties();

	private void loadconf() throws FileNotFoundException, IOException {
		String path = "conf.properties";
		prop.load(ConfUtil.class.getClassLoader().getResourceAsStream(path));
	}

	public ConfUtil() {
		try {
			loadconf();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean chkProperty(String _key) {
		return prop.containsKey(_key);
	}

	public String getProperty(String _key) {
		return prop.getProperty(_key);
	}
}
