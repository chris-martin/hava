package edu.gatech.hava.engine;

import edu.gatech.hava.engine.exception.ExceptionEnvironment;

public class HAbortException extends HException {

	private static final long serialVersionUID = 1L;

	public HAbortException(final String message,
			               final ExceptionEnvironment exEnv) {

		super(message, exEnv);

	}

}
