package com.cognixia.jump.model;

public class AuthenticationResponse {
	
	private final String JWT;
	
	public AuthenticationResponse() {
		this.JWT = null;
	}
	
	public AuthenticationResponse(String JWT) {
		this.JWT = JWT;
	}
	
	public String getJWT() {
		return JWT;
	}

}
