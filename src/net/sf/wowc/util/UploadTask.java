package net.sf.wowc.util;

import java.io.File;
import java.io.FileNotFoundException;

import net.sf.wowc.InvalidUserPassException;
import net.sf.wowc.WoWParser;
import net.sf.wowc.WoWParserException;
import net.sf.wowc.WoWUploader;
import net.sf.wowc.WoWUploaderConnectException;
import net.sf.wowc.WoWUploaderException;

/** Uses a SwingWorker to perform a time-consuming (and utterly fake) task. */

public class UploadTask {
    private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statusMessage;
    private ActualTask task;
	private boolean hasErrors;
	private String errorMessage;
	private String[] args = new String[0];

    public UploadTask(String[] args) {
    	this.args = args;
        //Compute length of task...
        //In a real program, this would figure out
        //the number of bytes to read or whatever.
        lengthOfTask = 1000;
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    public void go() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                statusMessage = null;
                task = new ActualTask();
                return task;
            }
        };
        worker.start();
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() {
        return current;
    }

    public void stop() {
        canceled = true;
        statusMessage = null;
    }

    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    public boolean isDone() {
        return done;
    }
    
    public boolean hasErrors() {
    	return hasErrors;
    }
    
    public String getErrorMessage() {
    	return errorMessage;
    }

    /**
     * Returns the most recent status message, or null
     * if there is no current status message.
     */
    public String getMessage() {
        return statusMessage;
    }

    /**
     * The actual long running task.  This runs in a SwingWorker thread.
     */
    class ActualTask {
    	private String savedVarsFilename, data, username, password;
    	
        ActualTask() {
        	this.savedVarsFilename = args[0];
        	this.username = args[1];
        	this.password = args[2];
        	
        	try {
        		// parse the data
        		statusMessage = "opening data file";
                File f = new File(savedVarsFilename);
    			if (!f.isFile()) {
    				throw new FileNotFoundException("failed to open '"+f.getAbsolutePath()+ "'");
    			}
        		statusMessage = "parsing character data";
    			this.data = WoWParser.parse(f);
    			
    			//System.out.println("UploadTask: data = "+data);

    			// send the data here
    			System.out.println("UploadTask: uploading");
        		statusMessage = "uploading character data";
        		WoWUploader.upload(data, username, password);
        	} catch (FileNotFoundException e) {
        		hasErrors = true;
        		errorMessage = "Failed to open data file"; //+e;
        		e.printStackTrace(System.err);
        	} catch (WoWParserException e) {
        		hasErrors = true;
        		errorMessage = "Error parsing character data"; //+e;
        		e.printStackTrace(System.err);
        	} catch (WoWUploaderException e) {
        		hasErrors = true;
        		errorMessage = "Error uploading data"; //+e;
        		e.printStackTrace(System.err);
        	} catch (WoWUploaderConnectException e) {
        		hasErrors = true;
        		errorMessage = "Error connecting to server"; //+e;
        		e.printStackTrace(System.err);
        	} catch (InvalidUserPassException e) {
        		hasErrors = true;
        		errorMessage = "Invalid user/pass";
        		e.printStackTrace(System.err);
//        	} catch (PermissionDeniedException e) {
//        		hasErrors = true;
//        		this.error = e;
        	} catch (Exception e) {
        		hasErrors = true;
        		errorMessage = "Error";
        		e.printStackTrace(System.err);
        	}
        	done = true;
        }
    }
}
