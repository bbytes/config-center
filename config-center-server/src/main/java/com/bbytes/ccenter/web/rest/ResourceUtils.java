package com.bbytes.ccenter.web.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.CCProperty;

public class ResourceUtils {

	
	public static boolean  projectExists(List<CCProject> ccprojects, String projectName) {
		if (ccprojects != null) {		
	    	for (CCProject proj : ccprojects) {
	    		if (proj.getProjectName().equalsIgnoreCase(projectName)) {
	    			return true; 
	    		}
	    	}
		}
		
		return false;
	}
	
	public static boolean  propertyExists(List<CCProperty> ccproperties, String propertyName) {
		if (ccproperties != null) {
	    	for (CCProperty prop : ccproperties) {
	    		if (prop.getPropertyName().equalsIgnoreCase(propertyName)) {
	    			return true; 
	    		}
	    	}
		}
		
		return false;
	}
	
	public static TreeMap<String, String> getProperties(String infile)
			throws IOException {
		final int lhs = 0;
		final int rhs = 1;

		TreeMap<String, String> map = new TreeMap<String, String>();
		BufferedReader bfr = new BufferedReader(
				new FileReader(new File(infile)));

		String line;
		while ((line = bfr.readLine()) != null) {
			if (!line.startsWith("#") && !line.isEmpty()) {
				String[] pair = line.trim().split("=");
				map.put(pair[lhs].trim(), pair[rhs].trim());
			}
		}
		bfr.close();

		return map;
	}
	
	public static TreeMap<String, String> getPropertiesAsMap(
			List<CCProperty> properties) {

		TreeMap<String, String> map = new TreeMap<String, String>();
		if (properties != null && properties.size() > 0) {
			for (CCProperty ccProperty : properties) {
				map.put(ccProperty.getPropertyName(), ccProperty
						.getPropertyValue().toString());
			}
		}
		return map;
	}

	public static void setProperties(List<CCProperty> properties,
			OutputStream os) throws IOException {
		TreeMap<String, String> map = getPropertiesAsMap(properties);
		final String seperator = "=";
		String charset = "UTF-8";
		String newLine = "\n";

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			os.write(key.getBytes(charset));
			os.write(seperator.getBytes(charset));
			os.write(value.getBytes(charset));
			os.write(newLine.getBytes(charset));
		}
	}
}
