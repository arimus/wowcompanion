package net.sf.wowc;

import java.util.Map;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

/**
 * <p>Title: WoWConfig</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author David Castro
 * @version 1.0
 */

public class WoWConfig {
    private static String configFile = "/wowcompanion.properties";
    private static String preferencesFile = "wowcompanion.pref";
    
	private InputStream resourceStream = null;
	private Properties config = new Properties();
	private Properties preferences = new Properties();
	
    public WoWConfig() throws WoWConfigException {
		// get the config data from the properties file
		resourceStream = WoWConfig.class.getResourceAsStream(configFile);
		if (resourceStream == null) {
			throw new WoWConfigException("WoWConfig: could not find config file '"+configFile+"'");
		}

		config = new Properties();

		// load our config file
		try {
			config.load(resourceStream);
			
			Iterator i = config.keySet().iterator();
			while (i.hasNext()) {
				Object key = i.next();
				System.out.println(key+"="+config.get(key));
			}
		} catch (IOException e) {
			  throw new WoWConfigException("WoWConfig: could not load config file '"+configFile+"'");
		}

		// get current working directory
	    String cwd = System.getProperty("user.dir");
	    System.out.println("cwd = "+cwd);
		
		// get the user preferences
	    try {
	    	String prefsFile = cwd + File.separator + preferencesFile;
		    System.out.println("prefsFiles = "+prefsFile);
		    FileInputStream fis = new FileInputStream(prefsFile);
		    if (fis != null) {
		    	preferences.load(fis);
			
		    	Iterator i = preferences.keySet().iterator();
		    	while (i.hasNext()) {
		    		Object key = i.next();
		    		System.out.println(key+"="+preferences.get(key));
		    	}
		    } else {
		    	// no preferences file found, create a default
		    	preferences = new Properties();
		    	
		    	String tmp = cwd + File.separator + preferencesFile;
		    	try {
		    		
		    		// save out this new empty properties file
		    		FileOutputStream fos = new FileOutputStream(tmp);
		    	    preferences.store(fos, "");
		    	} catch (FileNotFoundException ee) {
					  throw new WoWConfigException("WoWConfig: could not load properties file '"+tmp+"'");
		    	} catch (IOException ee) {
					  throw new WoWConfigException("WoWConfig: error loading properties file '"+tmp+"'");
		    	}

		    }
	    } catch (IOException e) {
	    	System.err.println("error loading preferences: "+e);
	    }
	    
	    try {
	    	resourceStream.close();
	    } catch (IOException e) {
	    	// do nothing
	    	e.printStackTrace(System.err);
	    }
    }
	
	public Map getProperties() {
		return config;
	}
	
	public Map getPreferences() {
		return preferences;
	}
	
	public String getProperty(String key) 
		throws WoWConfigPropertyNotFoundException 
	{
		if (config.containsKey(key)) {
			return config.getProperty(key);
		}
		throw new WoWConfigPropertyNotFoundException();
	}
	
	public String getPreference(String key)
		throws WoWConfigPropertyNotFoundException
	{
		if (preferences.containsKey(key)) {
			return preferences.getProperty(key);
		}
		throw new WoWConfigPropertyNotFoundException();
	}
	
	public void setProperties(Map m) {
		Iterator i = m.keySet().iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			String value = (String)m.get(key);
			config.put(key, value);
		}
	}
	
	public void setPreferences(Map m) {
		Iterator i = m.keySet().iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			String value = (String)m.get(key);
			preferences.put(key, value);
		}
	}
	
	public void setProperty(String key, String value) {
		config.put(key, value);
	}
	
	public void setPreference(String key, String value) {
		preferences.put(key, value);
	}
	
	public void savePreferences() throws IOException {
	    // get current working directory
	    String cwd = System.getProperty("user.dir");
	    FileOutputStream fos = new FileOutputStream(cwd + File.separator + preferencesFile);
	    preferences.store(fos, "");
	}
}
