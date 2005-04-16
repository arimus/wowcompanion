package net.sf.wowc;

import java.util.Map;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * <p>Title: WoWConfig</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author David Castro
 * @version 1.0
 */

public class WoWConfig {
	private static Logger log = LogManager.getLogger(WoWConfig.class); 
	private static Logger fileLogger = LogManager.getLogger("net.sf.wowc");

	private static String homeDirectory = System.getProperty("user.home");
	
	private static String configFile = "/wowcompanion.properties";
    private static String preferencesFile = "wowcompanion.pref";
    private static String logFile = "wowcompanion.log";
    private static String baseDirPath = "";
    
	private InputStream resourceStream = null;
	private Properties config = new Properties();
	private Properties preferences = new Properties();
	
    public WoWConfig() throws WoWConfigException {
    	configureLog();
    	
		// get the config data from the properties file
		resourceStream = WoWConfig.class.getResourceAsStream(configFile);
		if (resourceStream == null) {
			log.error("WoWConfig: could not find config file '"+configFile+"'");
			throw new WoWConfigException("WoWConfig: could not find config file '"+configFile+"'");
		}

		config = new Properties();

		// load our config file
		try {
			config.load(resourceStream);
			
			Iterator i = config.keySet().iterator();
			while (i.hasNext()) {
				Object key = i.next();
				//log.debug("WoWConfig: found in config: "+key+"="+config.get(key));
			}
		} catch (IOException e) {
			  log.error("WoWConfig: could not load config file '"+configFile+"'", e);
			  throw new WoWConfigException("WoWConfig: could not load config file '"+configFile+"'");
		}

		// get current working directory
	    //String cwd = System.getProperty("user.dir");
	    //log.debug("WoWConfig: cwd = "+cwd);
		
		// get the user preferences
	    try {
	    	String prefsFile = homeDirectory + File.separator + preferencesFile;
		    log.debug("WoWConfig: prefsFiles = "+prefsFile);
		    FileInputStream fis = new FileInputStream(prefsFile);
		    if (fis != null) {
		    	preferences.load(fis);
			
		    	Iterator i = preferences.keySet().iterator();
		    	while (i.hasNext()) {
		    		Object key = i.next();
		    		//log.debug("WoWConfig: found in prefs: "+key+"="+preferences.get(key));
		    	}
		    } else {
		    	// no preferences file found, create a default
		    	preferences = new Properties();
		    	
		    	String tmp = homeDirectory + File.separator + preferencesFile;
		    	try {
		    		
		    		// save out this new empty properties file
		    		FileOutputStream fos = new FileOutputStream(tmp);
		    	    preferences.store(fos, "");
		    	} catch (FileNotFoundException ee) {
					log.debug("WoWConfig: could not load preferences file '"+tmp+"'", ee);
					throw new WoWConfigException("WoWConfig: could not load properties file '"+tmp+"'");
		    	} catch (IOException ee) {
					log.debug("WoWConfig: error loading preferences file '"+tmp+"'", ee);
					throw new WoWConfigException("WoWConfig: error loading properties file '"+tmp+"'");
		    	}

		    }
	    } catch (IOException e) {
	    	log.debug("WoWConfig: error loading preferences", e);
	    }
	    
	    try {
	    	resourceStream.close();
	    } catch (IOException e) {
	    	// do nothing
	    	log.error("WoWConfig: error closing resources", e);
	    }
    }
	
    private void configureLog() {
		// get the file appender
		Logger rootLogger = Logger.getRootLogger();
		
		//Enumeration appenders = fileLogger.getAllAppenders();
		//FileAppender appender = null;
		
		//rootLogger.removeAllAppenders();
		//rootLogger.error("WoWCompanion: searching for file appender");
	
		// log4j.appender.file=org.apache.log4j.FileAppender
		// log4j.appender.file.layout=org.apache.log4j.PatternLayout
		// log4j.appender.file.layout.ConversionPattern=%d [%p] %c - %m%n
		// log4j.appender.file.File=wowcompanion.log

		try {
			PatternLayout pl = new PatternLayout("%d [%p] %c - %m%n");
			String logName = homeDirectory + File.separator + logFile;
			FileAppender fa = new FileAppender(pl, logName);
			rootLogger.removeAllAppenders();
			rootLogger.addAppender(fa);
			rootLogger.setLevel(Level.INFO);
		} catch (IOException e) {
			System.err.println("WoWCompanion: failed to create log");
			e.printStackTrace();
		}
		

//		// setup the logger before continuing
//		while (appenders.hasMoreElements()) {
//			FileAppender a = (FileAppender)appenders.nextElement();
//			if (a instanceof FileAppender) {
//				appender = a;
//				//rootLogger.error("WoWCompanion: found appender");
//			} else {
//				//rootLogger.error("WoWCompanion: not correct appender, '"+a.getName()+"'");
//			}
//		}
//		
//		// if we have a file appender
//		if (appender != null) {
//			String logName = appender.getFile();
//			
//			// we want the log to be saved into the WoW directory
//			File f = new File(baseDirPath);
//			if (f.exists() && f.isDirectory()) {
//				// adjust the appender
//				logName = homeDirectory + File.separator + logName;
//				//rootLogger.error("WoWCompanion: setting file for appender to '"+logName+"'");
//				appender.setFile(logName);
//				appender.activateOptions();
//				log.removeAllAppenders();
//				log.addAppender(appender);
//			}
//		}
//		
//		// we want the log to be saved into the WoW directory
//		File f = new File(baseDirPath + File.separator + "wowcompanion.log");
//		if (f.exists() && f.isDirectory()) {
//			// adjust the appender
//			String l = baseDirPath + File.separator + "wowcompanion.log";
//			//rootLogger.debug("WoWCompanion: saving log '"+baseDirPath + File.separator + "wowcompanion.log"+"'");
//			appender.setFile(l);
//		}
    }
    
    public Logger getLogger() {
    	return log;
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
		log.debug("WoWConfig: could not find property '"+key+"'");
		throw new WoWConfigPropertyNotFoundException();
	}
	
	public String getPreference(String key)
		throws WoWConfigPropertyNotFoundException
	{
		if (preferences.containsKey(key)) {
			return preferences.getProperty(key);
		}
		log.debug("WoWConfig: could not find preference '"+key+"'");
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
	    log.debug("WoWConfig: setting preference '"+key+"'='"+value+"'");

	    preferences.put(key, value);
	}
	
	public void savePreferences() throws IOException {
	    log.debug("WoWConfig: saving preferences");

	    // get user home directory
	    String cwd = System.getProperty("user.home");
	    log.debug("WoWConfig: saving preferences to '"+cwd + File.separator + preferencesFile+"'");
	    FileOutputStream fos = new FileOutputStream(cwd + File.separator + preferencesFile);
	    preferences.store(fos, "");
	}
}
