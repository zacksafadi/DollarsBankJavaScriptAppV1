package com.cognixia.jump.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class SameRoleException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public SameRoleException(String role, String action) {
		super("User already has role: " + role + ", cannot be " + action + ".");
	}

}
