/*
 * Created on Dec 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.wowc;

import java.io.*;
import java.util.*;
import org.apache.oro.text.perl.*;

/**
 * @author arimus
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WoWParser {
	// FIXME add configuration file
	
	public WoWParser() {
	}

	public static String parse(File savedVarsFile) throws WoWParserException {
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
            content = os.toString("ASCII");
            ////System.out.println("content: "+content);

            Perl5Util util = new Perl5Util();

            // remove everything up to our profile information
            content = content.substring(content.indexOf("myProfile = {"), content.length());
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
            System.out.println("parsing took "+(end-start)+" milliseconds");

            return data.toString();
        } catch (IOException e) {
            System.err.println("error reading file: "+e);
        } catch (Exception ee) {
            ee.printStackTrace(System.err);
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
			System.err.println(e);
		} catch (InvalidUserPassException e) {
			System.err.println(e);
		} catch (WoWParserException e) {
			System.err.println(e);
		} catch (WoWUploaderException e) {
			System.err.println("error: "+e);
		}
	}
}
