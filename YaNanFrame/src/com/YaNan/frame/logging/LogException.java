package com.YaNan.frame.logging;

public class LogException extends Exception {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1789149461642243879L;

	public LogException() {
	    super();
	  }

	  public LogException(String message) {
	    super(message);
	  }

	  public LogException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public LogException(Throwable cause) {
	    super(cause);
	  }

}
