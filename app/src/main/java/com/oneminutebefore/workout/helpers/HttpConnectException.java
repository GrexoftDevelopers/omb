/*
 * Copyright (C) 2013 Netree
 * 
 * Author Selvakumar
 * 
 */

package com.oneminutebefore.workout.helpers;

public class HttpConnectException extends Exception {

	public static final String MSG_NO_INTERNET = "No internet access";
	public static final String MSG_SESSION_EXPIRED = "session expired";

	private static final long serialVersionUID = 1L;

	private int statusCode;

	HttpConnectException(String message) {
		super(message);
	}

	HttpConnectException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}