package com.android.yttmarket.utils;

public class WSError extends Throwable {

	//自定义的异常类
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;

	public WSError() {

	}

	public WSError(String message) {
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessge() {
		return message;
	}
}
