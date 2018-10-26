package com.lukasrosz.revhost.exception;

public class AccessToFileDeniedException extends Exception {
	private static final long serialVersionUID = 1L;

	public AccessToFileDeniedException(String msg) {
		super(msg);
	}
	
	public AccessToFileDeniedException() {
		super("Logged user is not owner of the file and file is not public access");
	}

}
