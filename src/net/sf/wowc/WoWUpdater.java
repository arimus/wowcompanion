/*
 * Created on Apr 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.wowc;

import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpRecoverableException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

import java.io.File;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.security.*;


/**
 * @author arimus
 *
 * This class is used to update components required for WoWCompanion to work,
 * such as WoW AddOns, etc.
 */
public class WoWUpdater {
	private static Logger log = null;
	private String loglevel = "INFO";
	private WoWConfig config = null;
	private Map preferences = null;
	private Map properties = null;
	private String updateURL = "";
	private String catalogFile = "";
	private Properties catalogProperties = null;
	private HashMap components = new HashMap();
	private String baseDirPath = "";
	private String interfacePath = "Interface";
	private String addonPath = interfacePath + File.separator + "AddOns";
	private String framexmlPath = interfacePath + File.separator + "FrameXML";
    private ArrayList failed = new ArrayList();

	private static int defaultTimeout = 10000;
	private static int timeout = defaultTimeout;

	{
		try {
			WoWConfig config = new WoWConfig();
			Map m = config.getPreferences();
			
			log = config.getLogger();

			if (m.containsKey("loglevel")) {
				// set the level to debug if needed
				loglevel = config.getPreference("loglevel");
				if (loglevel.equals("DEBUG")) {
					log.setLevel(Level.DEBUG);
				}
			}
		} catch (WoWConfigException e) {
			log.error("WoWCompanion: failed to load configuration", e);
		} catch (WoWConfigPropertyNotFoundException e) {
			// FIXME - show an error dialog here, then exit
			log.error("WoWCompanion: failed to load preferences", e);
		}

	}

	public Collection getFailedComponents() {
		return failed;
	}
	
	private void initConfig(){
		try {
			log.debug("WoWUpdater: initializing config");
			config = new WoWConfig();
			preferences = config.getPreferences();
			
			try {
				updateURL = config.getPreference("update.url");
			} catch (WoWConfigPropertyNotFoundException e) {
				// nothing
				log.debug("WoWUpdater: no update.url found, using default");
			}
			
			if (updateURL.equals("")) {
				updateURL = config.getProperty("update.url.default");
			}
			log.debug("WoWUpdater: using update URL: '"+updateURL+"'");

			try {
				catalogFile = config.getProperty("update.catalog.file");
			} catch (WoWConfigPropertyNotFoundException e) {
				// nothing
				log.debug("WoWUpdater: no update.catalog.file found");
			}
			
			log.debug("WoWUpdater: using update catalog file: '"+catalogFile+"'");

			if (preferences.containsKey("basedir")) {
				baseDirPath = config.getPreference("basedir");
				log.debug("WoWCompanion: setting basedir to '"+baseDirPath+"'");
			} else {
				baseDirPath = config.getProperty("basedir.default");
				log.debug("WoWCompanion: setting basedir to '"+baseDirPath+"'");
			}
		} catch (WoWConfigException e) {
			// FIXME - show an error dialog here, then exit
			log.error("WoWUpdater: error loading config", e);
		} catch (WoWConfigPropertyNotFoundException e) {
			// FIXME - show an error dialog here, then exit
			log.error("WoWUpdater: required property not found", e);
		}
	}

	
	/**
	 * constructor
	 */
	public WoWUpdater() {
		log.debug("WoWUpdater: constructor");
	}
	
	public void run() throws WoWUpdaterException {
		log.debug("WoWUpdater: run");

		// get the update URL
		initConfig();
		
		// download the update catalog
		
		// create the http client
        HttpClient client = new HttpClient();

        // set the timeout is specified
        if (timeout != 0) {
            log.debug("WoWUpdater: setting timeout to '"+timeout+"' milliseconds");
            long start = System.currentTimeMillis();
            client.setConnectionTimeout(timeout);
            long end = System.currentTimeMillis();
            if (end - start < timeout) {
                client.setTimeout((int)(end - start));
            } else {
            	client.setTimeout(defaultTimeout);
            }
        }

        // create the http method we will use
        GetMethod method = new GetMethod(updateURL + "/" + catalogFile);
		
        // connect to the upload server, retrying up to the specified num
        int statusCode = -1;
        //for (int attempt = 0; statusCode == -1 && attempt < 3; attempt++) {
            try {
                // execute the get method
                log.debug("WoWUpdater: downloading catalog");
                statusCode = client.executeMethod(method);
            } catch (HttpRecoverableException e) {
                log.debug("WoWUpdater: a recoverable exception occurred, retrying.", e);
                throw new WoWUpdaterException("error: "+e);
            } catch (IOException e) {
                log.debug("WoWUpdater: failed to download data", e);
                throw new WoWUpdaterException("error: "+e);
            }
        //}

        // check that we didn't run out of retries
        if (statusCode != HttpStatus.SC_OK) {
            log.error("WoWUpdater: failed to download update catalog: "+HttpStatus.getStatusText(statusCode));
            throw new WoWUpdaterException("failed to download update catalog");
        } else {
            // read the response body
            byte[] responseBody = method.getResponseBody();

            // release the connection
            method.releaseConnection();

            // we now have the catalog
            //StringReader reader = new StringReader(catalog);
            try {
            	catalogProperties = new Properties();
            	catalogProperties.load(new ByteArrayInputStream(responseBody));
            } catch (IOException e) {
            	log.error("WoWUpdater: failed to read catalog properties", e);
            }
        }
		
		// determine if we have the latest version of each component
        Iterator iter = catalogProperties.keySet().iterator();
        String name = null;
        String version = null;
        String myversion = null;
        String file = null;
        String tmp = null;
        String classname = null;
        HashMap components = new HashMap();
        HashMap componentClasses = new HashMap();
        HashMap componentNames = new HashMap();
        HashMap componentVersions = new HashMap();
        HashMap globalNames = new HashMap();
        HashMap global = new HashMap();
        while (iter.hasNext()) {
        	String key = (String)iter.next();
        	if (key.endsWith(".name") && !key.startsWith("global.")) {
        		// get the component key
        		tmp = key.substring(0, key.indexOf(".name"));
        		log.debug("WoWUpdater: found component key '"+tmp+"', getting version");

        		file = catalogProperties.getProperty(tmp+".file");
        		version = catalogProperties.getProperty(tmp+".version");
        		name = catalogProperties.getProperty(tmp+".name");
        		classname = catalogProperties.getProperty(tmp+".classname");
        		
        		// we save in the prefs "component key=version", e.g. profiler=0.1
        		if (preferences.containsKey("component."+tmp)) {
        			myversion = (String)preferences.get("component."+tmp);
        			log.debug("WoWUpdater: found preference for component '"+name+"' with version '"+myversion+"'");
        			if (!version.equals(myversion)) {
        				// download the new version
        				log.debug("WoWUpdater: adding component '"+name+","+version+","+file+"' for download");
        				components.put(tmp, file);
        				componentClasses.put(tmp, classname);
        				componentNames.put(tmp, name);
        				componentVersions.put(tmp, version);
        			} else {
        				log.debug("WoWUpdater: already have latest '"+name+","+version+","+file+"'");
        			}
        		} else {
    				// download the new version
    				log.debug("WoWUpdater: adding component '"+name+","+version+","+file+"' with key '"+tmp+"' for download");
    				components.put(tmp, file);
    				componentClasses.put(tmp, classname);
    				componentNames.put(tmp, name);
    				componentVersions.put(tmp, version);
        		}
        	} else if (key.startsWith("global.") && key.endsWith(".name")) {
        		// construct a list of global components
        		tmp = key.substring("global.".length(), key.indexOf(".name"));
        		name = catalogProperties.getProperty("global."+tmp+".name");
        		file = catalogProperties.getProperty("global."+tmp+".file");
        		log.debug("WoWUpdater: adding global file '"+file+"' to list");

        		global.put(key, file);
        		globalNames.put(key, name);
        	}

        }
		
		// download newer components
        iter = components.keySet().iterator();
        String key = null;
        while (iter.hasNext()) {
        	key = (String)iter.next();
        	name = (String)componentNames.get(key);
        	file = (String)components.get(key);
        	classname = (String)componentClasses.get(key);
        	version = (String)componentVersions.get(key);
        	log.debug("WoWUpdater: loading '"+name+"="+file+"'");

    		// getting global file URLs
            log.debug("WoWUpdater: adding global files to the classloader");
    		Iterator giter = global.keySet().iterator();
    		URL[] updateURLs = new URL[global.size()+1+3];
    		int count = 0;
    		while (giter.hasNext()) {
    			String k = (String)giter.next();
    			String f = (String)global.get(k);
    			String n = (String)globalNames.get(k);
    			try {
        			log.debug("WoWUpdater: adding URL '" + updateURL + "/" + f + "' to the classloader");
    				updateURLs[count] = new URL(updateURL + "/" + f);
        			count++;
    			} catch (MalformedURLException me) {
    				log.error("WoWUpdater: malformed URL, not adding: "+updateURL + "/" + f);
    			}
    		}

    		// add the arguments
    		String[] arguments = new String[4];
    		arguments[0] = baseDirPath; 
    		arguments[1] = baseDirPath + File.separator + addonPath; 
    		arguments[2] = baseDirPath + File.separator + framexmlPath;
    		arguments[3] = loglevel;

    		// FIXME - this is bad, figure out how to simply add permission 
    		// for the components to do what they need to do
    		System.setSecurityManager(null);
    		
        	ClassLoader savedClassloader = Thread.currentThread().getContextClassLoader();
        	try {
        		// create URLs for our wow paths
        		updateURLs[count++] = new File(arguments[0]).toURL();
        		log.debug("WoWUpdater: adding wow URL: "+ updateURLs[count-1]);
        		updateURLs[count++] = new File(arguments[1]).toURL();
        		log.debug("WoWUpdater: adding wow URL: "+ updateURLs[count-1]);
        		updateURLs[count++] = new File(arguments[2]).toURL();
        		log.debug("WoWUpdater: adding wow URL: "+ updateURLs[count-1]);

        		updateURLs[count] = new URL(updateURL + "/" + file);
        		log.debug("WoWUpdater: adding final URL: "+ updateURL + "/" + file);
        		
        		URLClassLoader loader = new URLClassLoader(updateURLs, WoWUpdater.class.getClassLoader());
        		log.debug("WoWUpdater: done loading '"+name+"'");
        		
        		// run each component (constructor does all the work), passing useful wowc properties
        		// through
        		log.debug("WoWUpdater: loading class '"+classname+"'");
        		
        		Class c = loader.loadClass(classname);
        		
//        		log.debug("WoWUpdater: getting main method");
//        		//Method m = c.getMethod("main", new Class[] { new String[0].getClass() });
//                Class [] mainArgs = new Class[] {String[].class};
//                Method m = c.getMethod("main", mainArgs);
//
//                //log.debug("WoWUpdater: creating new instance of class '"+classname+"'");
//        		//Object o = c.newInstance();

                log.debug("WoWUpdater: invoking execute(String[]) method");
        		
        		//m.invoke(null, arguments);

        		//Thread.currentThread().setContextClassLoader(loader);
        		//AccessControlContext acc = AccessController.getContext();
				invokeClass(c, arguments);
				//Thread.currentThread().setContextClassLoader(savedClassloader);

        		log.debug("WoWUpdater: done running '"+name+"'");
        		
	            // save the new component version to the prefs
	            try {
	                log.info("WoWCompanion: saving "+key+"="+version+" to preferences");
	                config.setPreference("component."+key, version);	
	            	config.savePreferences();
	            } catch (IOException ee) {
	    			log.error("WoWCompanion: couldn't save preferences", ee);
	            }
        	} catch (MalformedURLException e) {
        		log.error("WoWUpdater: error loading jar", e);
        		//throw new WoWUpdaterException("WoWUpdater: error updating "+name+"'");
        		failed.add(name);
        	} catch (ClassNotFoundException e) {
        		log.error("WoWUpdater: class not found", e);
        		//throw new WoWUpdaterException("WoWUpdater: error updating "+name+"'");
        		failed.add(name);
        	//} catch (IllegalAccessException e) {
        	//	log.error("WoWUpdater: illegal access", e);
        	//	throw new WoWUpdaterException("WoWUpdater: error updating "+name+"'");
        	//} catch (InstantiationException e) {
        	//	log.error("WoWUpdater: instantiation", e);
        	//	throw new WoWUpdaterException("WoWUpdater: error updating "+name+"'");
        	} catch (NoSuchMethodException e) {
        		log.error("WoWUpdater: no such method", e);
        		//throw new WoWUpdaterException("WoWUpdater: error updating "+name+"'");
        		failed.add(name);
        	} catch (InvocationTargetException e) {
        		log.error("WoWUpdater: invocation target exception", e);
 
        		//throw new WoWUpdaterException("WoWUpdater: error updating "+name+"'");
        		failed.add(name);
        	}
        }
	}

	public void invokeClass(Class c, String[] args)
			throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException 
	{
		Method m = c.getMethod("execute", new Class[] { args.getClass() });
		m.setAccessible(true);
		int mods = m.getModifiers();
		if (m.getReturnType() != void.class || 
			!Modifier.isStatic(mods) || 
			!Modifier.isPublic(mods)) 
		{
			throw new NoSuchMethodException("execute");
		}
		try {
			m.invoke(null, new Object[] { args });
		} catch (IllegalAccessException e) {
			// this shouldn't happen, as we have disabled access checks
		}
	}
}
