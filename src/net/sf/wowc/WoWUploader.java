/*
 * Created on Dec 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.wowc;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import net.sf.wowc.util.GZIP;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WoWUploader {
	private static Logger log = null; 

	private static String serverURL = "";
	private static String username = "";
	private static String password = "";

	private static String SUCCESS = "SUCCESS";
    private static String FAILURE = "FAILURE";
    private static String DUPLICATE_USER = "DUPLICATE_USER";
    private static String ERROR_USER_PASS = "ERROR_USER_PASS";
    private static String ACTION_CREATE_ACCOUNT = "CREATE_ACCOUNT";
    private static String ACTION_UPDATE_ACCOUNT = "UPDATE_ACCOUNT";
    private static String ACTION_UPLOAD_DATA = "UPLOAD_DATA";

	private static int defaultTimeout = 10000;
	private static int timeout = defaultTimeout;
	// FIXME - configure this in a config file
	
	private static boolean initialized = false;

	private static void initLog() {
		try {
			WoWConfig config = new WoWConfig();
			Map m = config.getPreferences();
			
			log = config.getLogger();

			if (m.containsKey("loglevel")) {
				// set the level to debug if needed
				String loglevel = config.getPreference("loglevel");
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

	public WoWUploader() {
	}
	
	private static void initConfig(){
		//if (!initialized) {
			initialized = true;
			try {
				log.debug("WoWUploader: initializing config");
				WoWConfig config = new WoWConfig();
				
				try {
					serverURL = config.getPreference("server.url");
				} catch (WoWConfigPropertyNotFoundException e) {
					// nothing
					log.debug("WoWUploader: no server.url found, using default");
				}
				
				if (serverURL.equals("")) {
					serverURL = config.getProperty("server.url.default");
				}
				log.debug("WoWUploader: using server URL: '"+serverURL+"'");
			} catch (WoWConfigException e) {
				// FIXME - show an error dialog here, then exit
				log.error("WoWUploader: error loading config");
			} catch (WoWConfigPropertyNotFoundException e) {
				// FIXME - show an error dialog here, then exit
				log.error("WoWUploader: server URL property not found");
			}
		//}
	}
	
	public static void createAccount(String realname, String email, String username, String password) 
		throws WoWUploaderException, WoWUploaderConnectException, 
		InvalidUsernameException, DuplicateUsernameException
	{
	    initLog();
	    log.debug("WoWUploader: createAccount()");
	    initConfig();
		
        if ((username == null) || username.equals("") ||
    		(password == null) || password.equals(""))
    	{
           	throw new WoWUploaderException("invalid username/password");
    	}
    		
    	// post the XML data to the server via HTTP post

		// create the http client
        HttpClient client = new HttpClient();

        // set the timeout is specified
        if (timeout != 0) {
            log.debug("WoWUploader: setting timeout to '"+timeout+"' milliseconds");
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
        MultipartPostMethod method = new MultipartPostMethod(serverURL);
		StringPart actionPart = new StringPart("action", ACTION_CREATE_ACCOUNT);
		StringPart realnamePart = new StringPart("realname", realname);
		StringPart userPart = new StringPart("username", username);
		StringPart passPart = new StringPart("password", password);
		StringPart emailPart = new StringPart("email", password);
		method.addPart(actionPart);
		method.addPart(realnamePart);
		method.addPart(userPart);
		method.addPart(passPart);
		method.addPart(emailPart);
		
        // connect to the upload server, retrying up to the specified num
        int statusCode = -1;
        //for (int attempt = 0; statusCode == -1 && attempt < 3; attempt++) {
            try {
                // execute the get method
                log.debug("WoWUploader: creating account");
                statusCode = client.executeMethod(method);
            } catch (HttpRecoverableException e) {
                log.debug("WoWUploader: a recoverable exception occurred, retrying.", e);
                throw new WoWUploaderConnectException("error: "+e);
            } catch (IOException e) {
                log.debug("WoWUploader: failed to upload data: ", e);
                throw new WoWUploaderConnectException("error: "+e);
            }
        //}

        // check that we didn't run out of retries
        if (statusCode != HttpStatus.SC_OK) {
            log.error("WoWUploader: failed to create account: "+HttpStatus.getStatusText(statusCode));
            throw new WoWUploaderException("failed to create account");
        } else {
            // read the response body
            byte[] responseBody = method.getResponseBody();

            // release the connection
            method.releaseConnection();

            // deal with the response.
            // FIXME - ensure we use the correct character encoding here
            String response = new String(responseBody);
            log.debug("WoWUploader: response was: "+response);
            
            if (response.equals(FAILURE)) {
            	log.error("WoWUploader: failed to create account");
            	throw new WoWUploaderException("error");
            } else if (response.equals(DUPLICATE_USER)) {
            	log.error("WoWUploader: duplicate user, not creating account");
            	throw new DuplicateUsernameException();
            } else if (response.equals(SUCCESS)) {
            	log.debug("WoWUploader: account created");
            	// do nothing
            } else {
            	log.error("WoWUploader: unknown response from server");
            	throw new WoWUploaderException("unknown response from server");
            }
        }
	}
	
	public static void updateAccount(String realname, String email, String username, String oldpassword, String password) 
		throws WoWUploaderException, WoWUploaderConnectException, 
		InvalidUsernameException, InvalidUserPassException
	{
	    initLog();
	    log.debug("WoWUploader: updateAccount()");
	    initConfig();
		
        if ((username == null) || username.equals("") ||
    		(password == null) || password.equals(""))
    	{
           	throw new WoWUploaderException("invalid username/password");
    	}
    		
        // username and oldpassword are current information, everything else will be updated
        
        // create the http client
        HttpClient client = new HttpClient();

        // set the timeout is specified
        if (timeout != 0) {
            log.debug("WoWUploader: setting timeout to '"+timeout+"' milliseconds");
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
        MultipartPostMethod method = new MultipartPostMethod(serverURL);
		StringPart actionPart = new StringPart("action", ACTION_UPDATE_ACCOUNT);
		StringPart realnamePart = new StringPart("realname", realname);
		StringPart userPart = new StringPart("username", username);
		StringPart passPart = new StringPart("password", password);
		StringPart emailPart = new StringPart("email", password);
		method.addPart(actionPart);
		method.addPart(realnamePart);
		method.addPart(userPart);
		method.addPart(passPart);
		method.addPart(emailPart);
		
        // connect to the upload server, retrying up to the specified num
        int statusCode = -1;
        //for (int attempt = 0; statusCode == -1 && attempt < 3; attempt++) {
            try {
                // execute the get method
                log.debug("WoWUploader: updating account");
                statusCode = client.executeMethod(method);
            } catch (HttpRecoverableException e) {
                log.debug("WoWUploader: a recoverable exception occurred, retrying.", e);
                throw new WoWUploaderConnectException("error: "+e);
            } catch (IOException e) {
                log.debug("WoWUploader: failed to upload data: ", e);
                throw new WoWUploaderConnectException("error: "+e);
            }
        //}

        // check that we didn't run out of retries
        if (statusCode != HttpStatus.SC_OK) {
            log.error("WoWUploader: failed to update account: "+HttpStatus.getStatusText(statusCode));
            throw new WoWUploaderException("failed to create account");
        } else {
            // read the response body
            byte[] responseBody = method.getResponseBody();

            // release the connection
            method.releaseConnection();

            // deal with the response.
            // FIXME - ensure we use the correct character encoding here
            String response = new String(responseBody);
            log.debug("WoWUploader: response was: "+response);
            
            if (response.equals(FAILURE)) {
            	log.error("WoWUploader: failed to create account");
            	throw new WoWUploaderException("error");
            } else if (response.equals(ERROR_USER_PASS)) {
               	log.error("WoWUpload: invalid user/pass");
               	throw new InvalidUserPassException();
            } else if (response.equals(SUCCESS)) {
            	log.debug("WoWUploader: account created");
            	// do nothing
            } else {
            	log.error("WoWUploader: unknown response from server");
            	throw new WoWUploaderException("unknown response from server");
            }
        }
	}
	
	public static void upload(String data, String username, String password) 
		throws WoWUploaderException, InvalidUserPassException, WoWUploaderConnectException 
	{
		initLog();
		
		log.debug("WoWUpload: upload()");
		long istart = System.currentTimeMillis();
		
		initConfig();
		
        if ((username == null) || username.equals("") &&
			(password == null) && password.equals(""))
		{
        	throw new WoWUploaderException("invalid username/password");
		}
		
        // FIXME - check to see if this is already the latest data
        
		// post the XML data to the server via HTTP post

		// create the http client
        HttpClient client = new HttpClient();

        // set the timeout is specified
        if (timeout != 0) {
            log.debug("WoWUpload: setting timeout to '"+timeout+"' milliseconds");
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
        MultipartPostMethod method = new MultipartPostMethod(serverURL);
		StringPart actionPart = new StringPart("action", ACTION_UPLOAD_DATA);
		//StringPart dataPart = new StringPart("data", data);
		byte[] gziped = null;
		
		try {
			long st = System.currentTimeMillis();
			gziped = GZIP.gzipToBytes(data.getBytes());
			long en = System.currentTimeMillis();
			log.debug("WoWUpload: took "+(en-st)+" milliseconds to gzip");
		} catch (Exception ge) {
			// hrmm, failed to gzip
			throw new WoWUploaderException("failed to compress data");
		}
		
		ByteArrayPartSource dataPartSource = new ByteArrayPartSource("data", gziped);
		FilePart dataPart = new FilePart("data", dataPartSource);
		StringPart userPart = new StringPart("username", username);
		StringPart passPart = new StringPart("password", password);
		method.addPart(actionPart);
		method.addPart(dataPart);
		method.addPart(userPart);
		method.addPart(passPart);
		
        // connect to the upload server, retrying up to the specified num
        int statusCode = -1;
        //for (int attempt = 0; statusCode == -1 && attempt < 3; attempt++) {
            try {
                // execute the get method
                log.debug("WoWUpload: uploading data");
                statusCode = client.executeMethod(method);
            } catch (HttpRecoverableException e) {
                log.debug("WoWUpload: a recoverable exception occurred, retrying.", e);
                throw new WoWUploaderConnectException("error: "+e);
            } catch (IOException e) {
                log.debug("WoWUpload: failed to upload data", e);
                throw new WoWUploaderConnectException("error: "+e);
            }
        //}

        // check that we didn't run out of retries
        if (statusCode != HttpStatus.SC_OK) {
            log.error("WoWUpload: failed to upload data: "+HttpStatus.getStatusText(statusCode));
            throw new WoWUploaderException("failed to upload data");
        } else {
            // read the response body
            byte[] responseBody = method.getResponseBody();

            // release the connection
            method.releaseConnection();

            // deal with the response.
            // FIXME - ensure we use the correct character encoding here
            String response = new String(responseBody);
            log.debug("WoWUpload: response was: "+response);
            
            if (response.equals(ERROR_USER_PASS)) {
            	log.error("WoWUpload: invalid user/pass");
            	throw new InvalidUserPassException();
            } else if (response.equals(FAILURE)) {
            	log.error("WoWUpload: failed to upload");
            	throw new WoWUploaderException("error");
            } else if (response.equals(SUCCESS)) {
            	log.debug("WoWUpload: uploaded successfully");
            	// do nothing
            } else {
            	log.error("WoWUpload: unknown response from server");
            	throw new WoWUploaderException("unknown response from server");
            }
        }

        long iend = System.currentTimeMillis();
    	log.debug("WoWUpload: upload took "+(iend-istart)+" milliseconds");
	}
}
