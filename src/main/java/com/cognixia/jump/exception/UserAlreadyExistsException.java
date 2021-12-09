package com.cognixia.jump.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public UserAlreadyExistsException(String username) {
		super(username + " already exists.");
	}
	
	
	
}
