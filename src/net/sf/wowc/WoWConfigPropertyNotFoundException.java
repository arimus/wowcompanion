package net.sf.wowc;

/**
 * <p>Title: javalib</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author David Castro
 * @version 1.0
 */

public class WoWConfigPropertyNotFoundException extends Exception {
	private String message = "";

	public WoWConfigPropertyNotFoundException() {
		// do nothing
	}
	
	public WoWConfigPropertyNotFoundException(String message) {
		this.message = message;
	}

	public String toString() {
		return this.message;
	}
}
