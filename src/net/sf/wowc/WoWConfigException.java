package net.sf.wowc;

/**
 * <p>Title: javalib</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author David Castro
 * @version 1.0
 */

public class WoWConfigException extends Exception {
	private String message = "";

	public WoWConfigException() {
		// do nothing
	}
	
	public WoWConfigException(String message) {
		this.message = message;
	}

	public String toString() {
		return this.message;
	}
}
