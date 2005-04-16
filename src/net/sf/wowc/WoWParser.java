/*
 * Created on Dec 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.wowc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

/**
 * @author arimus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WoWParser {
	private static Logger log = null;
	private static boolean initialized = false;
	
	// FIXME add configuration file

	private static void initConfig() {
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
			
			initialized = true;
		} catch (WoWConfigException e) {
			log.error("WoWCompanion: failed to load configuration", e);
		} catch (WoWConfigPropertyNotFoundException e) {
			// FIXME - show an error dialog here, then exit
			log.error("WoWCompanion: failed to load preferences", e);
		}

	}

	public WoWParser() {
	}

	public static String parse(File savedVarsFile) throws WoWParserException {
		initConfig();
		
        // we have the file, let's get to work!
        try {
            String content = "", line = "";
            FileInputStream fis = new FileInputStream(savedVarsFile);
            byte[] buf = new byte[1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream((int)savedVarsFile.length());
            int len = 1;
            while ((len = fis.read(buf)) != -1) {
            	os.write(buf, 0, len);
            }
            content = os.toString("UTF8");
            
            log.debug("========================================");
            log.debug("content: "+content);
            log.debug("========================================");

            Perl5Util util = new Perl5Util();

            // remove everything up to our profile information
            content = content.substring(content.indexOf("wowcProfile = {"), content.length());
            StringBuffer data = new StringBuffer();
            BufferedReader reader = new BufferedReader(new StringReader(content));
            Stack stack = new Stack();
            boolean done = false;
            int count = 0;

            long start = System.currentTimeMillis();
            while (((line = reader.readLine()) != null) && !done) {
//            	System.out.println("line count is "+ ++count);
//                //line = line.trim();
                data.append(line + "\n"); //.trim();
//                if (util.match("/{/", line)) {
//                    count++;
//                }
//                if (util.match("/}/", line)) {
//                    count--;
//                }
//                if (count == 0) {
//                    done = true;
//                }
                if (util.match("/^}$/", line)) {
                	done = true;
                }
            }
            long end = System.currentTimeMillis();
            log.debug("WoWParser: parsing took "+(end-start)+" milliseconds");

            return data.toString();
        } catch (IOException e) {
            log.error("WoWParser: error reading file: ", e);
        } catch (Exception ee) {
            log.error("WoWParser: error", ee);
        }

		throw new WoWParserException();
	}
	
	public static void main(String []args) {
		//WoWParser parser = new WoWParser();
		try {
			String data = WoWParser.parse(new File("SavedVariables.lua"));
			// FIXME - send the data to the server
			System.out.println(data);
			WoWUploader.upload(data, "arimus", "arimus");
		} catch (WoWUploaderConnectException e) {
			log.error("WoWParser: uploading exception", e);
		} catch (InvalidUserPassException e) {
			log.error("WoWParser: invalid user/pass");
		} catch (WoWParserException e) {
			log.error("WoWParser: parser exception", e);
		} catch (WoWUploaderException e) {
			log.error("WoWParser: uploading exception", e);
		}
	}
}
