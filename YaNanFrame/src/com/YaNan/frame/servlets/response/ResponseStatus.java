package com.YaNan.frame.servlets.response;


public class ResponseStatus {
	private int status;
	private String message;
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public ResponseStatus(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public ResponseStatus(int status) {
		this.status = status;
	}

	public static ResponseStatus response(int status,String message){
		return new ResponseStatus(status,message);
	}

	public static ResponseStatus response(int status) {
		return new ResponseStatus(status);
	}
	public static ResponseStatus ok(){
		return new ResponseStatus(200);
	}
	public static ResponseStatus ok(String message){
		return new ResponseStatus(200,message);
	}

	public static ResponseStatus not_found() {
		return new ResponseStatus(404);
	}
	
	public static ResponseStatus not_found(String message) {
		return new ResponseStatus(404,message);
	}
}
