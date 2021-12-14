package com.cognixia.jump.exception;

public class NegativeBalanceException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public NegativeBalanceException(int balance) {
		super("The balance given of $" + balance + " is negative, cannot be used");
	}

}
